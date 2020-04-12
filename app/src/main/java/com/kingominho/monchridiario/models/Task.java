package com.kingominho.monchridiario.models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Task {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY_ID = "category_id";
    public static final String KEY_FINISHED = "finished";
    public static final String KEY_FINISH_BY = "finish_by";
    public static final String KEY_PRIORITY = "priority";
    public static final String KEY_USER_ID = "user_id";

    private String description;
    private String category_id;
    private boolean finished;
    private Date finish_by;
    private int priority;
    private String user_id;

    public Task() {
        //required empty constructor
    }

    public Task(String description, String category_id, boolean finished, Date finish_by, int priority, String uid) {
        this.description = description;
        this.category_id = category_id;
        this.finished = finished;
        this.finish_by = finish_by;
        this.priority = priority;
        this.user_id = uid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Date getFinish_by() {
        return finish_by;
    }

    public void setFinish_by(Date finish_by) {
        this.finish_by = finish_by;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    /*public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(DATE_KEY, this.date);
        map.put(TIME_KEY, this.time);
        map.put(ENTRY_KEY, this.entry);
        map.put(USER_ID_KEY, this.user_id);
        map.put(TIME_STAMP_KEY, this.timeStamp);
        return map;
    }*/

    public Map<String, Object> toMap() {
        Map<String,  Object> map = new HashMap<>();
        map.put(KEY_DESCRIPTION, description);
        map.put(KEY_CATEGORY_ID, category_id);
        map.put(KEY_FINISH_BY, finish_by);
        map.put(KEY_FINISHED, finished);
        map.put(KEY_PRIORITY, priority);
        map.put(KEY_USER_ID, user_id);
        return map;
    }
}
