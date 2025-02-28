package com.levon.framework.mapper;

import com.levon.framework.domain.entry.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author leivik
* @description 针对表【sys_user(用户表)】的数据库操作Mapper
* @createDate 2025-02-22 09:53:58
* @Entity com.levon.framework.domain.entry.SysUser
*/
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

}




