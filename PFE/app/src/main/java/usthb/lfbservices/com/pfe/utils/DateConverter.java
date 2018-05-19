package usthb.lfbservices.com.pfe.utils;

import android.arch.persistence.room.TypeConverter;

import java.sql.Date;

/**
 * Created by LFB-SC3 on 15/05/2018.
 */

public class DateConverter {
    @TypeConverter
    public static Date toDate(Long timestamp) {
        return timestamp == null ? null : new Date(timestamp);
    }

    @TypeConverter
    public static Long toTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
