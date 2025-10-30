package com.mxxikr.couponadmin.domain;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 쿠폰 발급 파일 메타데이터 JPA 리포지토리
 */
public interface CouponIssuanceFileMetadataRepository extends JpaRepository<CouponIssuanceFileMetadata, String> {
}
