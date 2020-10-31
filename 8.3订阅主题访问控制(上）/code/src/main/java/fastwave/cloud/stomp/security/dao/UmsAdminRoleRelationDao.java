package fastwave.cloud.stomp.security.dao;

import fastwave.cloud.stomp.security.entity.UmsAdminRoleRelation;
import fastwave.cloud.stomp.security.entity.UmsResource;
import fastwave.cloud.stomp.security.entity.UmsRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 自定义后台用户与角色管理Dao
 */
public interface UmsAdminRoleRelationDao {
    /**
     * 批量插入用户角色关系
     */
    int insertList(@Param("list") List<UmsAdminRoleRelation> adminRoleRelationList);

    /**
     * 获取用于所有角色
     */
    List<UmsRole> getRoleList(@Param("adminId") Long adminId);

    /**
     * 获取用户所有可访问资源
     *
     */
    List<UmsResource> getResourceList(@Param("adminId") Long adminId);

    /**
     * 获取资源相关用户ID列表
     */
    List<Long> getAdminIdList(@Param("resourceId") Long resourceId);
}
