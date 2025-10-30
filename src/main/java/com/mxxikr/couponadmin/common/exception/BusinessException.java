package com.mxxikr.couponadmin.common.exception;

import lombok.Getter;

/**
 * 비즈니스 로직 처리 중 발생하는 예외를 위한 커스텀 예외 클래스
 */
@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;

    /**
     * ErrorCode를 사용하여 BusinessException을 생성함
     * @param errorCode 발생한 에러 코드
     */
    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    /**
     * ErrorCode와 Throwable을 사용하여 BusinessException을 생성함
     * @param errorCode 발생한 에러 코드
     * @param cause 예외의 근본 원인
     */
    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
    }
}