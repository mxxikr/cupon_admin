package com.mxxikr.couponadmin.domain;

import com.mxxikr.couponadmin.common.exception.BusinessException;
import com.mxxikr.couponadmin.common.exception.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CouponIssuanceFileTest {

    @Mock
    private MultipartFile mockMultipartFile;

    @Test
    @DisplayName("CouponIssuanceFile 생성 성공")
    void constructor_성공() throws IOException {
        // given
        MockMultipartFile file = new MockMultipartFile("file", "test.csv", "text/csv", "customer_id\n123".getBytes());

        // when
        CouponIssuanceFile couponIssuanceFile = new CouponIssuanceFile(file);

        // then
        assertThat(couponIssuanceFile).isNotNull();
        assertThat(couponIssuanceFile.getOriginalFilename()).isEqualTo("test.csv");
    }

    @Test
    @DisplayName("CouponIssuanceFile 생성 실패 - 파일이 비어있음")
    void constructor_실패_파일_비어있음() {
        // given
        MultipartFile emptyFile = new MockMultipartFile("file", "empty.csv", "text/csv", new byte[0]);

        // when & then
        assertThatThrownBy(() -> new CouponIssuanceFile(emptyFile))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_IS_EMPTY);
    }

    @Test
    @DisplayName("고객 ID 추출 성공 - CSV 파일")
    void getCustomerIds_성공_CSV() throws IOException {
        // given
        String csvContent = "customer_id\n111\n222";
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.csv");
        when(mockMultipartFile.getInputStream()).thenReturn(new ByteArrayInputStream(csvContent.getBytes(StandardCharsets.UTF_8)));

        CouponIssuanceFile couponIssuanceFile = new CouponIssuanceFile(mockMultipartFile);

        // when
        List<Long> customerIds = couponIssuanceFile.getCustomerIds();

        // then
        assertThat(customerIds).containsExactly(111L, 222L);
    }

    @Test
    @DisplayName("고객 ID 추출 실패 - 지원하지 않는 파일 형식")
    void getCustomerIds_실패_지원하지_않는_파일_형식() {
        // given
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.txt");

        // when & then
        assertThatThrownBy(() -> new CouponIssuanceFile(mockMultipartFile))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.UNSUPPORTED_FILE_TYPE);
    }

    @Test
    @DisplayName("고객 ID 추출 실패 - 파일 파싱 오류")
    void getCustomerIds_실패_파일_파싱_오류() throws IOException {
        // given
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.csv");
        // InputStream에서 IOException 발생하도록 Mocking
        when(mockMultipartFile.getInputStream()).thenThrow(new IOException("Mock IO Exception"));

        CouponIssuanceFile couponIssuanceFile = new CouponIssuanceFile(mockMultipartFile);

        // when & then
        assertThatThrownBy(couponIssuanceFile::getCustomerIds)
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_PARSING_FAILED);
    }

    @Test
    @DisplayName("파일 바이트 배열 가져오기 성공")
    void getBytes_성공() throws IOException {
        // given
        byte[] testBytes = "test content".getBytes(StandardCharsets.UTF_8);
        when(mockMultipartFile.getBytes()).thenReturn(testBytes);
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.csv"); // 생성자 요구사항

        CouponIssuanceFile couponIssuanceFile = new CouponIssuanceFile(mockMultipartFile);

        // when
        byte[] resultBytes = couponIssuanceFile.getBytes();

        // then
        assertThat(resultBytes).isEqualTo(testBytes);
    }

    @Test
    @DisplayName("파일 바이트 배열 가져오기 실패 - IO 오류")
    void getBytes_실패_IO_오류() throws IOException {
        // given
        when(mockMultipartFile.getBytes()).thenThrow(new IOException("Mock IO Exception"));
        when(mockMultipartFile.getOriginalFilename()).thenReturn("test.csv"); // 생성자 요구사항

        CouponIssuanceFile couponIssuanceFile = new CouponIssuanceFile(mockMultipartFile);

        // when & then
        assertThatThrownBy(couponIssuanceFile::getBytes)
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.FILE_READ_FAILED); // 수정된 부분
    }
}
