package me.jahni.couponcore.service;

import lombok.RequiredArgsConstructor;
import me.jahni.couponcore.model.Coupon;
import me.jahni.couponcore.repository.redis.dto.CouponRedisEntity;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponCacheService {

    private final CouponIssueService couponIssService;

    @Cacheable(cacheNames = "coupon", key = "#couponId", cacheManager = "redisCacheManager")
    public CouponRedisEntity getCouponCache(long couponId) {
        Coupon coupon = couponIssService.findCoupon(couponId);
        return new CouponRedisEntity(coupon);
    }

    @Cacheable(cacheNames = "coupon", key = "#couponId", cacheManager = "localCacheManager")
    public CouponRedisEntity getCouponLocalCache(long couponId) {
        Coupon coupon = couponIssService.findCoupon(couponId);
        return proxy().getCouponCache(couponId);
    }

    private CouponCacheService proxy() {
        return ((CouponCacheService) AopContext.currentProxy());
    }
}
