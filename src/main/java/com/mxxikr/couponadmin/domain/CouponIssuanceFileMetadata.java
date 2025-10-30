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
 * 쿠폰 발급 파일 메타데이터 엔티티
 * 업로드된 파일의 정보를 데이터베이스에 저장함
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CouponIssuanceFileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "file_id", updatable = false, nullable = false)
    private String fileId; // 파일 고유 ID

    @Column(nullable = false)
    private String originalFileName; // 원본 파일명

    @Column(nullable = false)
    private String storedFilePath; // 서버에 저장된 파일의 실제 경로

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt; // 생성일시

    @Builder
    public CouponIssuanceFileMetadata(String originalFileName, String storedFilePath) {
        this.originalFileName = originalFileName;
        this.storedFilePath = storedFilePath;
    }
}
