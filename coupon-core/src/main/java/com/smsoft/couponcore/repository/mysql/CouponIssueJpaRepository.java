package com.smsoft.couponcore.repository.mysql;

import com.smsoft.couponcore.model.Coupon;
import com.smsoft.couponcore.model.CouponIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {
}
