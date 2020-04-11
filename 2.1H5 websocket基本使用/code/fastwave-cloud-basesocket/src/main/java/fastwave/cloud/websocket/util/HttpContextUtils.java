package fastwave.cloud.websocket.util;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class HttpContextUtils {
	public static HttpServletRequest getHttpServletRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}
	
	public static HashMap<String, String> parseQueryString(String queryString)
    {
		HashMap<String, String> maps = new HashMap<String, String>();
		
		if(queryString == null || queryString == "")
		{
			return maps;
		}
		
		String[] kvs = queryString.split("&");
		String[] kvItem;
		for(String item : kvs)
		{
			kvItem = item.split("=");
			if(kvItem != null && kvItem.length == 2)
			{
				maps.put(kvItem[0], kvItem[1]);
			}
		}
		
		return maps;
    }

}
