package com.kingominho.monchridiario;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

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
    protected void onBindViewHolder(@NonNull CategoryCardHolder holder, int position, @NonNull Category model) {
        holder.textViewCategoryName.setText(model.getCategoryName());
        //holder.textViewTaskRemaining.setText(position);
        String id = getSnapshots().getSnapshot(position).getId();
        //TaskManager.getRemainingTaskCount()
        holder.textViewTaskRemaining.setText(id);
    }

    @NonNull
    @Override
    public CategoryCardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card_layout,
                parent, false);
        return new CategoryCardHolder(v);
    }


    class CategoryCardHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView textViewCategoryName;
        TextView textViewTaskRemaining;

        public CategoryCardHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.text_view_category_name);
            textViewTaskRemaining = itemView.findViewById(R.id.text_view_task_remaining);

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
    }
}
