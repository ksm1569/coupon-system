package com.smsoft.couponcore.model;

import com.smsoft.couponcore.exception.CouponIssueException;
import com.smsoft.couponcore.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.smsoft.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static com.smsoft.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "coupons")
public class Coupon extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private CouponType couponType;

    @Column(nullable = true)
    private Integer totalQuantity;

    @Column(nullable = false)
    private Integer issuedQuantity;

    @Column(nullable = false)
    private Integer discountAmount;

    @Column(nullable = false)
    private Integer minAvailableAmount;

    @Column(nullable = false)
    private LocalDateTime dateIssueStart;

    @Column(nullable = false)
    private LocalDateTime dateIssueEnd;

    public boolean availableIssueQuantity() {
        if (totalQuantity == null) {
            return true;
        }

        return totalQuantity > issuedQuantity;
    }

    public boolean availableIssueDate() {
        LocalDateTime now = LocalDateTime.now();

        return now.isAfter(dateIssueStart) && now.isBefore(dateIssueEnd);
    }

    public void issue() {
        if (!availableIssueQuantity()) {
            throw new CouponIssueException(INVALID_COUPON_ISSUE_QUANTITY, "발급 가능한 쿠폰 수량을 초과했습니다. totalQuantity : %s, issueQuantity : %s".formatted(totalQuantity, issuedQuantity));
        }

        if (!availableIssueDate()) {
            throw new CouponIssueException(INVALID_COUPON_ISSUE_DATE, "쿠폰 발급 가능 일자가 아닙니다. request : %s, dateIssueStart : %s, dateIssueEnd : %s".formatted(LocalDateTime.now(), dateIssueStart, dateIssueEnd));
        }

        issuedQuantity++;
    }
}
