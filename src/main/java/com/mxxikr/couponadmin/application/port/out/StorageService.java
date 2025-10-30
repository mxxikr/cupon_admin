package com.mxxikr.couponadmin.application.port.out;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 저장을 위한 Outgoing Port
 */
public interface StorageService {

    /**
     * 파일을 저장하고 저장된 경로/ID를 반환함
     * @param file 저장할 MultipartFile
     * @return 저장된 파일의 경로 또는 식별자
     */
    String store(MultipartFile file);

    /**
     * 저장된 경로의 파일 내용을 불러옴
     * @param storedPath 저장된 파일의 경로 또는 식별자
     * @return 파일의 바이트 배열
     */
    byte[] load(String storedPath);
}
