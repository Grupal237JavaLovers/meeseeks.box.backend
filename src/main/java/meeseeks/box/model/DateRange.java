package meeseeks.box.model;

import jdk.nashorn.internal.ir.annotations.Immutable;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Immutable
public class DateRange implements Serializable {

    private Calendar start;
    private Calendar end;

    public DateRange(final Calendar start, final Calendar end) {
        this.start = start;
        this.end = end;
    }

    public Calendar getStart() {
        return start;
    }

    public Calendar getEnd() {
        return end;
    }
}
