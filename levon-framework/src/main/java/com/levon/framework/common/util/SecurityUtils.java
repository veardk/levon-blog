package com.levon.framework.common.util;

import com.levon.framework.domain.entry.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * 获取用户
     * @return LoginUser
     * @throws IllegalStateException 如果未登录或用户信息格式不正确
     */
    public static LoginUser getLoginUser() {
        Authentication authentication = getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("用户未登录");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof LoginUser) {
            return (LoginUser) principal;
        } else {
            throw new IllegalStateException("当前用户信息格式不正确");
        }
    }

    /**
     * 获取Authentication
     * @return Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 判断当前用户是否是管理员
     * @return true 如果是管理员，false 否则
     */
    public static Boolean isAdmin() {
        Long id = getLoginUser().getUser().getId();
        return id != null && 1L == id;
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    public static Long getUserId() {
        return getLoginUser().getUser().getId();
    }
}