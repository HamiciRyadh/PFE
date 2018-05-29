package usthb.lfbservices.com.pfe.utils;

import android.arch.persistence.room.TypeConverter;

import java.sql.Date;

/**
 * A converter used by {@link android.arch.persistence.room.Room} for the date management.
 */

public class DateConverter {

    /**
     * Converts a given timestamp to its associated {@link Date}.
     * @param timestamp The timestamp to convert, it is equal to the number of seconds since
     *                  midnight 01/01/1970.
     * @return The {@link Date} corresponding to the given timestamp.
     */
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    /**
     * Converts a given {@link Date} to its associated timestamp.
     * @param date The {@link Date} to convert into timestamp.
     * @return The timestamp associated with the given {@link Date}, it is equal to the number of
     * seconds since midnight 01/01/1970.
     */
    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
