package me.jahni.couponcore.model;


import jakarta.persistence.*;
import lombok.*;
import me.jahni.couponcore.exception.CouponIssueException;

import java.time.LocalDateTime;

import static me.jahni.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_DATE;
import static me.jahni.couponcore.exception.ErrorCode.INVALID_COUPON_ISSUE_QUANTITY;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "coupons")
public class Coupon extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponType couponType;

    private Integer totalQuantity;

    @Column(nullable = false)
    private int issuedQuantity;

    @Column(nullable = false)
    private int discountAmount;

    @Column(nullable = false)
    private int minAvailableAmount;

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
        return dateIssueStart.isBefore(now) && dateIssueEnd.isAfter(now);
    }

    public void issue() {
        validateIssueConditions();
        issuedQuantity++;
    }

    private void validateIssueConditions() {
        validateIssueQuantityCondition();
        validateIssueDateCondition();
    }

    private void validateIssueDateCondition() {
        if (!availableIssueDate()) {
            throw new CouponIssueException(
                    INVALID_COUPON_ISSUE_DATE,
                    "쿠폰이 발급 가능한 기간이 아닙니다. dateIssueStart: %s, dateIssueEnd: %s"
                            .formatted(dateIssueStart, dateIssueEnd)
            );
        }
    }

    private void validateIssueQuantityCondition() {
        if (!availableIssueQuantity()) {
            throw new CouponIssueException(
                    INVALID_COUPON_ISSUE_QUANTITY,
                    "쿠폰이 발급 가능한 수량이 아닙니다. totalQuantity: %d, issuedQuantity: %d"
                            .formatted(totalQuantity, issuedQuantity)
            );
        }
    }
}
