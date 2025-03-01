package com.levon.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.levon.framework.domain.entry.SysRole;
import com.levon.framework.service.SysRoleService;
import com.levon.framework.mapper.SysRoleMapper;
import org.springframework.stereotype.Service;

/**
* @author leivik
* @description 针对表【sys_role(角色信息表)】的数据库操作Service实现
* @createDate 2025-02-28 19:32:31
*/
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole>
    implements SysRoleService{

}




