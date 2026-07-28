package com.mandal.tshirt.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    public String storeImage(MultipartFile file) throws IOException {
        File dir = new File(uploadDir);
        if (!dir.exists()) dir.mkdirs();

        String originalName = file.getOriginalFilename() == null ? "design" : file.getOriginalFilename();
        String extension = "";
        int dot = originalName.lastIndexOf('.');
        if (dot >= 0) extension = originalName.substring(dot);

        String safeName = UUID.randomUUID() + extension;
        Path target = Path.of(uploadDir, safeName);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return safeName;
    }

    public List<String> listImages() {
        File dir = new File(uploadDir);
        if (!dir.exists()) return List.of();
        File[] files = dir.listFiles(File::isFile);
        if (files == null) return List.of();

        return Arrays.stream(files)
                .sorted(Comparator.comparingLong(File::lastModified).reversed())
                .map(File::getName)
                .collect(Collectors.toList());
    }

    public boolean deleteImage(String fileName) {
        // guard against path traversal, e.g. "../../etc"
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            return false;
        }
        File f = new File(uploadDir, fileName);
        return f.exists() && f.delete();
    }
}
