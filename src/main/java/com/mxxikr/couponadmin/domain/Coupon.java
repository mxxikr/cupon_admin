package com.mxxikr.couponadmin.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 쿠폰 엔티티
 * 발급된 개별 쿠폰의 정보를 저장
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 쿠폰 고유 ID

    @Column(nullable = false)
    private String couponName; // 쿠폰 이름

    @Column(nullable = false)
    private Long customerId; // 쿠폰을 발급받은 고객 ID

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CouponStatus status; // 쿠폰 상태

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 쿠폰 만료일시

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime issuedAt; // 쿠폰 발급일시

    @Builder
    public Coupon(String couponName, Long customerId, LocalDateTime expiresAt) {
        this.couponName = couponName;
        this.customerId = customerId;
        this.expiresAt = expiresAt;
        this.status = CouponStatus.ISSUED; // 쿠폰 생성 시 기본 상태는 발급
    }
}
