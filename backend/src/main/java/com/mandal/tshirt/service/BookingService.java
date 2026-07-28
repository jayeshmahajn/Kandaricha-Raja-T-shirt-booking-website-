package com.mandal.tshirt.service;

import com.mandal.tshirt.dto.BookingRequest;
import com.mandal.tshirt.entity.Booking;
import com.mandal.tshirt.repository.BookingRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private static final String[] HEADERS = {"Sr No", "Full Name", "T-Shirt Size", "Sleeve Type", "Phone Number", "Booked On"};
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void saveBooking(BookingRequest request) {
        Booking booking = new Booking();
        booking.setFullName(request.getFullName().trim());
        booking.setSize(request.getSize().trim());
        booking.setSleeveType(request.getSleeveType().trim());
        booking.setPhoneNumber(request.getPhoneNumber().trim());
        bookingRepository.save(booking);
    }

    public List<Map<String, String>> getAllBookingsAsMap() {
        List<Booking> bookings = bookingRepository.findAll();
        List<Map<String, String>> result = new ArrayList<>();
        
        long srNo = 1;
        for (Booking b : bookings) {
            Map<String, String> entry = new LinkedHashMap<>();
            entry.put("id", String.valueOf(b.getId()));
            entry.put("Sr No", String.valueOf(srNo++));
            entry.put("Full Name", b.getFullName());
            entry.put("T-Shirt Size", b.getSize());
            entry.put("Sleeve Type", b.getSleeveType());
            entry.put("Phone Number", b.getPhoneNumber());
            entry.put("Booked On", b.getBookedOn() != null ? b.getBookedOn().format(TIMESTAMP_FORMAT) : "");
            result.add(entry);
        }
        return result;
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public byte[] generateExcelBytes() throws IOException {
        List<Booking> bookings = bookingRepository.findAll();
        
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Bookings");

            // Header row formatting
            CellStyle headerStyle = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Data rows
            int rowIdx = 1;
            for (Booking booking : bookings) {
                Row row = sheet.createRow(rowIdx);
                row.createCell(0).setCellValue(rowIdx); // Sr No
                row.createCell(1).setCellValue(booking.getFullName());
                row.createCell(2).setCellValue(booking.getSize());
                row.createCell(3).setCellValue(booking.getSleeveType());
                row.createCell(4).setCellValue(booking.getPhoneNumber());
                row.createCell(5).setCellValue(booking.getBookedOn() != null ? booking.getBookedOn().format(TIMESTAMP_FORMAT) : "");
                rowIdx++;
            }

            // Auto-size columns
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return out.toByteArray();
        }
    }
}
