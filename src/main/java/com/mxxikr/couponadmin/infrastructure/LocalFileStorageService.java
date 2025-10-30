package com.mxxikr.couponadmin.infrastructure;

import com.mxxikr.couponadmin.application.port.out.StorageService;
import com.mxxikr.couponadmin.common.exception.BusinessException;
import com.mxxikr.couponadmin.common.exception.ErrorCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 로컬 파일 시스템을 사용하는 StorageService 구현체
 */
@Service
public class LocalFileStorageService implements StorageService {

    private final Path fileStorageLocation; // 파일이 저장될 경로

    public LocalFileStorageService(@Value("${file.upload-dir}") String uploadDir) {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            // 서비스가 생성될 때 파일 저장 디렉토리를 생성함
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.DIRECTORY_CREATION_FAILED, ex);
        }
    }

    @Override
    public String store(MultipartFile file) {
        // 파일을 서버의 지정된 경로에 저장함
        String storedFileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path targetLocation = this.fileStorageLocation.resolve(storedFileName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return targetLocation.toString(); // 저장된 전체 경로 반환
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.FILE_STORAGE_FAILED, ex);
        }
    }

    @Override
    public byte[] load(String storedPath) {
        // 저장된 경로에서 파일을 읽어옴
        try {
            Path filePath = Paths.get(storedPath);
            return Files.readAllBytes(filePath);
        } catch (IOException ex) {
            throw new BusinessException(ErrorCode.FILE_READ_FAILED, ex);
        }
    }
}
