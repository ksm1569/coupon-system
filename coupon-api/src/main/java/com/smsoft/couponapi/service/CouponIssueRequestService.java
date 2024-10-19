package com.smsoft.couponapi.service;

import com.smsoft.couponapi.controller.dto.CouponIssueRequestDto;
import com.smsoft.couponcore.component.DistributeLockExecutor;
import com.smsoft.couponcore.service.CouponIssueService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {
    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());

    public void issueRequestV1(CouponIssueRequestDto requestDto) {
        distributeLockExecutor.execute("lock_" + requestDto.couponId(), 10000, 10000, () -> {
            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        });

        logger.info("Issue request completed. couponId: {}, userId: {}", requestDto.couponId(), requestDto.userId());
    }
}
