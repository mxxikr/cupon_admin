package com.mxxikr.couponadmin.domain;

import com.mxxikr.couponadmin.common.exception.BusinessException;
import com.mxxikr.couponadmin.common.exception.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;

/**
 * 쿠폰 발급을 위한 업로드 파일을 나타내는 도메인 객체
 */
public class CouponIssuanceFile {

    private final MultipartFile file; // 업로드된 파일
    private final FileParser fileParser; // 파일 파서 인터페이스

    /**
     * CouponIssuanceFile 객체를 생성함
     * @param file 업로드 된 MultipartFile
     * @throws BusinessException 파일이 비어있을 경우 발생
     */
    public CouponIssuanceFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ErrorCode.FILE_IS_EMPTY);
        }
        this.file = file;
        // 파일 확장자에 따라 적절한 파서를 선택함
        this.fileParser = getFileParser(Objects.requireNonNull(file.getOriginalFilename()));
    }

    /**
     * 파일에서 고객 ID 목록을 추출함
     * @return 추출 된 고객 ID 목록
     * @throws BusinessException 파일 파싱 실패 시 발생
     */
    public List<Long> getCustomerIds() {
        try (InputStream inputStream = file.getInputStream()) {
            return fileParser.parse(inputStream);
        } catch (BusinessException e) {
            throw e; // 이미 BusinessException으로 처리된 예외는 그대로 던짐
        } catch (Exception e) {
            // 그 외 예외 발생 시 파일 파싱 실패 예외로 처리함
            throw new BusinessException(ErrorCode.FILE_PARSING_FAILED, e);
        }
    }

    /**
     * 파일 확장자에 따라 FileParser 구현체를 반환함
     * @param filename 파일명
     * @return FileParser 구현체
     * @throws BusinessException 지원하지 않는 파일 형식일 경우 발생
     */
    private FileParser getFileParser(String filename) {
        if (filename.toLowerCase().endsWith(".csv")) {
            return new CsvFileParser();
        } else if (filename.toLowerCase().endsWith(".xls") || filename.toLowerCase().endsWith(".xlsx")) {
            return new ExcelFileParser();
        } else {
            throw new BusinessException(ErrorCode.UNSUPPORTED_FILE_TYPE);
        }
    }

    /**
     * 원본 파일명을 반환함
     * @return 원본 파일명
     */
    public String getOriginalFilename() {
        return file.getOriginalFilename();
    }

    /**
     * 파일의 바이트 배열을 반환함
     * @return 파일의 바이트 배열
     * @throws BusinessException 파일 내용을 읽을 수 없을 경우 발생
     */
    public byte[] getBytes() {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.FILE_READ_FAILED, e);
        }
    }
}