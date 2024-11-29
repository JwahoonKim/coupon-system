package me.jahni.couponapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jahni.couponapi.controller.dto.CouponIssueRequestDto;
import me.jahni.couponcore.component.DistributeLockExecutor;
import me.jahni.couponcore.service.CouponIssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final Logger log = LoggerFactory.getLogger(CouponIssueRequestService.class.getSimpleName());

    public void issueRequestV1(CouponIssueRequestDto requestDto) {
        couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료 - userId: {}, couponId: {}", requestDto.userId(), requestDto.couponId());
    }

    public void issueRequestV2(CouponIssueRequestDto requestDto) {
        synchronized (this) {
            couponIssueService.issue(requestDto.couponId(), requestDto.userId());
        }
        log.info("쿠폰 발급 완료 - userId: {}, couponId: {}", requestDto.userId(), requestDto.couponId());
    }

    public void issueRequestV3(CouponIssueRequestDto requestDto) {
        distributeLockExecutor.execute(
                "lock_" + requestDto.couponId(), 1000, 1000,
                () -> {couponIssueService.issue(requestDto.couponId(), requestDto.userId());}
        );
        log.info("쿠폰 발급 완료 - userId: {}, couponId: {}", requestDto.userId(), requestDto.couponId());
    }
}
