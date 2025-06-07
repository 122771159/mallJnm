package com.jnm.mallJnm.model.enums;

import lombok.Getter;


@Getter
public enum ErrorEnum {
    NOT_LOGIN(104, "请先登录后访问"),
    NO_AUTHORITY(104, "没有访问权限"),
    //103 账号密码登录校验错误
    //104 token登录校验错误
    //105 微信openId登录校验错误

    //201 实体类参数校验错误
    REPEAT(205, "数据重复"),
    PASSWORD_ERROR(202, "原密码错误"),
    EMPTY_ID_ERROR(203, "请传入对象的id"),
    USER_TYPE_ERROR(204, "用户类型不存在"),
    HAS_ACCOUNT(205, "此账号已存在"),
    HAS_NO(206, "编号已存在"),
    HAS_NAME(207, "名称已存在"),
    HAS_CHILD(208, "存在下级,不可删除"),
    HAS_USED(209, "使用中,不可删除"),
    HAS_SORT(210, "此页码已存在"),
    DIFFERENT_USER_TYPE(211, "用户类型应与父级相同"),
    EMPTY_FILE_ERROR(212, "上传文件不可为空"),
    EMPTY_FILE_NAME_ERROR(213, "上传文件不可为空"),
    SUFFIX_ERROR(214, "请上传正确格式的文件"),
    EMPTY_URL_ERROR(215, "链接地址不可为空"),
    NUM_FORMAT_ERROR(216, "数量应为最多两位小数的正数"),
    NOT_EXIST_ERROR(217, "对象不存在"),
    HAS_PUSHED(218, "已成功推送，不可重复推送"),


    SAVE_ERROR(301, "保存失败"),
    UPDATE_ERROR(302, "修改失败"),
    DELETE_ERROR(303, "删除失败"),
    CACHE_ERROR(304, "获取缓存错误"),
    VERIFY_IMG_ERROR(305, "生成验证码图片错误"),
    READ_FILE_ERROR(306, "读取文件失败"),
    WRITE_FILE_ERROR(307, "写入文件失败"),
    EMAIL_NOT_EXIST(308, "用户邮箱不存在"),
    EMAIL_EXIST(309, "邮箱已存在"),
    PASSWORD_EMPTY(310, "密码未设置"),
    OBJECT_EMPTY(311, "传入的对象为空"),
    UPLOAD_FILE_ERROR(308, "上传文件失败"),
    DELETE_FILE_ERROR(309, "删除文件失败"),
    INVALID_STATUS_ERROR(310, "无效状态"),
    AID_EMPTY(311,"业务员或者管理员不能为空"),
    CID_EMPTY(312,"客户不能为空"),
    IDENTITY_ERROR(313,"身份信息不正确");
    private final int code;
    private final String msg;

    ErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
