package me.jahni.couponcore.repository.mysql;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import me.jahni.couponcore.model.CouponIssue;
import org.springframework.stereotype.Repository;

import static me.jahni.couponcore.model.QCouponIssue.couponIssue;

@RequiredArgsConstructor
@Repository
public class CouponIssueRepository {
    private final JPAQueryFactory queryFactory;

    public CouponIssue findFirstByCouponIdAndUserId(Long couponId, Long userId) {
        return queryFactory.selectFrom(couponIssue)
                .where(couponIssue.couponId.eq(couponId))
                .where(couponIssue.userId.eq(userId))
                .fetchFirst();
    }
}
