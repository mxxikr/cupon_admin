package com.mxxikr.couponadmin.application;

import com.mxxikr.couponadmin.application.port.out.StorageService;
import com.mxxikr.couponadmin.common.exception.BusinessException;
import com.mxxikr.couponadmin.common.exception.ErrorCode;
import com.mxxikr.couponadmin.controller.FileDownloadResponseDTO;
import com.mxxikr.couponadmin.domain.CouponIssuanceFileMetadata;
import com.mxxikr.couponadmin.domain.CouponIssuanceFileMetadataRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CouponIssuanceServiceTest {

    @InjectMocks
    private CouponIssuanceService couponIssuanceService;

    @Mock
    private CouponIssuanceFileMetadataRepository metadataRepository;

    @Mock
    private CouponService couponService;

    @Mock
    private StorageService storageService;

    @Test
    @DisplayName("파일 업로드 및 쿠폰 대량 발급 성공")
    void uploadFile_성공() {
        // given
        List<Long> customerIds = List.of(12345L, 67890L);
        MockMultipartFile file = new MockMultipartFile("file", "customers.csv", "text/csv", "customer_id\n12345\n67890".getBytes());
        String couponName = "신규 고객 환영 쿠폰";
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(30);
        String storedPath = "/path/to/stored/file.csv";
        String expectedFileId = "test_file_id_123";

        // Repository가 save 후 반환할 Mock 객체 생성
        CouponIssuanceFileMetadata savedMetadata = mock(CouponIssuanceFileMetadata.class);

        // Mock 객체의 getFileId()가 호출되면 예상 ID를 반환하도록 설정
        when(savedMetadata.getFileId()).thenReturn(expectedFileId);

        // Repository의 save 메서드가 호출되면 위에서 만든 Mock 객체를 반환하도록 설정
        when(metadataRepository.save(any(CouponIssuanceFileMetadata.class))).thenReturn(savedMetadata);

        when(storageService.store(any(MockMultipartFile.class))).thenReturn(storedPath);

        // when
        String actualFileId = couponIssuanceService.uploadFile(file, couponName, expiresAt);

        // then
        // 반환된 ID가 예상 ID와 일치하는지 검증
        assertThat(actualFileId).isEqualTo(expectedFileId);
        verify(couponService, times(1)).issueCouponsInBulk(customerIds, couponName, expiresAt);
        verify(storageService, times(1)).store(file);
        verify(metadataRepository, times(1) ).save(any(CouponIssuanceFileMetadata.class));
    }

    @Test
    @DisplayName("파일 다운로드 성공")
    void downloadFile_성공() {
        // given
        String fileId = "test_file_id";
        String originalFileName = "customers.csv";
        String storedPath = "/path/to/stored/file.csv";
        byte[] content = "test content".getBytes();

        CouponIssuanceFileMetadata mockMetadata = CouponIssuanceFileMetadata.builder()
                .originalFileName(originalFileName)
                .storedFilePath(storedPath)
                .build();

        when(metadataRepository.findById(fileId)).thenReturn(Optional.of(mockMetadata));
        when(storageService.load(storedPath)).thenReturn(content);

        // when
        FileDownloadResponseDTO result = couponIssuanceService.downloadFile(fileId);

        // then
        assertThat(result.originalFileName()).isEqualTo(originalFileName);
        assertThat(result.content()).isEqualTo(content);
    }

    @Test
    @DisplayName("파일 다운로드 실패 - 파일을 찾을 수 없음")
    void downloadFile_실패_파일_찾을_수_없음() {
        // given
        String fileId = "non_existent_id";
        when(metadataRepository.findById(fileId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> couponIssuanceService.downloadFile(fileId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_NOT_FOUND);
    }
}
