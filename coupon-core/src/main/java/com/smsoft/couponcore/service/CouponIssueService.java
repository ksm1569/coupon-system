package com.smsoft.couponcore.service;

import com.smsoft.couponcore.exception.CouponIssueException;
import com.smsoft.couponcore.model.Coupon;
import com.smsoft.couponcore.model.CouponIssue;
import com.smsoft.couponcore.repository.mysql.CouponIssueJpaRepository;
import com.smsoft.couponcore.repository.mysql.CouponIssueRepository;
import com.smsoft.couponcore.repository.mysql.CouponJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.smsoft.couponcore.exception.ErrorCode.COUPON_NOT_EXIST;
import static com.smsoft.couponcore.exception.ErrorCode.DUPLICATE_COUPON_ISSUE;

@RequiredArgsConstructor
@Service
public class CouponIssueService {
    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponIssueRepository couponIssueRepository;

    @Transactional
    public void issue(long couponId, long userId) {
        Coupon coupon = findCoupon(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
    }

    @Transactional(readOnly = true)
    public Coupon findCoupon(long couponId) {
        return couponJpaRepository.findById(couponId).orElseThrow(() ->
            new CouponIssueException(COUPON_NOT_EXIST, "존재하지 않는 쿠폰 입니다. %s".formatted(couponId))
        );
    }

    @Transactional
    public CouponIssue saveCouponIssue(long couponId, long userId) {
        checkAlreadyIssuance(couponId, userId);

        CouponIssue issue = CouponIssue.builder()
                .couponId(couponId)
                .userId(userId)
                .build();

        return couponIssueJpaRepository.save(issue);
    }

    private void checkAlreadyIssuance(long couponId, long userId) {
        CouponIssue issue = couponIssueRepository.findFirstCouponIssue(couponId, userId);

        if (issue != null) {
            throw new CouponIssueException(DUPLICATE_COUPON_ISSUE, "이미 발급된 쿠폰입니다. couponId : %s, userId : %s".formatted(couponId, userId));
        }
    }
}
