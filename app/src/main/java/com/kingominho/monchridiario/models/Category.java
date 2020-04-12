package com.kingominho.monchridiario.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Category {

    public static final String KEY_CATEGORY_NAME = "categoryName";
    public static final String KEY_USER_ID = "uid";

    public static final String KEY_CATEGORY_KEY = "cat_key";


    private String categoryName;
    private String uid;

    @Exclude
    private String cat_key;

    public Category() {

    }

    public Category(String categoryName, String uid) {
        this.categoryName = categoryName;
        this.uid = uid;
    }

    @Exclude
    public String getCategoryKey() {
        return cat_key;
    }

    @Exclude
    public void setCategoryKey(String cat_key) {
        this.cat_key = cat_key;
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

    @NonNull
    @Override
    public String toString() {
        return this.categoryName;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        Category c;
        try {
            c = (Category) obj;
        } catch (Exception e) {
            return false;
        }
        try {
            if (obj.getClass() != Category.class) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        boolean condition1 = c.categoryName.compareTo(this.categoryName) == 0;
        boolean condition2 = c.uid.compareTo(this.uid) == 0;
        return condition1 && condition2;
    }
}
