package brmcmaho.dtlocationsharing.events;

import android.location.Location;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Brian on 2014-10-10.
 */
public class StatusMessageEvent {

    Date when = Calendar.getInstance().getTime();
    private String message;

    public StatusMessageEvent(String message) {
        this.message = message;
    }

    public String getMessage() { return message; }
    public Date getTimestamp() { return when; }


}
