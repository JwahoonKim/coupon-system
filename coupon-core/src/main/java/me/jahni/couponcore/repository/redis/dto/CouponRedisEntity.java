package me.jahni.couponcore.repository.redis.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import me.jahni.couponcore.model.Coupon;
import me.jahni.couponcore.model.CouponType;

import java.time.LocalDateTime;

public record CouponRedisEntity(
        Long id,
        CouponType couponType,
        Integer totalQuantity,

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime dateIssueSTart,

        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonDeserialize(using = LocalDateTimeDeserializer.class)
        LocalDateTime dateIssueEnd
) {

    public CouponRedisEntity(Coupon coupon) {
        this(
                coupon.getId(),
                coupon.getCouponType(),
                coupon.getTotalQuantity(),
                coupon.getDateIssueStart(),
                coupon.getDateIssueEnd()
        );
    }

    public Coupon toDomain() {
        return Coupon.builder()
                .id(id)
                .couponType(couponType)
                .totalQuantity(totalQuantity)
                .dateIssueStart(dateIssueSTart)
                .dateIssueEnd(dateIssueEnd)
                .build();
    }
}
