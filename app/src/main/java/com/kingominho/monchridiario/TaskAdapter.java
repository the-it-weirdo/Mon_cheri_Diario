package com.kingominho.monchridiario;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;

public class TaskAdapter extends FirestoreRecyclerAdapter<Task, TaskAdapter.TaskHolder> {

    private OnTaskItemInteractionListener listener;

    public TaskAdapter(@NonNull FirestoreRecyclerOptions<Task> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull TaskHolder holder, int position, @NonNull Task model) {
        holder.textView.setText(model.getDescription());
        holder.checkBox.setChecked(model.is_finished());
        if(model.is_finished()) {
            holder.textView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        }
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item, parent, false);
        return new TaskHolder(v);
    }

    class TaskHolder extends RecyclerView.ViewHolder implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        CheckBox checkBox;
        TextView textView;
        ImageButton imageButton;

        public TaskHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            textView = itemView.findViewById(R.id.text_view_description);
            imageButton = itemView.findViewById(R.id.button_delete);

            checkBox.setOnCheckedChangeListener(this);
            imageButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            listener.onDeleteClick(getSnapshots().getSnapshot(position), position);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            int position = getAdapterPosition();
            listener.onCheckedChange(getSnapshots().getSnapshot(position), position);
        }
    }

    public void setTaskInteractionListener(OnTaskItemInteractionListener listener) {
        this.listener = listener;
    }

    public interface OnTaskItemInteractionListener {
        void onDeleteClick(DocumentSnapshot documentSnapshot, int position);
        void onCheckedChange(DocumentSnapshot documentSnapshot, int position);
    }
}
