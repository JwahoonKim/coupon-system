package me.jahni.couponcore.model;

import me.jahni.couponcore.exception.CouponIssueException;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CouponTest {

    @Test
    void 쿠폰_개수가_남아있으면_발급이_가능하다() {
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .build();

        boolean result = coupon.availableIssueQuantity();

        assertThat(result).isTrue();
    }

    @Test
    void 쿠폰_개수가_남아있지_않으면_발급이_불가능하다() {
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .build();

        boolean result = coupon.availableIssueQuantity();

        assertThat(result).isFalse();
    }

    @Test
    void 쿠폰발급_최대_수량이_설정되지_않았다면_발급이_가능하다() {
        Coupon coupon = Coupon.builder()
                .totalQuantity(null)
                .issuedQuantity(99)
                .build();

        boolean result = coupon.availableIssueQuantity();

        assertThat(result).isTrue();
    }

    @Test
    void 쿠폰_발급_기한이_아직_시작되지_않았다면_발급이_불가능하다() {
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        boolean result = coupon.availableIssueDate();

        assertThat(result).isFalse();
    }

    @Test
    void 쿠폰_발급_기한이_지났다면_발급이_불가능하다() {
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(2))
                .dateIssueEnd(LocalDateTime.now().minusDays(1))
                .build();

        boolean result = coupon.availableIssueDate();

        assertThat(result).isFalse();
    }

    @Test
    void 쿠폰_발급_기한이_현재_시간에_해당하면_발급이_가능하다() {
        Coupon coupon = Coupon.builder()
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        boolean result = coupon.availableIssueDate();

        assertThat(result).isTrue();
    }

    @Test
    void 발급_수량과_발급_기간이_유효하다면_발급에_성공한다() {
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        coupon.issue();

        assertThat(coupon.getIssuedQuantity()).isEqualTo(100);
    }

    @Test
    void 발급_수량이_모두_소진되었다면_발급에_실패한다() {
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(100)
                .dateIssueStart(LocalDateTime.now().minusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(1))
                .build();

        assertThatThrownBy(coupon::issue)
                .isInstanceOf(CouponIssueException.class)
                .hasMessageContaining("쿠폰이 발급 가능한 수량이 아닙니다.");
    }

    @Test
    void 발급_기간이_아니라면_발급에_실패한다() {
        Coupon coupon = Coupon.builder()
                .totalQuantity(100)
                .issuedQuantity(99)
                .dateIssueStart(LocalDateTime.now().plusDays(1))
                .dateIssueEnd(LocalDateTime.now().plusDays(2))
                .build();

        assertThatThrownBy(coupon::issue)
                .isInstanceOf(CouponIssueException.class)
                .hasMessageContaining("쿠폰이 발급 가능한 기간이 아닙니다.");
    }
}