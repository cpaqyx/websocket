package fastwave.cloud.stomp.webstocket;


import cn.hutool.core.collection.CollUtil;
import fastwave.cloud.stomp.security.component.DynamicSecurityMetadataSource;
import fastwave.cloud.stomp.security.config.AdminUserDetails;
import fastwave.cloud.stomp.security.entity.UmsAdmin;
import fastwave.cloud.stomp.security.entity.UmsResource;
import fastwave.cloud.stomp.security.services.UmsAdminService;
import fastwave.cloud.stomp.security.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 功能:频道拦截器,类似管道,获取消息的一些meta数据
 */
@Component
public class SocketChanelInterceptor implements ChannelInterceptor {

    @Autowired
    public JwtTokenUtil jwtTokenUtil;

    @Autowired
    public UserDetailsService userDetailsService;

    @Value("${jwt.tokenHead}")
    public String tokenHead;

    @Autowired
    UmsAdminService umsAdminService;

    @Autowired
    DynamicSecurityMetadataSource dynamicSecurityMetadataSource;

    /**
     * 实际消息发送到频道之前调用
     */
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String jwtToken = accessor.getFirstNativeHeader("token");
            if(jwtToken == null || jwtToken.length() < tokenHead.length())
            {
                throw new IllegalArgumentException("抱歉，您没有访问权限");
            }
            jwtToken = jwtToken.substring(tokenHead.length());
            String username = jwtTokenUtil.getUserNameFromToken(jwtToken);
            if (username != null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
                if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    accessor.setUser(authentication);
                }
            }
            else
            {
                throw new IllegalArgumentException("抱歉，您没有访问权限");
            }
        }
        else if(StompCommand.SUBSCRIBE.equals(accessor.getCommand()))
        {
            String topic = accessor.getDestination().toString();
            Authentication authentication = (Authentication)accessor.getUser();
            System.out.println(topic);
            boolean result = checkSubscribe(authentication, topic);

            if(!result)
            {
                // 这里返回一个自定义提示消息，由于没有指令和相关参数，所以不会真正执行订阅
                Message<String> msg = new Message<String>() {
                    @Override
                    public String getPayload() {
                        return "主题订阅失败";
                    }

                    @Override
                    public MessageHeaders getHeaders() {
                        return null;
                    }
                };
                return msg;
                // 如果抛出异常，则会导致前端面页连接也会断开，不断重连
                //throw new IllegalArgumentException("抱歉，没有权限订阅该主题");
            }
        }

        return message;
    }

    /**
     * 验证当前用户是否有权限订阅主题
     */
    private boolean checkSubscribe(Authentication authentication, String topic) {

        Collection<ConfigAttribute> configAttributes = dynamicSecurityMetadataSource.getAttributes(topic);
        if (CollUtil.isEmpty(configAttributes)) {
            return false;
        }
        Iterator<ConfigAttribute> iterator = configAttributes.iterator();
        while (iterator.hasNext()) {
            ConfigAttribute configAttribute = iterator.next();
            //将访问所需资源或用户拥有资源进行比对
            String needAuthority = configAttribute.getAttribute();
            for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
                if (needAuthority.trim().equals(grantedAuthority.getAuthority())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 消息发送立即调用
     */
    @Override
    public void postSend(Message<?> message, MessageChannel channel, boolean sent) {
    }

    /**
     * 消息发送之后进行调用,是否有异常,进行数据清理
     */
    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, @Nullable Exception ex) {
        return;
    }
}


