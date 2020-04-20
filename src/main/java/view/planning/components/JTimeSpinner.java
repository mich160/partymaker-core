package view.planning.components;

import javax.swing.*;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

public class JTimeSpinner extends JSpinner {
    public static final LocalDate DUMMY_DATE = LocalDate.of(2000, 1, 1);

    private static SpinnerDateModel getSpinnerDateModel() {
        SpinnerDateModel model = new SpinnerDateModel();
        model.setCalendarField(Calendar.MINUTE);
        return model;
    }

    private final ZoneId zone;

    public JTimeSpinner() {
        this(ZoneId.systemDefault());
    }

    public JTimeSpinner(ZoneId zone) {
        super(getSpinnerDateModel());
        this.zone = zone;
        this.setEditor(new JSpinner.DateEditor(this, "HH:mm"));
    }

    public LocalTime getSelectedTime() {
        Date value = (Date) this.getModel().getValue();
        return value.toInstant().atZone(zone).toLocalTime();
    }

    public void setSelectedTime(LocalTime localTime) {
        Instant instant = localTime
                .atDate(DUMMY_DATE)
                .atZone(zone)
                .toInstant();

        this.getModel().setValue(Date.from(instant));
    }
}
