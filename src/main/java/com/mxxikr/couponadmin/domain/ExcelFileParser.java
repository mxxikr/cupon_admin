package com.mxxikr.couponadmin.domain;

import com.mxxikr.couponadmin.common.FileConstants;
import com.mxxikr.couponadmin.common.exception.BusinessException;
import com.mxxikr.couponadmin.common.exception.ErrorCode;
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Excel 파일 파서 구현체
 */
public class ExcelFileParser implements FileParser {

    @Override
    public List<Long> parse(InputStream inputStream) throws Exception {
        Workbook workbook = WorkbookFactory.create(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        validateHeader(sheet.getRow(0));

        List<Long> customerIds = extractCustomerIds(sheet);

        if (customerIds.isEmpty()) {
            throw new BusinessException(ErrorCode.EMPTY_CUSTOMER_LIST);
        }

        return customerIds;
    }

    /**
     * 헤더의 유효성을 검증함
     * 첫 번째 열의 헤더는 customer_id여야 함
     * @param headerRow 헤더 행
     */
    private void validateHeader(Row headerRow) {
        if (headerRow == null || headerRow.getCell(0) == null || !Objects.equals(headerRow.getCell(0).getStringCellValue(), FileConstants.CUSTOMER_ID_HEADER)) {
            throw new BusinessException(ErrorCode.INVALID_FILE_HEADER);
        }
    }

    /**
     * Excel 시트에서 고객 ID를 추출함
     * @param sheet Excel 시트
     * @return 고객 ID 목록
     */
    private List<Long> extractCustomerIds(Sheet sheet) {
        List<Long> customerIds = new ArrayList<>();
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row != null) {
                Cell cell = row.getCell(0);
                if (cell != null && cell.getCellType() == CellType.NUMERIC) {
                    customerIds.add((long) cell.getNumericCellValue());
                } else if (cell != null && cell.getCellType() == CellType.STRING) {
                    try {
                        customerIds.add(Long.parseLong(cell.getStringCellValue()));
                    } catch (NumberFormatException e) {
                        // 숫자 형식으로 변환할 수 없는 문자열은 무시함
                    }
                }
            }
        }
        return customerIds;
    }
}