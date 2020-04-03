package com.kingominho.monchridiario;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class CategoryAdapter extends FirestoreRecyclerAdapter<Category, CategoryAdapter.CategoryHolder> {

    private OnItemClickListener listener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public CategoryAdapter(@NonNull FirestoreRecyclerOptions<Category> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull CategoryHolder holder, int position, @NonNull Category model) {
        holder.textViewCategoryName.setText(model.getCategoryName());
    }

    @NonNull
    @Override
    public CategoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item,
                parent, false);
        return new CategoryHolder(v);
    }

    class CategoryHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        TextView textViewCategoryName;

        public CategoryHolder(@NonNull View itemView) {
            super(itemView);
            textViewCategoryName = itemView.findViewById(R.id.text_view_category_name);

            /*itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener!=null) {
                        listener.OnItemClick(getSnapshots().getSnapshot(position), position);
                    }
                }
            });*/
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION && listener != null) {
                listener.OnItemClick(getSnapshots().getSnapshot(position), position);
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem doWhatever = menu.add(Menu.NONE, 1, 1, "Update");
            MenuItem delete = menu.add(Menu.NONE, 2, 2, "Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (listener != null) {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            listener.OnUpdateClick(getSnapshots().getSnapshot(position), position);
                            return true;
                        case 2:
                            listener.OnDeleteClick(getSnapshots().getSnapshot(position), position);
                            return true;
                    }
                }
            }
            return false;
        }
    }

    public void deleteItem(int position) {
        TaskManager.getInstance().deleteAllTask(getSnapshots().getSnapshot(position).getId());
        getSnapshots().getSnapshot(position).getReference().delete();
    }

    public interface OnItemClickListener {
        void OnItemClick(DocumentSnapshot documentSnapshot, int position);

        void OnUpdateClick(DocumentSnapshot documentSnapshot, int position);

        void OnDeleteClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}

