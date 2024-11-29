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

    @PostMapping("/v1/issue")
    public CouponIssueResponseDto issueV1(@RequestBody CouponIssueRequestDto body) {
        couponIssueRequestService.issueRequestV1(body);
        return new CouponIssueResponseDto(true, null);
    }

}
