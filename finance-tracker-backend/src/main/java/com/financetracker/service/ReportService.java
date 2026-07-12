package com.financetracker.service;

import com.financetracker.model.Transaction;
import com.financetracker.model.TransactionType;
import com.financetracker.model.User;
import com.financetracker.repository.TransactionRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TransactionRepository transactionRepository;
    private final CurrentUserProvider currentUserProvider;

    public byte[] generateMonthlyReport(int month, int year) {
        User currentUser = currentUserProvider.getCurrentUser();

        YearMonth ym = YearMonth.of(year, month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Transaction> transactions = transactionRepository
                .findByUserAndDateBetween(currentUser, start, end);

        BigDecimal totalIncome = sum(transactions, TransactionType.INCOME);
        BigDecimal totalExpense = sum(transactions, TransactionType.EXPENSE);
        BigDecimal net = totalIncome.subtract(totalExpense);

        try {
            Document document = new Document(PageSize.A4, 40, 40, 50, 50);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, baos);
            document.open();

            String monthName = ym.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);

            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Font subFont = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.GRAY);
            Font sectionFont = new Font(Font.HELVETICA, 13, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 10, Font.NORMAL);
            Font headerFont = new Font(Font.HELVETICA, 10, Font.BOLD, Color.WHITE);

            Paragraph title = new Paragraph("Monthly Finance Report", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph sub = new Paragraph(monthName + " " + year + "  |  User: " + currentUser.getUsername(), subFont);
            sub.setAlignment(Element.ALIGN_CENTER);
            sub.setSpacingAfter(20);
            document.add(sub);

            // ---- Summary table ----
            PdfPTable summaryTable = new PdfPTable(3);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingAfter(20);
            addSummaryCell(summaryTable, "Total Income", totalIncome, new Color(34, 139, 34));
            addSummaryCell(summaryTable, "Total Expense", totalExpense, new Color(178, 34, 34));
            addSummaryCell(summaryTable, "Net Balance", net, net.signum() >= 0 ? new Color(34, 139, 34) : new Color(178, 34, 34));
            document.add(summaryTable);

            // ---- Expense by category ----
            Paragraph catHeading = new Paragraph("Expenses by Category", sectionFont);
            catHeading.setSpacingAfter(8);
            document.add(catHeading);

            Map<String, BigDecimal> byCategory = transactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE)
                    .collect(Collectors.groupingBy(Transaction::getCategory,
                            Collectors.reducing(BigDecimal.ZERO, Transaction::getAmount, BigDecimal::add)));

            PdfPTable catTable = new PdfPTable(2);
            catTable.setWidthPercentage(100);
            catTable.setWidths(new float[]{3, 1});
            catTable.setSpacingAfter(20);
            addHeaderRow(catTable, headerFont, "Category", "Amount");
            byCategory.forEach((cat, amt) -> {
                catTable.addCell(cellOf(cat, normalFont));
                catTable.addCell(cellOf(formatCurrency(amt), normalFont));
            });
            if (byCategory.isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("No expenses recorded", normalFont));
                empty.setColspan(2);
                catTable.addCell(empty);
            }
            document.add(catTable);

            // ---- Full transaction list ----
            Paragraph txHeading = new Paragraph("Transaction Details", sectionFont);
            txHeading.setSpacingAfter(8);
            document.add(txHeading);

            PdfPTable txTable = new PdfPTable(4);
            txTable.setWidthPercentage(100);
            txTable.setWidths(new float[]{2, 2, 3, 2});
            addHeaderRow(txTable, headerFont, "Date", "Type", "Category", "Amount");

            DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("dd MMM yyyy");
            transactions.stream()
                    .sorted((a, b) -> a.getDate().compareTo(b.getDate()))
                    .forEach(t -> {
                        txTable.addCell(cellOf(t.getDate().format(dateFmt), normalFont));
                        txTable.addCell(cellOf(t.getType().toString(), normalFont));
                        txTable.addCell(cellOf(t.getCategory(), normalFont));
                        txTable.addCell(cellOf(formatCurrency(t.getAmount()), normalFont));
                    });
            if (transactions.isEmpty()) {
                PdfPCell empty = new PdfPCell(new Phrase("No transactions recorded for this month", normalFont));
                empty.setColspan(4);
                txTable.addCell(empty);
            }
            document.add(txTable);

            document.close();
            return baos.toByteArray();

        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage(), e);
        }
    }

    private BigDecimal sum(List<Transaction> transactions, TransactionType type) {
        return transactions.stream()
                .filter(t -> t.getType() == type)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String formatCurrency(BigDecimal amount) {
        return "$" + amount.setScale(2, java.math.RoundingMode.HALF_UP).toPlainString();
    }

    private void addSummaryCell(PdfPTable table, String label, BigDecimal value, Color color) {
        PdfPCell cell = new PdfPCell();
        cell.setPadding(10);
        cell.setBorderColor(Color.LIGHT_GRAY);

        Paragraph p = new Paragraph();
        p.add(new Chunk(label + "\n", new Font(Font.HELVETICA, 9, Font.NORMAL, Color.GRAY)));
        p.add(new Chunk(formatCurrency(value), new Font(Font.HELVETICA, 14, Font.BOLD, color)));
        cell.addElement(p);
        table.addCell(cell);
    }

    private void addHeaderRow(PdfPTable table, Font headerFont, String... headers) {
        for (String h : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
            cell.setBackgroundColor(new Color(52, 73, 94));
            cell.setPadding(6);
            table.addCell(cell);
        }
    }

    private PdfPCell cellOf(String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setPadding(5);
        return cell;
    }
}
