package me.jahni.couponapi.controller;

import lombok.RequiredArgsConstructor;
import me.jahni.couponapi.controller.dto.CouponIssueRequestDto;
import me.jahni.couponapi.controller.dto.CouponIssueResponseDto;
import me.jahni.couponapi.service.CouponIssueRequestService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class CouponIssueController {

    private final CouponIssueRequestService couponIssueRequestService;

    // 동시성 문제 발생
    @PostMapping("/v1/issue")
    public CouponIssueResponseDto issueV1(@RequestBody CouponIssueRequestDto body) {
        couponIssueRequestService.issueRequestV1(body);
        return new CouponIssueResponseDto(true, null);
    }

    // synchronized 키워드로 동시성 문제 해결
    @PostMapping("/v2/issue")
    public CouponIssueResponseDto issueV2(@RequestBody CouponIssueRequestDto body) {
        couponIssueRequestService.issueRequestV2(body);
        return new CouponIssueResponseDto(true, null);
    }

    // DistributeLockExecutor로 동시성 문제 해결
    @PostMapping("/v3/issue")
    public CouponIssueResponseDto issueV3(@RequestBody CouponIssueRequestDto body) {
        couponIssueRequestService.issueRequestV3(body);
        return new CouponIssueResponseDto(true, null);
    }
}
