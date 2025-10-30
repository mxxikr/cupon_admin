package com.mxxikr.couponadmin.domain;

import com.mxxikr.couponadmin.common.exception.BusinessException;
import com.mxxikr.couponadmin.common.exception.ErrorCode;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExcelFileParserTest {

    private final ExcelFileParser excelFileParser = new ExcelFileParser();

    @Test
    @DisplayName("Excel 파싱 성공")
    void parse_성공() throws Exception {
        // given
        InputStream inputStream = createExcelInputStream("customer_id", List.of("11111", "22222", "33333"));

        // when
        List<Long> customerIds = excelFileParser.parse(inputStream);

        // then
        assertThat(customerIds).hasSize(3);
        assertThat(customerIds).containsExactly(11111L, 22222L, 33333L);
    }

    @Test
    @DisplayName("Excel 파싱 실패 - 유효하지 않은 헤더")
    void parse_실패_유효하지_않은_헤더() throws IOException {
        // given
        InputStream inputStream = createExcelInputStream("invalid_header", List.of("11111"));

        // when & then
        assertThatThrownBy(() -> excelFileParser.parse(inputStream))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_FILE_HEADER);
    }

    @Test
    @DisplayName("Excel 파싱 실패 - 고객 목록이 비어있음")
    void parse_실패_고객_목록_비어있음() throws IOException {
        // given
        InputStream inputStream = createExcelInputStream("customer_id", List.of());

        // when & then
        assertThatThrownBy(() -> excelFileParser.parse(inputStream))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMPTY_CUSTOMER_LIST);
    }

    @Test
    @DisplayName("Excel 파싱 시 숫자 아닌 값은 무시")
    void parse_성공_숫자_아닌_값_무시() throws Exception {
        // given
        InputStream inputStream = createExcelInputStream("customer_id", List.of("11111", "not_a_number", "33333"));

        // when
        List<Long> customerIds = excelFileParser.parse(inputStream);

        // then
        assertThat(customerIds).hasSize(2);
        assertThat(customerIds).containsExactly(11111L, 33333L);
    }

    // 테스트용 Excel 파일의 InputStream을 생성하는 헬퍼 메서드
    private InputStream createExcelInputStream(String header, List<String> values) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Customers");

            // Header
            Row headerRow = sheet.createRow(0);
            Cell headerCell = headerRow.createCell(0);
            headerCell.setCellValue(header);

            // Values
            for (int i = 0; i < values.size(); i++) {
                Row row = sheet.createRow(i + 1);
                Cell cell = row.createCell(0);
                try {
                    double numericValue = Double.parseDouble(values.get(i));
                    cell.setCellValue(numericValue);
                } catch (NumberFormatException e) {
                    cell.setCellValue(values.get(i));
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
