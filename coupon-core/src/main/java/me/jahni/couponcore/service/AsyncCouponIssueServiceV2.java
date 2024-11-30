package me.jahni.couponcore.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.jahni.couponcore.component.DistributeLockExecutor;
import me.jahni.couponcore.exception.CouponIssueException;
import me.jahni.couponcore.exception.ErrorCode;
import me.jahni.couponcore.model.Coupon;
import me.jahni.couponcore.repository.redis.RedisRepository;
import me.jahni.couponcore.repository.redis.dto.CouponRedisEntity;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV2 {

    private final RedisRepository redisRepository;
    private final CouponCacheService couponCacheService;

    public void issue(long couponId, long userId) {
        Coupon coupon = couponCacheService.getCouponCache(couponId).toDomain();
        validateIssueDateCondition(coupon);
        issueRequest(couponId, userId, coupon.getTotalQuantity());

    }

    private void issueRequest(long couponId, long userId, Integer totalQuantity) {
        redisRepository.issueRequest(couponId, userId, totalQuantity);
    }

    private void validateIssueDateCondition(Coupon coupon) {
        if (!coupon.availableIssueDate()) {
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE,
                    "쿠폰 발급 기간이 아닙니다. couponId: " + coupon.getId());
        }
    }
}
