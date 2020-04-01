package com.kingominho.monchridiario;

import java.util.Date;

public class Task {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_CATEGORY_ID = "category_id";
    public static final String KEY_IS_FINISHED = "is_finished";
    public static final String KEY_FINISH_BY = "finish_by";
    public static final String KEY_PRIORITY = "priority";

    private String description;
    private String category_id;
    private boolean is_finished;
    private Date finish_by;
    private int priority;

    public Task() {
        //required empty constructor
    }

    public Task(String description, String category_id, boolean is_finished, Date finish_by, int priority) {
        this.description = description;
        this.category_id = category_id;
        this.is_finished = is_finished;
        this.finish_by = finish_by;
        this.priority = priority;
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

    public boolean is_finished() {
        return is_finished;
    }

    public void set_finished(boolean is_finished) {
        this.is_finished = is_finished;
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
}
