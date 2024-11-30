package me.jahni.couponcore.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.jahni.couponcore.component.DistributeLockExecutor;
import me.jahni.couponcore.exception.CouponIssueException;
import me.jahni.couponcore.exception.ErrorCode;
import me.jahni.couponcore.model.Coupon;
import me.jahni.couponcore.repository.redis.RedisRepository;
import me.jahni.couponcore.repository.redis.dto.CouponIssueRequest;
import me.jahni.couponcore.util.CouponRedisUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AsyncCouponIssueServiceV1 {

    private final RedisRepository redisRepository;
    private final CouponIssueRedisService couponIssueRedisService;
    private final CouponIssueService couponIssueService;
    private final ObjectMapper objectMapper;
    private final DistributeLockExecutor distributeLockExecutor;
    private final CouponCacheService couponCacheService;

    public void issue(long couponId, long userId) {
        distributeLockExecutor.execute("lock_%s".formatted(couponId), 3000, 3000,
                () -> {
                    validateIssueConditions(couponId, userId);
                    issueRequest(couponId, userId);
            }
        );
    }

    private void issueRequest(long couponId, long userId) {
        try {
            CouponIssueRequest request = new CouponIssueRequest(couponId, userId);
            redisRepository.sAdd(CouponRedisUtils.getIssueRequestKey(couponId), String.valueOf(userId));
            redisRepository.rPush(CouponRedisUtils.getIssueRequestQueueKey(), objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            throw new CouponIssueException(ErrorCode.FAIL_COUPON_ISSUE_REQUEST, "JSON 형식이 올바르지 않습니다.");
        }
    }

    private void validateIssueConditions(long couponId, long userId) {
        Coupon coupon = couponCacheService.getCouponCache(couponId).toDomain();
        validateIssueDateCondition(coupon);
        validateTotalQuantityCondition(coupon);
        validateDuplicateIssueCondition(couponId, userId);
    }

    private void validateIssueDateCondition(Coupon coupon) {
        if (!coupon.availableIssueDate()) {
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_DATE,
                    "쿠폰 발급 기간이 아닙니다. couponId: " + coupon.getId());
        }
    }

    private void validateTotalQuantityCondition(Coupon coupon) {
        if (!couponIssueRedisService.availableTotalIssueQuantity(coupon.getId(), coupon.getTotalQuantity())) {
            throw new CouponIssueException(ErrorCode.INVALID_COUPON_ISSUE_QUANTITY,
                    "쿠폰 발급 가능 수량을 초과하였습니다. couponId: " + coupon.getId());
        }
    }

    private void validateDuplicateIssueCondition(long couponId, long userId) {
        if (!couponIssueRedisService.availableUserIssueQuantity(couponId, userId)) {
            throw new CouponIssueException(ErrorCode.COUPON_ALREADY_ISSUED,
                    "이미 발급된 쿠폰입니다. couponId: " + couponId + ", userId: " + userId);
        }
    }
}
