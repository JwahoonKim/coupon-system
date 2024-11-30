package me.jahni.couponconsumer.component;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.jahni.couponconsumer.TestConfig;
import me.jahni.couponcore.repository.redis.RedisRepository;
import me.jahni.couponcore.service.CouponIssueService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Set;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Import(CouponIssueListener.class)
class CouponIssueListenerTest extends TestConfig {

    @Autowired
    private CouponIssueListener sut;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private RedisRepository repository;

    @MockitoBean
    private CouponIssueService couponIssueService;

    @BeforeEach
    void clear() {
        Set<String> keys = redisTemplate.keys("*");
        redisTemplate.delete(keys);
    }

    @Test
    @DisplayName("쿠폰 발급 큐에 처리 대상이 없다면 발급을 하지 않는다.")
    void issue_1() throws JsonProcessingException {
        // when
        sut.issue();
        // then
        verify(couponIssueService, never()).issue(anyLong(), anyLong());
    }

    @Test
    @DisplayName("쿠폰 발급 큐에 처리 대상이 있다면 발급한다.")
    void issue_2() throws JsonProcessingException {
        // given
        long couponId = 1;
        long userId = 1;
        int totalQuantity = Integer.MAX_VALUE;
        repository.issueRequest(couponId, userId, totalQuantity);

        // when
        sut.issue();

        // then
        verify(couponIssueService, times(1)).issue(couponId, userId);
    }

    @Test
    @DisplayName("쿠폰 발급 요청 순서에 맞게 처리된다.")
    void issue_3() throws JsonProcessingException {
        // given
        long couponId = 1;
        long userId1 = 1;
        long userId2 = 2;
        long userId3 = 3;
        int totalQuantity = Integer.MAX_VALUE;
        repository.issueRequest(couponId, userId1, totalQuantity);
        repository.issueRequest(couponId, userId2, totalQuantity);
        repository.issueRequest(couponId, userId3, totalQuantity);

        // when
        sut.issue();
        // then
        InOrder inOrder = Mockito.inOrder(couponIssueService);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId1);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId2);
        inOrder.verify(couponIssueService, times(1)).issue(couponId, userId3);
    }


}