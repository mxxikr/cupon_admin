package com.mxxikr.couponadmin.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 쿠폰 JPA 리포지토리
 */
public interface CouponRepository extends JpaRepository<Coupon, Long> {
}
