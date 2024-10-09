package com.smsoft.couponcore.service;

import com.smsoft.couponcore.TestConfig;
import com.smsoft.couponcore.exception.CouponIssueException;
import com.smsoft.couponcore.exception.ErrorCode;
import com.smsoft.couponcore.model.Coupon;
import com.smsoft.couponcore.model.CouponIssue;
import com.smsoft.couponcore.model.CouponType;
import com.smsoft.couponcore.repository.mysql.CouponIssueJpaRepository;
import com.smsoft.couponcore.repository.mysql.CouponIssueRepository;
import com.smsoft.couponcore.repository.mysql.CouponJpaRepository;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.smsoft.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static com.smsoft.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;
import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CouponIssueServiceTest extends TestConfig {
    private static final Logger log = LoggerFactory.getLogger(CouponIssueServiceTest.class);
    @Autowired
    CouponIssueService sut;

    @Autowired
    CouponIssueJpaRepository couponIssueJpaRepository;

    @Autowired
    CouponIssueRepository couponIssueRepository;

    @Autowired
    CouponJpaRepository couponJpaRepository;

    @BeforeEach
    void setUp() {
        couponIssueJpaRepository.deleteAllInBatch();
        couponJpaRepository.deleteAllInBatch();
    }

    @Test
    @Order(1)
    @DisplayName("쿠폰 미존재 -> 예외 반환")
    void saveCouponIssue_one() {
        // given
        long couponId = 1;
        long userId = 1;

        // when & then
        CouponIssueException couponIssueException = assertThrows(CouponIssueException.class, () -> {
            sut.issue(couponId, userId);
        });

        assertEquals(couponIssueException.getErrorCode(), ErrorCode.COUPON_NOT_EXIST);
    }

    @Test
    @Order(2)
    @DisplayName("쿠폰 발급 내역이 존재(중복 발급) -> 예외 반환")
    void saveCouponIssue_two() {
        // given
        CouponIssue couponIssue = CouponIssue.builder()
                .couponId(1L)
                .userId(1L)
                .build();

        couponIssueJpaRepository.save(couponIssue);

        // when & then
        CouponIssueException couponIssueException = assertThrows(CouponIssueException.class, () -> {
            sut.saveCouponIssue(couponIssue.getCouponId(), couponIssue.getUserId());
        });

        assertEquals(couponIssueException.getErrorCode(), ErrorCode.DUPLICATE_COUPON_ISSUE);
    }

    @Test
    @Order(3)
    @DisplayName("쿠폰 발급 내역이 존재하지 않는다 -> 발급 성공")
    void saveCouponIssue_three() {
        // given
        long couponId = 1;
        long userId = 1;

        // when
        CouponIssue issue = sut.saveCouponIssue(couponId, userId);

        // then
        assertTrue(couponIssueJpaRepository.findById(issue.getId()).isPresent());
    }

    @Test
    @Order(4)
    @DisplayName("쿠폰 발급 수량이 초과한다면 -> 예외 반환")
    void saveCouponIssue_four() {
        // given
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .name("선착순 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(10)
                .discountAmount(0)
                .minAvailableAmount(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        couponJpaRepository.save(coupon);

        // when & then
        CouponIssueException couponIssueException = assertThrows(CouponIssueException.class, () -> {
            sut.issue(coupon.getId(), userId);
        });

        assertEquals(couponIssueException.getErrorCode(), INVALID_COUPON_ISSUE_QUANTITY);
    }

    @Test
    @Order(5)
    @DisplayName("쿠폰 발급 기간이 아니라면 (발급 시작 전) -> 예외 반환")
    void saveCouponIssue_five() {
        // given
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .name("선착순 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .discountAmount(0)
                .minAvailableAmount(0)
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        couponJpaRepository.save(coupon);

        // when & then
        CouponIssueException couponIssueException = assertThrows(CouponIssueException.class, () -> {
            sut.issue(coupon.getId(), userId);
        });

        assertEquals(couponIssueException.getErrorCode(), INVALID_COUPON_ISSUE_DATE);
    }

    @Test
    @Order(6)
    @DisplayName("쿠폰 발급 기간이 아니라면 (발급일자 지남) -> 예외 반환")
    void saveCouponIssue_six() {
        // given
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .name("선착순 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .discountAmount(0)
                .minAvailableAmount(0)
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();

        couponJpaRepository.save(coupon);

        // when & then
        CouponIssueException couponIssueException = assertThrows(CouponIssueException.class, () -> {
            sut.issue(coupon.getId(), userId);
        });

        assertEquals(couponIssueException.getErrorCode(), INVALID_COUPON_ISSUE_DATE);
    }

    @Test
    @Order(7)
    @DisplayName("쿠폰 발급 - 중복발급, 일자, 수량에 문제가 없으면 -> 발급 성공")
    void saveCouponIssue_seven() {
        // given
        long userId = 1;
        Coupon coupon = Coupon.builder()
                .couponType(CouponType.FIRST_COME_FIRST_SERVED)
                .name("선착순 쿠폰")
                .totalQuantity(10)
                .issuedQuantity(0)
                .discountAmount(0)
                .minAvailableAmount(0)
                .dateIssueStart(LocalDateTime.now().minusDays(3))
                .dateIssueEnd(LocalDateTime.now().plusDays(3))
                .build();

        couponJpaRepository.save(coupon);

        // when
        sut.issue(coupon.getId(), userId);

        // then
        Coupon couponResult = couponJpaRepository.findById(coupon.getId()).get();
        assertEquals(couponResult.getIssuedQuantity(), 1);

        CouponIssue couponIssueResult = couponIssueRepository.findFirstCouponIssue(coupon.getId(), userId);
        assertNotNull(couponIssueResult);
    }

}