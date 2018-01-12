package meeseeks.box.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Calendar;

/**
 * @author Alexandru Stoica
 * @version 1.0
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public class DateRange implements Serializable {

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Calendar start;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final Calendar end;

    public DateRange(final Calendar start, final Calendar end) {
        this.start = start;
        this.end = end;
    }

    public DateRange() {
        this(Calendar.getInstance(), Calendar.getInstance());
    }

    public Calendar getStart() {
        return start;
    }

    public Calendar getEnd() {
        return end;
    }
}
