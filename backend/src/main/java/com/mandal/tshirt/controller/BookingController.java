package com.mandal.tshirt.controller;

import com.mandal.tshirt.config.AdminAuth;
import com.mandal.tshirt.dto.ApiResponse;
import com.mandal.tshirt.dto.BookingRequest;
import com.mandal.tshirt.service.ExcelService;
import jakarta.validation.Valid;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final ExcelService excelService;
    private final AdminAuth adminAuth;

    public BookingController(ExcelService excelService, AdminAuth adminAuth) {
        this.excelService = excelService;
        this.adminAuth = adminAuth;
    }

    /** Anyone can submit the t-shirt booking form. */
    @PostMapping
    public ResponseEntity<ApiResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        try {
            excelService.saveBooking(request);
            return ResponseEntity.ok(new ApiResponse(true, "Booking saved! जय गणेश 🙏"));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Could not save booking, please try again."));
        }
    }

    /** Admin-only: view every booking that's been collected so far. */
    @GetMapping
    public ResponseEntity<?> getAllBookings(@RequestHeader("X-Admin-Key") String adminKey) {
        if (!adminAuth.isValid(adminKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid admin key"));
        }
        try {
            List<Map<String, String>> bookings = excelService.getAllBookings();
            return ResponseEntity.ok(new ApiResponse(true, "OK", bookings));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Could not read bookings"));
        }
    }

    /** Admin-only: download the raw bookings.xlsx file. */
    @GetMapping("/download")
    public ResponseEntity<FileSystemResource> downloadExcel(@RequestHeader("X-Admin-Key") String adminKey) {
        if (!adminAuth.isValid(adminKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        FileSystemResource resource = new FileSystemResource(excelService.getExcelFile());
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bookings.xlsx\"")
                .body(resource);
    }
}
