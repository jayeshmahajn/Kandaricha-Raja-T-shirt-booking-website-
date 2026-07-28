package com.mandal.tshirt.controller;

import com.mandal.tshirt.config.AdminAuth;
import com.mandal.tshirt.dto.ApiResponse;
import com.mandal.tshirt.dto.BookingRequest;
import com.mandal.tshirt.service.BookingService;
import jakarta.validation.Valid;
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

    private final BookingService bookingService;
    private final AdminAuth adminAuth;

    public BookingController(BookingService bookingService, AdminAuth adminAuth) {
        this.bookingService = bookingService;
        this.adminAuth = adminAuth;
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        bookingService.saveBooking(request);
        return ResponseEntity.ok(new ApiResponse(true, "Booking saved! जय गणेश 🙏"));
    }

    @GetMapping
    public ResponseEntity<?> getAllBookings(@RequestHeader("X-Admin-Key") String adminKey) {
        if (!adminAuth.isValid(adminKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, "Invalid admin key"));
        }
        List<Map<String, String>> bookings = bookingService.getAllBookingsAsMap();
        return ResponseEntity.ok(new ApiResponse(true, "OK", bookings));
    }

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadExcel(@RequestHeader("X-Admin-Key") String adminKey) {
        if (!adminAuth.isValid(adminKey)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            byte[] excelBytes = bookingService.generateExcelBytes();
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"bookings.xlsx\"")
                    .body(excelBytes);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
