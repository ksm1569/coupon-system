package com.smsoft.couponcore.exception;

public enum ErrorCode {
    INVALID_COUPON_ISSUE_QUANTITY("발급 가능한 수량을 초과합니다"),
    INVALID_COUPON_ISSUE_DATE("발급 가능한 쿠폰 기간이 아닙니다"),

    COUPON_NOT_EXIST("존재하지 않는 쿠폰입니다"),
    DUPLICATE_COUPON_ISSUE("이미 발급된 쿠폰입니다");

    public final String msg;

    ErrorCode(String msg) {
        this.msg = msg;
    }
}
