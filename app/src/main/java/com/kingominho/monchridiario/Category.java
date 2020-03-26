package com.kingominho.monchridiario;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Category {

    public static final String KEY_CATEGORY_NAME = "categoryName";
    public static final String KEY_USER_ID = "uid";


    private String categoryName;
    private String uid;


    public Category() {

    }

    public Category(String categoryName, String uid) {
        this.categoryName = categoryName;
        this.uid = uid;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put(KEY_CATEGORY_NAME, categoryName);
        map.put(KEY_USER_ID, uid);
        return map;
    }

}
