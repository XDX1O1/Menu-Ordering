package menuorderingapp.project.service;

import menuorderingapp.project.model.Invoice;
import menuorderingapp.project.model.Order;

import java.util.List;
import java.util.Optional;

public interface InvoiceService {

    Invoice generateInvoice(Order order, Long cashierId);

    Optional<Invoice> getInvoiceById(Long id);

    Optional<Invoice> getInvoiceByNumber(String invoiceNumber);

    Optional<Invoice> getInvoiceByOrder(Order order);

    List<Invoice> getInvoicesByDateRange(String startDate, String endDate);

    byte[] generateInvoicePdf(Long invoiceId);

    List<Invoice> getAllInvoices();
}
