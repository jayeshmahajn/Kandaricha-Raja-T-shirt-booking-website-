package com.mandal.tshirt.controller;

import com.mandal.tshirt.config.AdminAuth;
import com.mandal.tshirt.dto.ApiResponse;
import com.mandal.tshirt.service.FileStorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/designs")
public class DesignController {

    private final FileStorageService fileStorageService;
    private final AdminAuth adminAuth;

    public DesignController(FileStorageService fileStorageService, AdminAuth adminAuth) {
        this.fileStorageService = fileStorageService;
        this.adminAuth = adminAuth;
    }

    /** Public: anyone visiting the site can see the uploaded t-shirt designs. */
    @GetMapping
    public ResponseEntity<ApiResponse> listDesigns() {
        List<String> images = fileStorageService.listImages();
        return ResponseEntity.ok(new ApiResponse(true, "OK", images));
    }

    /** Admin-only: upload a new t-shirt design photo. */
    @PostMapping
    public ResponseEntity<ApiResponse> uploadDesign(
            @RequestHeader("X-Admin-Key") String adminKey,
            @RequestParam("file") MultipartFile file) {

        if (!adminAuth.isValid(adminKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Invalid admin key"));
        }
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "No file provided"));
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            return ResponseEntity.badRequest().body(new ApiResponse(false, "Only image files are allowed"));
        }

        try {
            String storedName = fileStorageService.storeImage(file);
            return ResponseEntity.ok(new ApiResponse(true, "Design uploaded", storedName));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Upload failed"));
        }
    }

    /** Admin-only: remove a design. */
    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteDesign(
            @RequestHeader("X-Admin-Key") String adminKey,
            @RequestParam("url") String url) {

        if (!adminAuth.isValid(adminKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse(false, "Invalid admin key"));
        }
        boolean deleted = fileStorageService.deleteImage(url);
        if (deleted) {
            return ResponseEntity.ok(new ApiResponse(true, "Deleted"));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(false, "File not found"));
    }
}
