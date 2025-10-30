package com.mxxikr.couponadmin.application;

import com.mxxikr.couponadmin.application.port.out.StorageService;
import com.mxxikr.couponadmin.common.exception.BusinessException;
import com.mxxikr.couponadmin.common.exception.ErrorCode;
import com.mxxikr.couponadmin.controller.FileDownloadResponseDTO;
import com.mxxikr.couponadmin.domain.CouponIssuanceFile;
import com.mxxikr.couponadmin.domain.CouponIssuanceFileMetadata;
import com.mxxikr.couponadmin.domain.CouponIssuanceFileMetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 쿠폰 대량 발급 요청을 처리
 */
@Service
@RequiredArgsConstructor
public class CouponIssuanceService {

    private final CouponIssuanceFileMetadataRepository metadataRepository;
    private final CouponService couponService;
    private final StorageService storageService;

    /**
     * 파일을 업로드하고 고객 목록을 추출하여 각 고객에게 쿠폰을 대량 발급함
     * @param file 업로드된 MultipartFile
     * @param couponName 발급할 쿠폰 이름
     * @param expiresAt 쿠폰 만료일시
     * @return 저장된 파일의 고유 ID
     */
    @Transactional
    public String uploadFile(MultipartFile file, String couponName, LocalDateTime expiresAt) {
        // 업로드된 파일을 도메인 객체로 변환하고 유효성을 검증함
        CouponIssuanceFile couponFile = new CouponIssuanceFile(file);
        List<Long> customerIds = couponFile.getCustomerIds();

        // 추출된 고객 ID 목록을 사용하여 쿠폰을 대량 발급함
        couponService.issueCouponsInBulk(customerIds, couponName, expiresAt);

        // 파일 저장 로직을 StorageService에 위임함
        String storedPath = storageService.store(file);

        // 파일 메타데이터를 데이터베이스에 저장함
        CouponIssuanceFileMetadata metadata = CouponIssuanceFileMetadata.builder()
                .originalFileName(couponFile.getOriginalFilename())
                .storedFilePath(storedPath) // 저장된 경로를 받아서 설정
                .build();

        CouponIssuanceFileMetadata savedMetadata = metadataRepository.save(metadata);

        return savedMetadata.getFileId();
    }

    /**
     * 파일 ID를 사용하여 저장된 파일 메타데이터와 파일 내용을 반환함
     * @param fileId 파일 고유 ID
     * @return 파일 메타데이터와 파일 바이트 배열을 담은 DTO
     */
    @Transactional(readOnly = true)
    public FileDownloadResponseDTO downloadFile(String fileId) {
        // 데이터베이스에서 파일 메타데이터를 조회함
        CouponIssuanceFileMetadata metadata = metadataRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ErrorCode.FILE_NOT_FOUND));

        // 파일 로드 로직을 StorageService에 위임함
        byte[] fileContent = storageService.load(metadata.getStoredFilePath());
        return new FileDownloadResponseDTO(metadata.getOriginalFileName(), fileContent);
    }
}