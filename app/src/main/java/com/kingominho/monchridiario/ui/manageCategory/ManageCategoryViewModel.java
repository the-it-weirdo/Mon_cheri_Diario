package com.kingominho.monchridiario.ui.manageCategory;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.kingominho.monchridiario.adapters.CategoryAdapter;
import com.kingominho.monchridiario.manager.CategoryManager;

public class ManageCategoryViewModel extends ViewModel {
    private static final String TAG = "ManageCatViewModel:";

    private CategoryAdapter categoryAdapter;
    private CategoryManager categoryManager;

    public ManageCategoryViewModel() {
        Log.d(TAG, "ManageCategoryViewModel: Constructor called.");
        categoryManager = CategoryManager.getInstance();
        categoryAdapter = new CategoryAdapter(categoryManager.getAllCategoriesOptions());
    }

    public CategoryAdapter getCategoryAdapter() {
        return categoryAdapter;
    }

    public CategoryManager getCategoryManager() {
        return categoryManager;
    }
}
