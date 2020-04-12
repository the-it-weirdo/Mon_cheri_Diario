package com.kingominho.monchridiario.ui.toDo;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kingominho.monchridiario.adapters.CategoryCardAdapter;
import com.kingominho.monchridiario.manager.CategoryManager;

public class ToDoViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    CategoryCardAdapter categoryCardAdapter;
    CategoryManager categoryManager;

    public ToDoViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is toDo fragment");
        categoryManager = CategoryManager.getInstance();
        categoryCardAdapter = new CategoryCardAdapter(categoryManager.getAllCategoriesOptions());
    }

    public LiveData<String> getText() {
        return mText;
    }
}