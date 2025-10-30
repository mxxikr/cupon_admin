package com.mxxikr.couponadmin.domain;

import com.mxxikr.couponadmin.common.exception.BusinessException;
import com.mxxikr.couponadmin.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvFileParserTest {

    private final CsvFileParser csvFileParser = new CsvFileParser();

    @Test
    @DisplayName("CSV 파싱 성공")
    void parse_성공() throws Exception {
        // given
        String csvContent = "customer_id\n11111\n22222\n33333";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        // when
        List<Long> customerIds = csvFileParser.parse(inputStream);

        // then
        assertThat(customerIds).hasSize(3);
        assertThat(customerIds).containsExactly(11111L, 22222L, 33333L);
    }

    @Test
    @DisplayName("CSV 파싱 실패 - 유효하지 않은 헤더")
    void parse_실패_유효하지_않은_헤더() {
        // given
        String csvContent = "invalid_header\n11111\n22222";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        // when & then
        assertThatThrownBy(() -> csvFileParser.parse(inputStream))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_FILE_HEADER);
    }

    @Test
    @DisplayName("CSV 파싱 실패 - 고객 목록이 비어있음")
    void parse_실패_고객_목록_비어있음() {
        // given
        String csvContent = "customer_id";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        // when & then
        assertThatThrownBy(() -> csvFileParser.parse(inputStream))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMPTY_CUSTOMER_LIST);
    }

    @Test
    @DisplayName("CSV 파싱 시 숫자 아닌 값은 무시")
    void parse_성공_숫자_아닌_값_무시() throws Exception {
        // given
        String csvContent = "customer_id\n11111\nnot_a_number\n33333";
        InputStream inputStream = new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8));

        // when
        List<Long> customerIds = csvFileParser.parse(inputStream);

        // then
        assertThat(customerIds).hasSize(2);
        assertThat(customerIds).containsExactly(11111L, 33333L);
    }
}
