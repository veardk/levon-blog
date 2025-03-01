package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.common.constants.MenuConstants;
import com.levon.framework.common.enums.AppHttpCodeEnum;
import com.levon.framework.common.exception.SystemException;
import com.levon.framework.common.util.BeanCopyUtils;
import com.levon.framework.common.util.SecurityUtils;
import com.levon.framework.domain.entry.SysMenu;
import com.levon.framework.domain.vo.LeBlogAdminMenuRouterVO;
import com.levon.framework.domain.vo.LeBlogAdminUserInfoVO;
import com.levon.framework.domain.vo.MenuVo;
import com.levon.framework.domain.vo.UserInfoVO;
import com.levon.framework.mapper.SysRoleMapper;
import com.levon.framework.service.SysMenuService;
import com.levon.framework.mapper.SysMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * @author leivik
 * @description 针对表【sys_menu(菜单权限表)】的数据库操作Service实现
 * @createDate 2025-02-28 19:32:18
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu>
        implements SysMenuService {

    @Autowired
    private SysMenuMapper sysMenuMapper;

    @Autowired
    private SysRoleMapper sysRoleMapper;

    /**
     * 直接获取超级管理员权限角色信息
     *
     * @return
     */
    @Override
    public LeBlogAdminUserInfoVO getAdminInfo() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SysMenu::getMenuType, MenuConstants.MENU_TYPE_MENU, MenuConstants.MENU_TYPE_BUTTON)
                .eq(SysMenu::getStatus, MenuConstants.MENU_STATUS_NORMAL);

        // 超级管理员默认拥有所有权限
        List<String> permList = sysMenuMapper.selectList(wrapper).stream()
                .map(SysMenu::getPerms)
                .collect(Collectors.toList());

        // 超级管理员 role key 为 admin
        List<String> role = new ArrayList<>();
        role.add("admin");

        // 超级管理员信息
        UserInfoVO userInfo = BeanCopyUtils.copyBean(SecurityUtils.getLoginUser().getUser(), UserInfoVO.class);
        return new LeBlogAdminUserInfoVO(permList, role, userInfo);
    }

    /**
     * 获取管理员权限与角色信息
     *
     * @return
     */
    @Override
    public LeBlogAdminUserInfoVO getOtherAdminInfo() {
        Long userId = SecurityUtils.getUserId();

        // 查询管理员权限信息
        List<String> permsList = sysMenuMapper.getPermsByUserId(userId);
        if (permsList == null || permsList.isEmpty()) {
            throw new SystemException(AppHttpCodeEnum.NO_ADMIN_PERMISSIONS);
        }

        // 查询管理员角色
        List<String> roleKeyList = sysRoleMapper.getRoleKeyByUserId(userId);
        if (roleKeyList == null || roleKeyList.isEmpty()) {
            throw new SystemException(AppHttpCodeEnum.NO_ADMIN_PERMISSIONS);
        }
        // 封装返回
        UserInfoVO userInfo = BeanCopyUtils.copyBean(SecurityUtils.getLoginUser().getUser(), UserInfoVO.class);
        return new LeBlogAdminUserInfoVO(permsList, roleKeyList, userInfo);
    }

    /**
     * 获取所有菜单路由信息
     *
     * @return LeBlogAdminMenuRouterVO 返回封装的菜单路由
     */
    @Override
    public LeBlogAdminMenuRouterVO selectAllMenuRouter() {
        List<MenuVo> menuRouterList = sysMenuMapper.selectAllMenuRouter();
        if (menuRouterList == null && menuRouterList.isEmpty()) {
            throw new SystemException(AppHttpCodeEnum.ADMIN_MENU_NOT_FOUND);
        }

        // 转换为树形结构
        List<MenuVo> menuTree = buildMenuTree(menuRouterList);

        return new LeBlogAdminMenuRouterVO(menuTree);
    }

    /**
     * 根据用户 ID 获取该用户的菜单路由信息
     *
     * @param userId 用户id
     * @return 返回封装用户的菜单路由
     */
    @Override
    public LeBlogAdminMenuRouterVO selectMenuRouterTreeByUserId(Long userId) {
        List<MenuVo> menuRouterList = sysMenuMapper.selectMenuRouterTreeByUserId(userId);

        if (menuRouterList == null && menuRouterList.isEmpty()) {
            throw new SystemException(AppHttpCodeEnum.ADMIN_MENU_NOT_FOUND);
        }

        // 转换为树形结构
        List<MenuVo> menuTree = buildMenuTree(menuRouterList);

        return new LeBlogAdminMenuRouterVO(menuTree);
    }

    /**
     * 将菜单列表转换为树形结构
     *
     * @param menuList 菜单列表
     * @return 树形结构的菜单列表
     */
    private List<MenuVo> buildMenuTree(List<MenuVo> menuList) {
        Map<Long, MenuVo> menuMap = new HashMap<>();
        List<MenuVo> rootMenus = new ArrayList<>();

        // 并将menu放入map中,key为 menu.id
        menuList.forEach(menu -> {
            menu.setChildren(new ArrayList<>());
            menuMap.put(menu.getId(), menu);
        });

        // 根据 menuType 判断，如果是 `C` 类型的菜单，则是根菜单
        menuList.forEach(menu -> {
            if (MenuConstants.MENU_TYPE_MENU.equals(menu.getMenuType())) {
                rootMenus.add(menu);
            } else {
                // 子菜单，加入父级菜单的 children 列表
                MenuVo parent = menuMap.get(menu.getParentId());
                if (parent != null) {
                    // 将子菜单加入到父菜单的 children 列表
                    parent.getChildren().add(menu);
                }
            }
        });

        return rootMenus;
    }


}