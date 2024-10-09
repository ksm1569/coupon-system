package com.smsoft.couponcore.repository.mysql;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.smsoft.couponcore.model.CouponIssue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.smsoft.couponcore.model.QCouponIssue.couponIssue;

@RequiredArgsConstructor
@Repository
public class CouponIssueRepository {
    private final JPAQueryFactory queryFactory;

    public CouponIssue findFirstCouponIssue(long couponId, long userId) {
        return queryFactory.selectFrom(couponIssue)
                .where(couponIssue.couponId.eq(couponId)
                        .and(couponIssue.userId.eq(userId)))
                .fetchFirst();
    }
}
