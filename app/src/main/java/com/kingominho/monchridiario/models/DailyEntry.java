package com.kingominho.monchridiario.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class DailyEntry implements Parcelable {

    public static final String DATE_KEY = "date";
    public static final String TIME_KEY = "time";
    public static final String ENTRY_KEY = "entry";
    public static final String TIME_STAMP_KEY = "timeStamp";
    public static final String USER_ID_KEY = "user_id";

    public static final String DAILY_ENTRY_ID_KEY = "daily_entry_id";

    private String date;
    private String time;
    private String entry;
    private String timeStamp;
    private String user_id;

    @Exclude
    private String daily_entry_id;

    public DailyEntry() {
        //required empty constructor
    }

    protected DailyEntry(Parcel parcel) {
        this.date = parcel.readString();
        this.time = parcel.readString();
        this.entry = parcel.readString();
        this.timeStamp = parcel.readString();
        this.user_id = parcel.readString();
    }

    @Exclude
    public static final Creator<DailyEntry> CREATOR = new Creator<DailyEntry>() {
        @Override
        public DailyEntry createFromParcel(Parcel source) {
            return new DailyEntry(source);
        }

        @Override
        public DailyEntry[] newArray(int size) {
            return new DailyEntry[size];
        }
    };

    @Exclude
    public static Creator<DailyEntry> getCREATOR() {
        return CREATOR;
    }


    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.date);
        dest.writeString(this.time);
        dest.writeString(this.entry);
        dest.writeString(this.timeStamp);
        dest.writeString(this.user_id);
    }

    @Exclude
    public String getDaily_entry_id() {
        return daily_entry_id;
    }

    @Exclude
    public void setDaily_entry_id(String daily_entry_id) {
        this.daily_entry_id = daily_entry_id;
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
