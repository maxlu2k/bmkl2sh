package com.demo.services;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

@Service
public class UploadService {
    private static final String UPLOAD_DIR="uploads/";

    @PreAuthorize("hasRole('ADMIN')")
    public String uploadFile(MultipartFile file, String subFolder) throws IOException  {
            // Tạo thư mục nếu chưa có
            Path uploadPath = Paths.get(UPLOAD_DIR + subFolder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Lưu file vào thư mục
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption .REPLACE_EXISTING);

            return filePath.toString(); // Trả về đường dẫn file đã lưu
    }
}
