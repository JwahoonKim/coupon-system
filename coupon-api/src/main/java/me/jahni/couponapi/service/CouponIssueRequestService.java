package me.jahni.couponapi.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.jahni.couponapi.controller.dto.CouponIssueRequestDto;
import me.jahni.couponcore.component.DistributeLockExecutor;
import me.jahni.couponcore.service.AsyncCouponIssueServiceV1;
import me.jahni.couponcore.service.AsyncCouponIssueServiceV2;
import me.jahni.couponcore.service.CouponIssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CouponIssueRequestService {

    private final CouponIssueService couponIssueService;
    private final DistributeLockExecutor distributeLockExecutor;
    private final AsyncCouponIssueServiceV1 asyncCouponIssueServiceV1;
    private final AsyncCouponIssueServiceV2 asyncCouponIssueServiceV2;
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

    public void issueRequestV4(CouponIssueRequestDto requestDto) {
        couponIssueService.issueWithLock(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료 - userId: {}, couponId: {}", requestDto.userId(), requestDto.couponId());
    }

    public void issueRequestV5(CouponIssueRequestDto requestDto) {
        asyncCouponIssueServiceV1.issue(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료 - userId: {}, couponId: {}", requestDto.userId(), requestDto.couponId());
    }

    public void issueRequestV6(CouponIssueRequestDto requestDto) {
        asyncCouponIssueServiceV2.issue(requestDto.couponId(), requestDto.userId());
        log.info("쿠폰 발급 완료 - userId: {}, couponId: {}", requestDto.userId(), requestDto.couponId());
    }
}
