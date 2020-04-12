package com.kingominho.monchridiario.ui.manageCategory;


import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kingominho.monchridiario.R;
import com.kingominho.monchridiario.adapters.CategoryAdapter;
import com.kingominho.monchridiario.manager.CategoryManager;
import com.kingominho.monchridiario.manager.TaskManager;
import com.kingominho.monchridiario.models.Category;

public class ManageCategoryFragment extends Fragment {
    private final static String TAG = "ManageCategoryFragment";

    ManageCategoryViewModel manageCategoryViewModel;

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddCategory;
    private TextView emptyTextView;

    //TODO: StringBuilder not sending messages.

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_manage_category, container, false);
        manageCategoryViewModel = ViewModelProviders.of(this).get(ManageCategoryViewModel.class);
        recyclerView = root.findViewById(R.id.recycler_view);
        progressBar = root.findViewById(R.id.progress_circle);
        fabAddCategory = root.findViewById(R.id.fab_add_category);
        emptyTextView = root.findViewById(R.id.empty_text_view);

        manageCategoryViewModel.getCategoryAdapter().startListening();
        Log.d(TAG, "onCreateView: categoryAdapter started listening.");
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        manageCategoryViewModel.getCategoryAdapter().startListening();
        Log.d(TAG, "onResume: categoryAdapter started listening.");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView();
        fabAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCategoryDialog();
            }
        });

    }


    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: categoryAdapter to stop listening.");
        manageCategoryViewModel.getCategoryAdapter().stopListening();
    }

    private void setUpRecyclerView() {

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(manageCategoryViewModel.getCategoryAdapter());

        Log.d(TAG, "setUpRecyclerView: " + manageCategoryViewModel.getCategoryAdapter());

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                manageCategoryViewModel.getCategoryAdapter().deleteItem(viewHolder.getAdapterPosition());
            }
        }).attachToRecyclerView(recyclerView);

        manageCategoryViewModel.getCategoryAdapter().setOnItemClickListener(new CategoryAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                Category category = documentSnapshot.toObject(Category.class);
                //String id = documentSnapshot.getId();
                //String path = documentSnapshot.getReference().getPath();
                //documentSnapshot.getReference();
                Toast.makeText(getContext(), category.getCategoryName() + "clicked.",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnUpdateClick(DocumentSnapshot documentSnapshot, int position) {
                DocumentReference documentReference = documentSnapshot.getReference();
                showUpdateCategoryDialog(documentReference);
            }

            @Override
            public void OnDeleteClick(DocumentSnapshot documentSnapshot, int position) {
                //categoryAdapter.deleteItem(position);
                TaskManager.getInstance().deleteAllTask(documentSnapshot.getId());
                manageCategoryViewModel.getCategoryManager().deleteCategory(documentSnapshot.getReference());
            }

            @Override
            public void OnDataChanged() {
                progressBar.setVisibility(View.GONE);
                boolean bool = manageCategoryViewModel.getCategoryAdapter().getItemCount() == 0;
                emptyTextView.setVisibility(bool ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showAddCategoryDialog() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.single_edit_text_dialog_layout);
        dialog.setTitle("Add new category");

        final EditText editTextCategoryName = dialog.findViewById(R.id.edit_text);
        editTextCategoryName.setHint("Enter category name");
        Button okayButton = dialog.findViewById(R.id.positive_button);
        okayButton.setText("Add");
        Button dismissButton = dialog.findViewById(R.id.dismiss_dialog_button);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editTextCategoryName.getText().toString();
                if (s.trim().isEmpty()) {
                    editTextCategoryName.setError("Name cannot be empty!");
                    editTextCategoryName.requestFocus();
                } else {
                    addCategory(s);
                    dialog.dismiss();
                }
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showUpdateCategoryDialog(final DocumentReference documentReference) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.single_edit_text_dialog_layout);
        dialog.setTitle("Update Category");

        final EditText editTextCategoryName = dialog.findViewById(R.id.edit_text);
        editTextCategoryName.setHint("Enter category name");
        Button okayButton = dialog.findViewById(R.id.positive_button);
        Button dismissButton = dialog.findViewById(R.id.dismiss_dialog_button);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editTextCategoryName.getText().toString();
                if (s.trim().isEmpty()) {
                    editTextCategoryName.setError("Name cannot be empty!");
                    editTextCategoryName.requestFocus();
                } else {
                    updateCategory(documentReference, s);
                    dialog.dismiss();
                }
            }
        });

        dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addCategory(String name) {
        String message = manageCategoryViewModel.getCategoryManager().createNewCategory(name);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void updateCategory(DocumentReference documentReference, String newName) {
        String message = manageCategoryViewModel.getCategoryManager().updateCategoryName(documentReference, newName);
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}
