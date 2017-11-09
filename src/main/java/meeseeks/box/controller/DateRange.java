package meeseeks.box.controller;

import jdk.nashorn.internal.ir.annotations.Immutable;

import java.util.Calendar;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@Immutable
public class DateRange {

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
