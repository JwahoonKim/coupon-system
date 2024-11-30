package me.jahni.couponcore.service;

import me.jahni.couponcore.TestConfig;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Set;
import java.util.stream.IntStream;

import static me.jahni.couponcore.util.CouponRedisUtils.getIssueRequestKey;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CouponIssueRedisServiceTest extends TestConfig {

    @Autowired
    CouponIssueRedisService sut;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void clear() {
        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);
    }

    @Test
    void 쿠폰_수량_검증_발급_가능_수량이_존재하면_true() {
        // given
        long couponId = 1L;
        int totalIssueQuantity = 10;

        // when
        boolean result = sut.availableTotalIssueQuantity(couponId, totalIssueQuantity);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 쿠폰_수량_검증_발급_가능_수량이_존재하지_않으면_false() {
        // given
        long couponId = 1L;
        int totalIssueQuantity = 10;

        IntStream.range(0, totalIssueQuantity).forEach(i -> {
            redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(i));
        });

        // when
        boolean result = sut.availableTotalIssueQuantity(couponId, totalIssueQuantity);

        // then
        assertThat(result).isFalse();
    }

    @Test
    void 쿠폰_수량_검증_발급_가능_수량이_null이면_true() {
        // given
        long couponId = 1L;
        Integer totalIssueQuantity = null;

        // when
        boolean result = sut.availableTotalIssueQuantity(couponId, totalIssueQuantity);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 사용자_발급_여부_검증_발급_가능하면_true() {
        // given
        long couponId = 1L;
        long userId = 1L;

        // when
        boolean result = sut.availableUserIssueQuantity(couponId, userId);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 사용자_발급_여부_검증_발급_불가능하면_false() {
        // given
        long couponId = 1L;
        long userId = 1L;

        redisTemplate.opsForSet().add(getIssueRequestKey(couponId), String.valueOf(userId));

        // when
        boolean result = sut.availableUserIssueQuantity(couponId, userId);

        // then
        assertThat(result).isFalse();
    }

}