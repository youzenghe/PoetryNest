package com.poetry.common;

import lombok.Getter;

@Getter
public enum ErrorCode {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未登录或登录已过期"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "资源冲突"),
    INTERNAL_ERROR(500, "服务器内部错误"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_EXISTS(1002, "用户名已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    PASSWORD_TOO_SHORT(1004, "密码长度至少6位"),

    POEM_NOT_FOUND(2001, "诗词不存在"),
    ALREADY_FAVORITED(2002, "已经收藏过了"),
    NOT_FAVORITED(2003, "未收藏此诗词"),

    ARTICLE_NOT_FOUND(3001, "文章不存在"),
    TITLE_TOO_SHORT(3002, "标题至少需要5个字符"),
    CONTENT_TOO_SHORT(3003, "内容至少需要20个字符"),
    COMMENT_EMPTY(3004, "评论内容不能为空"),
    COMMENT_TOO_LONG(3005, "评论内容不能超过500字符"),
    CANNOT_FOLLOW_SELF(3006, "不能关注自己"),

    FILE_UPLOAD_FAILED(4001, "文件上传失败"),
    FILE_TYPE_NOT_ALLOWED(4002, "文件类型不支持"),

    AUTHOR_NOT_FOUND(5001, "未找到该作者的诗词");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
