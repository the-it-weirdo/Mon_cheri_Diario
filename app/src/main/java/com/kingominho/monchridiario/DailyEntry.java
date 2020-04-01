package com.kingominho.monchridiario;

import java.util.HashMap;
import java.util.Map;

public class DailyEntry {

    public static final String DATE_KEY = "date";
    public static final String TIME_KEY = "time";
    public static final String ENTRY_KEY = "entry";
    public static final String TIME_STAMP_KEY = "timeStamp";
    public static final String USER_ID_KEY = "user_id";

    private String date;
    private String time;
    private String entry;
    private String timeStamp;
    private String user_id;

    public DailyEntry() {
        //required empty constructor
    }

    public DailyEntry(String date, String time, String entry, String timeStamp, String user_id) {
        this.date = date;
        this.time = time;
        this.entry = entry;
        this.timeStamp = timeStamp;
        this.user_id = user_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /*public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_CATEGORY_NAME, categoryName);
        map.put(KEY_USER_ID, uid);
        return map;
    }*/

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(DATE_KEY, this.date);
        map.put(TIME_KEY, this.time);
        map.put(ENTRY_KEY, this.entry);
        map.put(USER_ID_KEY, this.user_id);
        map.put(TIME_STAMP_KEY, this.timeStamp);
        return map;
    }
}
