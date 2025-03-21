package com.example.assignment_java5.controller;

import com.example.assignment_java5.model.hoadon;
import com.example.assignment_java5.repository.hoadonrepository;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Controller
public class InvoicePDFController {

    @Autowired
    private hoadonrepository hoadonRepository;

    @GetMapping("/invoice/pdf")
    public void generateInvoicePDF(@RequestParam Long hoadonId, HttpServletResponse response) throws IOException {
        Optional<hoadon> optionalHoaDon = hoadonRepository.findById(Math.toIntExact(hoadonId));
        if (optionalHoaDon.isEmpty()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Hóa đơn không tồn tại.");
            return;
        }

        hoadon hoaDon = optionalHoaDon.get();

        // 🛠 Thiết lập response để tải file PDF
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=hoa_don_" + hoadonId + ".pdf");

        // 📝 Tạo file PDF
        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // 🔹 Nhúng font Unicode hỗ trợ tiếng Việt
        String fontPath = "src/main/resources/static/fonts/Arial.ttf";  // Đường dẫn font chữ
        PdfFont font = PdfFontFactory.createFont(fontPath, "Identity-H", true);
        document.setFont(font);

        // 🎨 Cấu hình font và căn chỉnh
        document.setFontSize(12);
        document.setTextAlignment(TextAlignment.LEFT);

        // 🔹 Tiêu đề hóa đơn
        Paragraph title = new Paragraph("HÓA ĐƠN THANH TOÁN")
                .setBold()
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(new DeviceRgb(0, 102, 204));
        document.add(title);

        // 🔹 Thông tin người mua
        document.add(new Paragraph("\nTên khách hàng: " + hoaDon.getNguoiMua().getTenNhanVien()).setBold());
        document.add(new Paragraph("Số điện thoại: " + hoaDon.getNguoiMua().getSoDienThoai()));
        document.add(new Paragraph("Địa chỉ: " + hoaDon.getNguoiMua().getDiaChi()));

        // 🔹 Ngày lập hóa đơn
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        document.add(new Paragraph("Ngày lập hóa đơn: " + hoaDon.getNgayLap().format(formatter)));

        // 🔹 Tạo bảng tổng tiền
        float[] columnWidths = {2, 4};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();
        table.setMarginTop(20);

        table.addCell(createHeaderCell("Mục", font));
        table.addCell(createHeaderCell("Giá trị", font));

        table.addCell(createCell("Tổng tiền", font));
        table.addCell(createCell(formatCurrency(hoaDon.getTongTien()) + " VND", font));

        document.add(table);

        // 🔹 Cảm ơn khách hàng
        document.add(new Paragraph("\nCảm ơn quý khách đã mua hàng!").setBold().setFontColor(new DeviceRgb(0, 153, 51)).setFontSize(14));

        // 🏁 Đóng file PDF
        document.close();
    }

    // 🎨 Hàm tạo ô tiêu đề bảng
    private Cell createHeaderCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setBold().setFontSize(12).setFont(font))
                .setBackgroundColor(new DeviceRgb(200, 200, 200))
                .setPadding(5)
                .setTextAlignment(TextAlignment.CENTER);
    }

    // 🎨 Hàm tạo ô dữ liệu bảng
    private Cell createCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font))
                .setPadding(5)
                .setTextAlignment(TextAlignment.LEFT);
    }

    // 🎨 Hàm định dạng tiền tệ
    private String formatCurrency(Number number) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(number);
    }
}
