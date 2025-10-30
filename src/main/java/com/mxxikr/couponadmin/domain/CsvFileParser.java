package com.mxxikr.couponadmin.domain;

import com.mxxikr.couponadmin.common.FileConstants;
import com.mxxikr.couponadmin.common.exception.BusinessException;
import com.mxxikr.couponadmin.common.exception.ErrorCode;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * CSV 파일 파서 구현체
 */
public class CsvFileParser implements FileParser {

    @Override
    public List<Long> parse(InputStream inputStream) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
            String[] header = reader.readNext();
            validateHeader(header);

            List<Long> customerIds = new ArrayList<>();
            String[] line;
            while ((line = reader.readNext()) != null) {
                if (line.length > 0 && !line[0].trim().isEmpty()) {
                    try {
                        customerIds.add(Long.parseLong(line[0].trim()));
                    } catch (NumberFormatException e) {
                        // 숫자 형식으로 변환할 수 없는 값은 무시함
                    }
                }
            }

            if (customerIds.isEmpty()) {
                throw new BusinessException(ErrorCode.EMPTY_CUSTOMER_LIST);
            }
            return customerIds;
        }
    }

    /**
     * 헤더의 유효성을 검증함. 첫 번째 열의 헤더는 'customer_id'여야 함
     * @param header 헤더 배열
     */
    private void validateHeader(String[] header) {
        if (header == null || header.length == 0 || !Objects.equals(header[0], FileConstants.CUSTOMER_ID_HEADER)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_HEADER);
        }
    }
}