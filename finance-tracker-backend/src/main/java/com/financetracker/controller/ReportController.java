package com.financetracker.controller;

import com.financetracker.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // GET /api/reports/monthly?month=7&year=2026
    @GetMapping("/monthly")
    public ResponseEntity<byte[]> downloadMonthlyReport(@RequestParam int month, @RequestParam int year) {
        byte[] pdfBytes = reportService.generateMonthlyReport(month, year);

        String filename = "finance-report-" + year + "-" + String.format("%02d", month) + ".pdf";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename).build());

        return ResponseEntity.ok().headers(headers).body(pdfBytes);
    }
}
