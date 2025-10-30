package com.mxxikr.couponadmin.common.exception;

import lombok.Builder;
import lombok.Getter;

/**
 * API 에러 발생 시 클라이언트에게 반환되는 응답 형식
 */
@Getter
@Builder
public class ErrorResponse {
    private final String code; // 에러 코드
    private final String message; // 에러 메시지
}