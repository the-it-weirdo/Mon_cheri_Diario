package com.kingominho.monchridiario.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.kingominho.monchridiario.models.Category;
import com.kingominho.monchridiario.R;
import com.kingominho.monchridiario.manager.TaskManager;

public class CategoryCardAdapter extends FirestoreRecyclerAdapter<Category, CategoryCardAdapter.CategoryCardHolder> {

    private OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CategoryCardAdapter(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull final CategoryCardHolder holder, int position, @NonNull Category model) {
        holder.progressBar.setVisibility(View.VISIBLE);
        holder.textViewCategoryName.setText(model.getCategoryName());
        String id = getSnapshots().getSnapshot(position).getId();
        TaskManager taskManager = TaskManager.getInstance();
        taskManager.getTaskCountQuery(id, false).get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        holder.progressBar.setVisibility(View.GONE);
                        String s = queryDocumentSnapshots.size() + " tasks remaining";
                        holder.textViewTaskRemaining.setText(s);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        holder.progressBar.setVisibility(View.GONE);
                        String s = "Failed to fetch remaining tasks. Error: " + e.getLocalizedMessage();
                        holder.textViewTaskRemaining.setText(s);
                    }
                });
    }

    @NonNull
    @Override
    public CategoryCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card_layout,
                parent, false);
        return new CategoryCardHolder(v);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        listener.onDataChanged();
    }

    class CategoryCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewCategoryName;
        TextView textViewTaskRemaining;
        ProgressBar progressBar;

        public CategoryCardHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.text_view_category_name);
            textViewTaskRemaining = itemView.findViewById(R.id.text_view_task_remaining);
            progressBar = itemView.findViewById(R.id.progress_bar);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.OnItemClick(getSnapshots().getSnapshot(position), position);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void OnItemClick(DocumentSnapshot documentSnapshot, int position);

        void onDataChanged();
    }
}
