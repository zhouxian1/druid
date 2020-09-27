package com.hjj.common.security;

/**
 * Created by root on 2016/6/24 0024.
 */


import com.dbs.ep.sys.domain.Permission;
import com.dbs.ep.sys.domain.Role;
import com.dbs.ep.sys.domain.User;
import com.dbs.ep.sys.service.PermissionService;
import com.dbs.ep.sys.service.RoleService;
import com.dbs.ep.sys.service.UserService;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cas.CasRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;

import java.net.URLDecoder;
import java.util.List;

/**
 * 扩展CAS桥接器,订制角色体系和资源体系
 *
 * @author shadow
 */
public class SimpleCasRealm extends CasRealm {

    private final Logger logger = Logger.getLogger(this.getClass());

    private UserService userService;
    private RoleService roleService;
    private PermissionService permissionService;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        try {
            SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
            //获取名称后，进行UTF-8转码
            String casUserName = String.valueOf(principals.getPrimaryPrincipal());
        	casUserName = URLDecoder.decode(casUserName, "UTF-8");
            final User user = userService.selectByUsername(casUserName);
            final List<Role> roleInfos = roleService.selectRolesByUserId(user.getId());
            for (Role role : roleInfos) {
                // 添加角色
                authorizationInfo.addRole(role.getRoleSign());

                final List<Permission> permissions = permissionService.selectPermissionsByRoleId(role.getId());
                for (Permission permission : permissions) {
//                 添加权限
                    authorizationInfo.addStringPermission(permission.getSign());
                }
            }
            return authorizationInfo;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }
 
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    protected void clearCachedAuthenticationInfo(String principal){
        SimplePrincipalCollection principals = new SimplePrincipalCollection(principal,getName());
        clearCachedAuthenticationInfo(principals);
    }

    public void clearAllCachedAuthorizationInfo(){
        Cache<Object,AuthorizationInfo> cache = getAuthorizationCache();
        if (cache != null){
            for (Object key : cache.keys()){
                cache.remove(key);
            }
        }
    }

}
