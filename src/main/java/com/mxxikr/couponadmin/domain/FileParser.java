package com.mxxikr.couponadmin.domain;

import java.io.InputStream;
import java.util.List;

/**
 * 파일 파싱을 위한 인터페이스 정의함
 */
public interface FileParser {
    /**
     * 입력 스트림으로부터 고객 ID 목록을 파싱함
     * @param inputStream 파일의 입력 스트림
     * @return 파싱된 고객 ID 목록
     * @throws Exception
     */
    List<Long> parse(InputStream inputStream) throws Exception;
}