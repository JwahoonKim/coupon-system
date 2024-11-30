package me.jahni.couponconsumer.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.jahni.couponcore.repository.redis.RedisRepository;
import me.jahni.couponcore.repository.redis.dto.CouponIssueRequest;
import me.jahni.couponcore.service.CouponIssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import static me.jahni.couponcore.util.CouponRedisUtils.getIssueRequestQueueKey;

@EnableScheduling
@RequiredArgsConstructor
@Component
public class CouponIssueListener {

    private final RedisRepository redisRepository;
    private final CouponIssueService couponIssueService;
    private final ObjectMapper objectMapper;
    private final String issueRequestQueueKey = getIssueRequestQueueKey();

    private final Logger log = LoggerFactory.getLogger(CouponIssueListener.class.getSimpleName());

    @Scheduled(fixedDelay = 1000)
    public void issue() throws JsonProcessingException {
        log.info("listen...");
        while (existCouponIssueTarget()) {
            CouponIssueRequest target = getIssueTarget();
            log.info("발급 시작 target: {}", target);
            couponIssueService.issue(target.couponId(), target.userId());
            log.info("발급 완료 target: {}", target);
            removeIssuedTarget();
        }
    }

    private boolean existCouponIssueTarget() {
        return redisRepository.lSize(issueRequestQueueKey) > 0;
    }

    private CouponIssueRequest getIssueTarget() throws JsonProcessingException {
        String rawValue = redisRepository.lIndex(issueRequestQueueKey, 0);
        return objectMapper.readValue(rawValue, CouponIssueRequest.class);
    }

    private void removeIssuedTarget() {
        redisRepository.lPop(issueRequestQueueKey);
    }
}
