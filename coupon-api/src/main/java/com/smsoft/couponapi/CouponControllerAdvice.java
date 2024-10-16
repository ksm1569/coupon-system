package com.smsoft.couponapi;

import com.smsoft.couponapi.controller.dto.CouponIssueResponseDto;
import com.smsoft.couponcore.exception.CouponIssueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CouponControllerAdvice {
    @ExceptionHandler(CouponIssueException.class)
    public CouponIssueResponseDto handleCouponIssueException(CouponIssueException e) {
        return new CouponIssueResponseDto(false, e.getErrorCode().msg);
    }
}
