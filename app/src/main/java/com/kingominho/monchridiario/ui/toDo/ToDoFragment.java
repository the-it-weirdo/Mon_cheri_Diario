package com.kingominho.monchridiario.ui.toDo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.kingominho.monchridiario.models.Category;
import com.kingominho.monchridiario.adapters.CategoryCardAdapter;
import com.kingominho.monchridiario.R;

public class ToDoFragment extends Fragment {
    private final String TAG = "ToDoFragment: ";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddTask;
    private TextView emptyTextView;

    private boolean isEmptyCategory;

    private ToDoViewModel toDoViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_to_do, container, false);
        recyclerView = root.findViewById(R.id.recycler_view);
        progressBar = root.findViewById(R.id.progress_circle);
        fabAddTask = root.findViewById(R.id.fab_add_task);
        fabAddTask.setEnabled(false);
        emptyTextView = root.findViewById(R.id.empty_text_view);

        toDoViewModel = new ViewModelProvider(this).get(ToDoViewModel.class);
        toDoViewModel.categoryCardAdapter.startListening();

        Log.d(TAG, "onCreateView: categoryAdapter started listening.");

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        toDoViewModel.categoryCardAdapter.startListening();
        Log.d(TAG, "onResume: categoryAdapter started listening.");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpRecyclerView();
        fabAddTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEmptyCategory) {
                    Toast.makeText(getActivity(), "Make some categories first!!\n" +
                            "Click the add button in Manage Category section." ,Toast.LENGTH_SHORT).show();
                    Navigation.findNavController(v).navigate(R.id.manageCategory);
                } else {
                    Navigation.findNavController(v).navigate(R.id.addTask);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: categoryAdapter to stop listening.");
        toDoViewModel.categoryCardAdapter.stopListening();
    }

    private void setUpRecyclerView() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setAdapter(toDoViewModel.categoryCardAdapter);

        Log.d(TAG, "setUpRecyclerView: " + toDoViewModel.categoryCardAdapter.getItemCount());

        toDoViewModel.categoryCardAdapter.setOnItemClickListener(new CategoryCardAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(DocumentSnapshot documentSnapshot, int position) {
                Category category = documentSnapshot.toObject(Category.class);
                String id = documentSnapshot.getId();
                //String path = documentSnapshot.getReference().getPath();
                //documentSnapshot.getReference();
                //Toast.makeText(getContext(), "Position: " + position + " ID: " + id + " clicked." + " path= " + path, Toast.LENGTH_SHORT).show();

                Bundle bundle = new Bundle();
                bundle.putString("category_id", id);
                bundle.putString(Category.KEY_CATEGORY_NAME, category.getCategoryName());
                //Navigation.findNavController(getView()).navigate(R.id.viewCategory, bundle);
                Navigation.findNavController(getView()).navigate(R.id.viewRemainingTasksFragment, bundle);
            }

            @Override
            public void onDataChanged() {
                progressBar.setVisibility(View.GONE);
                fabAddTask.setEnabled(true);
                isEmptyCategory = toDoViewModel.categoryCardAdapter.getItemCount() == 0;
                emptyTextView.setVisibility(isEmptyCategory ? View.VISIBLE : View.GONE);
            }
        });
    }
}