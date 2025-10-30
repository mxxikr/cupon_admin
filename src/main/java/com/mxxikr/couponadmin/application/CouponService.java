package com.mxxikr.couponadmin.application;

import com.mxxikr.couponadmin.domain.Coupon;
import com.mxxikr.couponadmin.domain.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 개별 쿠폰 관련 비즈니스 로직을 처리
 */
@Service
@RequiredArgsConstructor
public class CouponService {

    private final CouponRepository couponRepository;

    /**
     * 여러 고객에게 쿠폰을 대량으로 발급함
     * @param customerIds 고객 ID 목록
     * @param couponName 쿠폰 이름
     * @param expiresAt 쿠폰 만료일시
     */
    @Transactional
    public void issueCouponsInBulk(List<Long> customerIds, String couponName, LocalDateTime expiresAt) {
        List<Coupon> couponsToIssue = customerIds.stream()
                .map(customerId -> Coupon.builder()
                        .customerId(customerId)
                        .couponName(couponName) // 쿠폰 이름
                        .expiresAt(expiresAt) // 쿠폰 만료일시
                        .build())
                .collect(Collectors.toList());

        couponRepository.saveAll(couponsToIssue);
    }
}
