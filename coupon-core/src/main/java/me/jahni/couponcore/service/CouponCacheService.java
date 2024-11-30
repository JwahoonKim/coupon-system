package me.jahni.couponcore.service;

import lombok.RequiredArgsConstructor;
import me.jahni.couponcore.model.Coupon;
import me.jahni.couponcore.repository.redis.dto.CouponRedisEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponCacheService {

    private final CouponIssueService couponIssService;

    @Cacheable(cacheNames = "coupon", key = "#couponId")
    public CouponRedisEntity getCouponCache(long couponId) {
        Coupon coupon = couponIssService.findCoupon(couponId);
        return new CouponRedisEntity(coupon);
    }

}
