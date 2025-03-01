package com.levon.framework.mapper;

import com.levon.framework.domain.entry.SysRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author leivik
 * @description 针对表【sys_role(角色信息表)】的数据库操作Mapper
 * @createDate 2025-02-28 19:32:31
 * @Entity com.levon.framework.domain.entry.SysRole
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据userId获取role角色列表
     *
     * @param userId
     * @return
     */
    List<String> getRoleKeyByUserId(@Param("userId") Long userId);
}




