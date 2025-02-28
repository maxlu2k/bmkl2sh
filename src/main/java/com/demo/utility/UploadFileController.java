package com.demo.utility;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/file")
public class UploadFileController {
    private String fileLocation;

    @PostMapping("/uploadExcelFile")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream in = file.getInputStream();
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        fileLocation = path.substring(0, path.length() - 1) + file.getOriginalFilename();

        try (FileOutputStream f = new FileOutputStream(fileLocation)) {
            int ch;
            while ((ch = in.read()) != -1) {
                f.write(ch);
            }
        }
        return ResponseEntity.ok().body("{\"message\": \"File: " + file.getOriginalFilename() + " has been uploaded successfully!\"}");
    }
}
