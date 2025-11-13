package menuorderingapp.project.service.impl;

import menuorderingapp.project.model.*;
import menuorderingapp.project.repository.CashierRepository;
import menuorderingapp.project.repository.InvoiceRepository;
import menuorderingapp.project.repository.OrderRepository;
import menuorderingapp.project.service.InvoiceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final OrderRepository orderRepository;
    private final CashierRepository cashierRepository;

    public InvoiceServiceImpl(InvoiceRepository invoiceRepository,
                              OrderRepository orderRepository,
                              CashierRepository cashierRepository) {
        this.invoiceRepository = invoiceRepository;
        this.orderRepository = orderRepository;
        this.cashierRepository = cashierRepository;
    }

    @Override
    public Invoice generateInvoice(Order order, Long cashierId) {
        Cashier cashier = cashierRepository.findById(cashierId)
                .orElseThrow(() -> new RuntimeException("Cashier not found with id: " + cashierId));

        // Check if invoice already exists for this order
        Optional<Invoice> existingInvoice = invoiceRepository.findByOrder(order);
        if (existingInvoice.isPresent()) {
            return existingInvoice.get();
        }

        Invoice invoice = new Invoice();
        invoice.setOrder(order);
        invoice.setCashier(cashier);
        invoice.setTotalAmount(order.getTotal());
        invoice.setTaxAmount(calculateTax(order.getTotal()));
        invoice.setFinalAmount(invoice.getTotalAmount().add(invoice.getTaxAmount()));
        invoice.setPaymentMethod(order.getPaymentMethod());

        return invoiceRepository.save(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceById(Long id) {
        return invoiceRepository.findByIdWithOrderAndCashier(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Invoice> getInvoiceByNumber(String invoiceNumber) {
        return invoiceRepository.findByInvoiceNumber(invoiceNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getInvoicesByDateRange(String startDate, String endDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDateTime start = LocalDate.parse(startDate, formatter).atStartOfDay();
        LocalDateTime end = LocalDate.parse(endDate, formatter).atTime(23, 59, 59);

        return invoiceRepository.findInvoicesByDateRange(start, end);
    }

    @Override
    public byte[] generateInvoicePdf(Long invoiceId) {
        // This would integrate with a PDF generation library like iText or Apache PDFBox
        // For now, return a placeholder
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found with id: " + invoiceId));

        String pdfContent = generateInvoiceContent(invoice);
        return pdfContent.getBytes();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Invoice> getAllInvoices() {
        return invoiceRepository.findAll();
    }

    private BigDecimal calculateTax(java.math.BigDecimal amount) {
        // Simple tax calculation - 10% tax
        return amount.multiply(java.math.BigDecimal.valueOf(0.10));
    }

    private String generateInvoiceContent(Invoice invoice) {
        StringBuilder content = new StringBuilder();
        content.append("INVOICE: ").append(invoice.getInvoiceNumber()).append("\n");
        content.append("Date: ").append(invoice.getCreatedAt()).append("\n");
        content.append("Cashier: ").append(invoice.getCashier().getDisplayName()).append("\n");
        content.append("Order: ").append(invoice.getOrder().getOrderNumber()).append("\n");
        content.append("Customer: ").append(invoice.getOrder().getCustomerName()).append("\n");
        content.append("Payment Method: ").append(invoice.getPaymentMethod()).append("\n\n");
        content.append("Items:\n");

        for (OrderItem item : invoice.getOrder().getOrderItems()) {
            content.append(String.format("- %s x%d = %s\n",
                    item.getMenu().getName(),
                    item.getQuantity(),
                    item.getSubtotal()));
        }

        content.append("\nSubtotal: ").append(invoice.getTotalAmount()).append("\n");
        content.append("Tax: ").append(invoice.getTaxAmount()).append("\n");
        content.append("Total: ").append(invoice.getFinalAmount()).append("\n");

        return content.toString();
    }
}
