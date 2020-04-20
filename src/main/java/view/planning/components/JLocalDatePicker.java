package view.planning.components;

import org.jdatepicker.JDatePicker;

import java.time.LocalDate;
import java.util.GregorianCalendar;

public class JLocalDatePicker extends JDatePicker {

    public LocalDate getSelectedDate() {
        GregorianCalendar value = (GregorianCalendar) this.getModel().getValue();
        return value.toZonedDateTime().toLocalDate();
    }

    public void setSelectedDate(LocalDate localDate) {
        this.getModel().setDate(localDate.getYear(), adjustPlusOneMonthOffset(localDate), localDate.getDayOfMonth());
        this.getModel().setSelected(true);
    }

    private int adjustPlusOneMonthOffset(LocalDate localDate) {
        return localDate.getMonthValue() - 1;
    }
}
