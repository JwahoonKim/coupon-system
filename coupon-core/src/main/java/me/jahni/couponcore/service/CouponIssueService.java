package me.jahni.couponcore.service;

import lombok.RequiredArgsConstructor;
import me.jahni.couponcore.exception.CouponIssueException;
import me.jahni.couponcore.exception.ErrorCode;
import me.jahni.couponcore.model.Coupon;
import me.jahni.couponcore.model.CouponIssue;
import me.jahni.couponcore.repository.mysql.CouponIssueJpaRepository;
import me.jahni.couponcore.repository.mysql.CouponIssueRepository;
import me.jahni.couponcore.repository.mysql.CouponJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CouponIssueService {

    private final CouponJpaRepository couponJpaRepository;
    private final CouponIssueJpaRepository couponIssueJpaRepository;
    private final CouponIssueRepository couponIssueRepository;

    @Transactional
    public void issue(Long couponId, Long userId) {
        Coupon coupon = findCoupon(couponId);
        coupon.issue();
        saveCouponIssue(couponId, userId);
    }

    @Transactional(readOnly = true)
    public Coupon findCoupon(Long couponId) {
        return couponJpaRepository.findById(couponId)
                .orElseThrow(() -> new CouponIssueException(ErrorCode.COUPON_NOT_EXIST,
                        "Coupon not exist. couponId: " + couponId));
    }


    @Transactional
    public CouponIssue saveCouponIssue(Long couponId, Long userId) {
        checkAlreadyIssuance(couponId, userId);

        CouponIssue couponIssue = CouponIssue.builder()
                .couponId(couponId)
                .userId(userId)
                .build();

        return couponIssueJpaRepository.save(couponIssue);
    }

    private void checkAlreadyIssuance(long couponId, long userId) {
        CouponIssue issue = couponIssueRepository.findFirstByCouponIdAndUserId(couponId, userId);
        if (issue != null) {
            throw new CouponIssueException(ErrorCode.COUPON_ALREADY_ISSUED,
                    "이미 발급된 쿠폰입니다. couponId: " + couponId + ", userId: " + userId);
        }
    }
}
