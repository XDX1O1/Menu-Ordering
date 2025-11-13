package menuorderingapp.project.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportUtils {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String formatDate(LocalDate date) {
        return date != null ? date.format(DATE_FORMATTER) : "";
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_TIME_FORMATTER) : "";
    }

    public static String formatCurrency(Double amount) {
        if (amount == null) return Constants.CURRENCY_SYMBOL + "0";
        return Constants.CURRENCY_SYMBOL + String.format("%,.2f", amount);
    }

    public static String formatCurrency(Integer amount) {
        if (amount == null) return Constants.CURRENCY_SYMBOL + "0";
        return Constants.CURRENCY_SYMBOL + String.format("%,d", amount);
    }

    public static LocalDateTime getStartOfDay(LocalDate date) {
        return date != null ? date.atStartOfDay() : LocalDate.now().atStartOfDay();
    }

    public static LocalDateTime getEndOfDay(LocalDate date) {
        return date != null ? date.atTime(23, 59, 59) : LocalDate.now().atTime(23, 59, 59);
    }
}
