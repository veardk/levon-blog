package com.levon.framework.mapper;

import com.levon.framework.domain.entry.SysMenu;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.levon.framework.domain.vo.AdminMenuVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author leivik
 * @description 针对表【sys_menu(菜单权限表)】的数据库操作Mapper
 * @createDate 2025-02-28 19:32:18
 * @Entity com.levon.framework.domain.entry.SysMenu
 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    /**
     * 根据管理员ID获取menu管理员权限
     *
     * @param userId 管理员ID
     * @return
     */
    List<String> getPermsByUserId(@Param("userId") Long userId);

    /**
     * 获取所有菜单路由项。
     *
     * @return 包含所有菜单路由的列表。
     */
    List<AdminMenuVO> selectAllMenuRouter();

    /**
     * 根据管理员ID获取菜单路由项。
     *
     * @param userId 管理员ID
     * @return 该用户可访问的菜单树形列表
     */
    List<AdminMenuVO> selectMenuRouterTreeByUserId(@Param("userId") Long userId);
}




