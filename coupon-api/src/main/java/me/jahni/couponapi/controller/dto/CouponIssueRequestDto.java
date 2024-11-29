package me.jahni.couponapi.controller.dto;

public record CouponIssueRequestDto(
    long userId,
    long couponId
) {}
