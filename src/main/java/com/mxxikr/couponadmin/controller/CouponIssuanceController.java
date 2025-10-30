package com.mxxikr.couponadmin.controller;

import com.mxxikr.couponadmin.application.CouponIssuanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 쿠폰 발급 관련 API 요청을 처리
 */
@Tag(name = "쿠폰 대량 발급 API", description = "파일을 이용한 쿠폰 대량 발급 관련 API")
@RestController
@RequestMapping("/api/coupon-issuances")
public class CouponIssuanceController {

    private final CouponIssuanceService couponIssuanceService;

    public CouponIssuanceController(CouponIssuanceService couponIssuanceService) {
        this.couponIssuanceService = couponIssuanceService;
    }

    @Operation(summary = "쿠폰 발급 파일 업로드", description = "CSV 또는 Excel 형식의 사용자 목록 파일을 업로드하여 쿠폰 발급을 요청")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponseDTO> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam("couponName") String couponName,
            @RequestParam("expiresAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime expiresAt) {
        String fileId = couponIssuanceService.uploadFile(file, couponName, expiresAt);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UploadResponseDTO(fileId));
    }

    @Operation(summary = "업로드된 파일 다운로드", description = "업로드 시 반환된 파일 ID를 사용하여 원본 파일을 다운로드")
    @GetMapping("/{fileId}/download")
    public ResponseEntity<byte[]> downloadFile(@PathVariable String fileId) {
        FileDownloadResponseDTO fileDto = couponIssuanceService.downloadFile(fileId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", fileDto.originalFileName());

        return new ResponseEntity<>(fileDto.content(), headers, HttpStatus.OK);
    }
}