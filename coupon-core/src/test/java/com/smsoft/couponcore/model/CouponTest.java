package com.smsoft.couponcore.model;

import com.smsoft.couponcore.exception.CouponIssueException;
import com.smsoft.couponcore.exception.ErrorCode;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CouponTest {

    @Test
    @Order(1)
    @DisplayName("쿠폰 발급 수량이 남아 있으면 -> true 반환")
    void availableIssueQuantity_one() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(50)
                .issuedQuantity(49)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @Order(2)
    @DisplayName("쿠폰 발급 수량이 남아 있으면 -> false 반환")
    void availableIssueQuantity_two() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(50)
                .issuedQuantity(50)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        Assertions.assertFalse(result);
    }

    @Test
    @Order(3)
    @DisplayName("쿠폰 발급 수량이 설정 되지 않았다면 -> true 반환 ")
    void availableIssueQuantity_three() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(null)
                .issuedQuantity(50)
                .build();

        // when
        boolean result = coupon.availableIssueQuantity();

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @Order(4)
    @DisplayName("쿠폰 발급 기간이 시작되지 않았다면 -> false 반환")
    void availableIssueDate_one() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        Assertions.assertFalse(result);
    }

    @Test
    @Order(5)
    @DisplayName("쿠폰 발급 기간이라면 -> true 반환")
    void availableIssueDate_two() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(5))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        Assertions.assertTrue(result);
    }

    @Test
    @Order(6)
    @DisplayName("쿠폰 발급 기간이 끝났다면 -> false 반환")
    void availableIssueDate_three() {
        // given
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(6))
                .dateIssueEnd(LocalDateTime.now().minusDays(4))
                .build();

        // when
        boolean result = coupon.availableIssueDate();

        // then
        Assertions.assertFalse(result);
    }

    @Test
    @Order(7)
    @DisplayName("발급 수량 및 기간이 유효하다면 -> 발급 성공")
    void issue_one() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(50)
                .issuedQuantity(49)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(4))
                .build();

        // when
        coupon.issue();

        // then
        Assertions.assertEquals(coupon.getIssuedQuantity(), 50);
    }

    @Test
    @Order(8)
    @DisplayName("발급 수량 초과 -> 발급 실패 및 예외반환")
    void issue_two() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(50)
                .issuedQuantity(50)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(4))
                .build();

        // when & then
        CouponIssueException couponIssueException = assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(couponIssueException.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_QUANTITY);
    }

    @Test
    @Order(9)
    @DisplayName("발급 기간 되지 않았다면 -> 발급 실패 및 예외반환")
    void issue_three() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(50)
                .issuedQuantity(49)
                .dateIssueStart(LocalDateTime.now().plusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(4))
                .build();

        // when & then
        CouponIssueException couponIssueException = assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(couponIssueException.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_DATE);
    }

    @Test
    @Order(10)
    @DisplayName("발급 기간 지났다면 -> 발급 실패 및 예외반환")
    void issue_four() {
        // given
        Coupon coupon = Coupon.builder()
                .totalQuantity(50)
                .issuedQuantity(49)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();

        // when & then
        CouponIssueException couponIssueException = assertThrows(CouponIssueException.class, coupon::issue);
        Assertions.assertEquals(couponIssueException.getErrorCode(), ErrorCode.INVALID_COUPON_ISSUE_DATE);
    }
}

