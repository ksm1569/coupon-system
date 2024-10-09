package com.smsoft.couponcore.exception;

public enum ErrorCode {
    INVALID_COUPON_ISSUE_QUANTITY,      // 쿠폰 수량
    INVALID_COUPON_ISSUE_DATE,          // 쿠폰 기간

    COUPON_NOT_EXIST,                   // 쿠폰 미존재
    DUPLICATE_COUPON_ISSUE,             // 중복 쿠폰 발급 오류
}
