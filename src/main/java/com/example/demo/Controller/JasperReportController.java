package com.example.demo.Controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Service.JasperReportService;

@RestController
@RequestMapping("/reports")
public class JasperReportController {
    private final JasperReportService jasperReportService;

    public JasperReportController(JasperReportService jasperReportService) {
        this.jasperReportService = jasperReportService;
    }

    // API 1: Xuất báo cáo ra PDF
    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> exportPdf(@RequestParam String reportName,
            @RequestParam(required = false) String filter) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            if (filter != null) {
                parameters.put("FILTER_PARAM", filter);
            }

            byte[] pdfData = jasperReportService.exportReportToPdf(reportName, parameters);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment")
                    .filename(reportName + ".pdf")
                    .build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfData);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(("Lỗi xuất báo cáo PDF: " + e.getMessage()).getBytes());
        }
    }

    // API 2: Xuất báo cáo ra Excel (.xlsx)
    @GetMapping("/export/xlsx")
    public ResponseEntity<byte[]> exportXlsx(@RequestParam String reportName,
            @RequestParam(required = false) String filter) {
        try {
            Map<String, Object> parameters = new HashMap<>();
            if (filter != null) {
                parameters.put("FILTER_PARAM", filter);
            }

            byte[] data = jasperReportService.exportReportToXlsx(reportName, parameters);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportName + ".xlsx")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(data);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(("Lỗi xuất báo cáo XLSX: " + e.getMessage()).getBytes());
        }
    }
}
