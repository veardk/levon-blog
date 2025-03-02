package com.levon.framework.common.enums;

public enum AppHttpCodeEnum {
    // 成功
    SUCCESS(200, "操作成功"),
    // 登录
    NEED_LOGIN(401, "需要登录后操作"),
    NO_OPERATOR_AUTH(403, "无权限操作"),
    SYSTEM_ERROR(500, "出现错误"),
    USERNAME_EXIST(501, "用户名已存在"),
    PHONE_NUMBER_EXIST(502, "手机号已存在"),
    EMAIL_EXIST(503, "邮箱已存在"),
    REQUIRE_USERNAME(504, "必需填写用户名"),
    LOGIN_ERROR(505, "用户名或密码错误"),
    UPLOAD_FILE_TYPE_ERROR(506, "文件类型上传错误"),
    USER_INFO_EDIT_ERROR(507, "用户编辑信息失败"),
    USER_REGISTER_ERROR(508, "用户注册失败"),

    USER_NICK_NAME_EXISTS(509, "用户昵称已存在"),
    ARTICLE_NOT_FOUND(510, "文章未找到"),
    REDIS_COUNTER_ERROR(511, "redis自增失败"),
    NO_ADMIN_PERMISSIONS(512, "该用户没有管理员权限信息"),
    ADMIN_MENU_NOT_FOUND(513, "找不到该管理员的菜单路由"),
    TAG_NOT_FOUND(514, "标签未找到"),
    UPDATE_FAILED(515, "更新失败"),
    DELETE_FAILED(516, "删除失败"),
    CREATE_FAILED(517, "创建失败"),
    TAG_EXIST(518, "标签已存在"),
    DATA_NOT_FOUND(519, "数据不存在"),
    DATA_EXIST(520, "数据已存在");


    int code;
    String msg;

    AppHttpCodeEnum(int code, String errorMessage) {
        this.code = code;
        this.msg = errorMessage;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}