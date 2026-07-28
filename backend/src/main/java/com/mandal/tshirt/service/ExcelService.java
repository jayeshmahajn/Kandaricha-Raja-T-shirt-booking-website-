package com.mandal.tshirt.service;

import com.mandal.tshirt.dto.BookingRequest;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * Every booking is stored directly as a row in an Excel workbook.
 * There is no database - this class is the single source of truth
 * for reading and writing bookings.
 *
 * All calls are synchronized because Apache POI has to read the whole
 * file into memory, add a row, and re-save it - two people booking a
 * t-shirt at the exact same second must not overwrite each other.
 */
@Service
public class ExcelService {

    private static final String[] HEADERS = {"Sr No", "Full Name", "T-Shirt Size", "Sleeve Type", "Phone Number", "Booked On"};
    private static final DateTimeFormatter TIMESTAMP_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    @Value("${app.excel.file-path}")
    private String excelFilePath;

    @PostConstruct
    public void migrateOldBookings() {
        File file = new File(excelFilePath);
        if (!file.exists()) return;
        
        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {
             
            Sheet sheet = workbook.getSheet("Bookings");
            if (sheet == null) return;
            
            boolean modified = false;
            
            // 1. Rewrite Header Row
            Row headerRow = sheet.getRow(0);
            if (headerRow != null) {
                Cell hCell = headerRow.getCell(3);
                if (hCell == null || !hCell.getStringCellValue().equals("Sleeve Type")) {
                    writeHeaderRow(sheet, workbook);
                    modified = true;
                }
            }
            
            // 2. Fix old rows
            DataFormatter formatter = new DataFormatter();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;
                
                // If it only has 5 columns (Sr No, Name, Size, Phone, Date)
                if (row.getLastCellNum() == 5) {
                    Cell oldPhone = row.getCell(3);
                    Cell oldDate = row.getCell(4);
                    
                    String phoneVal = oldPhone != null ? formatter.formatCellValue(oldPhone) : "";
                    String dateVal = oldDate != null ? formatter.formatCellValue(oldDate) : "";
                    
                    row.createCell(3).setCellValue("N/A");
                    row.createCell(4).setCellValue(phoneVal);
                    row.createCell(5).setCellValue(dateVal);
                    modified = true;
                }
            }
            
            if (modified) {
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    workbook.write(fos);
                }
                System.out.println("Migrated old Excel file to new column format.");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void saveBooking(BookingRequest booking) throws IOException {
        File file = new File(excelFilePath);
        file.getParentFile().mkdirs();

        Workbook workbook;
        Sheet sheet;

        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                workbook = new XSSFWorkbook(fis);
            }
            sheet = workbook.getSheet("Bookings");
            if (sheet == null) {
                sheet = workbook.createSheet("Bookings");
                writeHeaderRow(sheet, workbook);
            }
        } else {
            workbook = new XSSFWorkbook();
            sheet = workbook.createSheet("Bookings");
            writeHeaderRow(sheet, workbook);
        }

        int nextRowNum = sheet.getLastRowNum() + 1;
        Row row = sheet.createRow(nextRowNum);

        row.createCell(0).setCellValue(nextRowNum); // Sr No (header row is 0)
        row.createCell(1).setCellValue(booking.getFullName().trim());
        row.createCell(2).setCellValue(booking.getSize().trim());
        row.createCell(3).setCellValue(booking.getSleeveType().trim());

        // Force phone number to be stored as text so leading nothing is lost
        // and Excel doesn't try to reformat it as a number.
        Cell phoneCell = row.createCell(4);
        phoneCell.setCellValue(booking.getPhoneNumber().trim());

        row.createCell(5).setCellValue(LocalDateTime.now().format(TIMESTAMP_FORMAT));

        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }

        try (FileOutputStream fos = new FileOutputStream(file)) {
            workbook.write(fos);
        }
        workbook.close();
    }

    private void writeHeaderRow(Sheet sheet, Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.ORANGE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        Row header = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
    }

    /** Reads every booking back out - used by the admin dashboard. */
    public synchronized List<Map<String, String>> getAllBookings() throws IOException {
        List<Map<String, String>> bookings = new ArrayList<>();
        File file = new File(excelFilePath);
        if (!file.exists()) return bookings;

        try (FileInputStream fis = new FileInputStream(file);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet("Bookings");
            if (sheet == null) return bookings;

            DataFormatter formatter = new DataFormatter();
            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) continue;

                Map<String, String> entry = new LinkedHashMap<>();
                int lastCell = row.getLastCellNum(); // Will be 5 for old rows, 6 for new rows

                for (int c = 0; c < HEADERS.length; c++) {
                    String value = "";
                    if (lastCell == 5) {
                        // Backwards compatibility for old bookings before "Sleeve Type" was added
                        if (c == 3) {
                            value = "N/A"; // Sleeve Type not provided
                        } else if (c == 4) {
                            Cell cell = row.getCell(3);
                            value = cell == null ? "" : formatter.formatCellValue(cell);
                        } else if (c == 5) {
                            Cell cell = row.getCell(4);
                            value = cell == null ? "" : formatter.formatCellValue(cell);
                        } else {
                            Cell cell = row.getCell(c);
                            value = cell == null ? "" : formatter.formatCellValue(cell);
                        }
                    } else {
                        // Standard mapping for new bookings
                        Cell cell = row.getCell(c);
                        value = cell == null ? "" : formatter.formatCellValue(cell);
                    }
                    entry.put(HEADERS[c], value);
                }
                bookings.add(entry);
            }
        }
        return bookings;
    }

    public File getExcelFile() {
        return new File(excelFilePath);
    }
}
