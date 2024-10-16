package com.smsoft.couponapi.service;

import com.smsoft.couponapi.controller.dto.CouponIssueRequestDto;
import com.smsoft.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {
    private final CouponIssueService couponIssueService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public void issueRequestV1(CouponIssueRequestDto requestDto) {
        couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        logger.info("Issue request completed. couponId: {}, userId: {}", requestDto.couponId(), requestDto.userId());
    }
}
