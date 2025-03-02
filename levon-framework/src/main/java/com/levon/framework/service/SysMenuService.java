package com.levon.framework.service;

import com.levon.framework.domain.entry.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.levon.framework.domain.vo.AdminMenuRouterVO;
import com.levon.framework.domain.vo.AdminUserInfoVO;

/**
 * @author leivik
 * @description 针对表【sys_menu(菜单权限表)】的数据库操作Service
 * @createDate 2025-02-28 19:32:18
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 直接获取超级管理员权限角色信息
     *
     * @return
     */
    AdminUserInfoVO getAdminInfo();

    /**
     * 获取管理员权限信息
     *
     * @return
     */
    AdminUserInfoVO getOtherAdminInfo();

    /**
     * 获取所有菜单路由信息
     *
     * @return LeBlogAdminMenuRouterVO 返回封装的菜单路由
     */
    AdminMenuRouterVO selectAllMenuRouter();

    /**
     * 根据用户 ID 获取该用户的菜单路由信息
     *
     * @param userId 用户id
     * @return 返回封装用户的菜单路由
     */
    AdminMenuRouterVO selectMenuRouterTreeByUserId(Long userId);
}
