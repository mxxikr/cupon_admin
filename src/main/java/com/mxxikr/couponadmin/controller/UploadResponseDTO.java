package com.mxxikr.couponadmin.controller;

/**
 * 파일 업로드 성공 시 응답 DTO
 * @param fileId 업로드된 파일의 고유 ID
 */
public record UploadResponseDTO(String fileId) {
}
