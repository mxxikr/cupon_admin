package com.mxxikr.couponadmin.controller;

/**
 * 파일 다운로드 시 DTO
 * @param originalFileName 원본 파일명
 * @param content 파일의 바이트 배열
 */
public record FileDownloadResponseDTO(String originalFileName, byte[] content) {
}
