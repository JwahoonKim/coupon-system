package me.jahni.couponcore.service;

import lombok.RequiredArgsConstructor;
import me.jahni.couponcore.repository.redis.RedisRepository;
import me.jahni.couponcore.util.CouponRedisUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponIssueRedisService {

    private final RedisRepository redisRepository;

    public boolean availableTotalIssueQuantity(long couponId, Integer totalIssueQuantity) {
        if (totalIssueQuantity == null) {
            return true;
        }
        String key = CouponRedisUtils.getIssueRequestKey(couponId);
        return totalIssueQuantity > redisRepository.sCard(key);
    }

    public boolean availableUserIssueQuantity(long couponId, long userId) {
        String key = CouponRedisUtils.getIssueRequestKey(couponId);
        return !redisRepository.sIsMember(key, String.valueOf(userId));
    }
}
