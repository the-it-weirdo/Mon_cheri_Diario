package com.kingominho.monchridiario.adapters;

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
import com.kingominho.monchridiario.models.DailyEntry;
import com.kingominho.monchridiario.R;

public class DailyEntryAdapter extends FirestoreRecyclerAdapter<DailyEntry, DailyEntryAdapter.DailyEntryHolder> {

    private OnContextMenuItemClickListener onContextMenuItemClickListener;
    private OnClickListener onClickListener;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    public DailyEntryAdapter(@NonNull FirestoreRecyclerOptions<DailyEntry> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull DailyEntryHolder holder, int position, @NonNull DailyEntry model) {
        holder.textViewDate.setText(model.getDate());
        holder.textViewTime.setText(model.getTime());
        holder.textViewEntry.setText(model.getEntry());
    }

    @NonNull
    @Override
    public DailyEntryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.daily_entry_card_layout,
                parent, false);
        return new DailyEntryHolder(v);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        onClickListener.onDataChanged();
    }

    class DailyEntryHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener, View.OnClickListener {

        TextView textViewDate, textViewTime, textViewEntry;

        public DailyEntryHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.date);
            textViewTime = itemView.findViewById(R.id.time);
            textViewEntry = itemView.findViewById(R.id.entry);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            //onContextMenuItemClickListener.onViewEntryClick(getSnapshots().getSnapshot(position), position);
            //DocumentSnapshot snapshot = getSnapshots().getSnapshot(position);
            //DocumentReference reference = snapshot.getReference()
            onClickListener.OnClick(getSnapshots().getSnapshot(position), position);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select Action");
            MenuItem viewEntry = menu.add(Menu.NONE, 1, 1, "View");
            MenuItem updateEntry = menu.add(Menu.NONE, 2, 2, "Update");
            MenuItem deleteEntry = menu.add(Menu.NONE, 3, 3, "Delete");

            viewEntry.setOnMenuItemClickListener(this);
            updateEntry.setOnMenuItemClickListener(this);
            deleteEntry.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if (onContextMenuItemClickListener != null) {
                int position = getAdapterPosition();

                if (position != RecyclerView.NO_POSITION) {
                    switch (item.getItemId()) {
                        case 1:
                            onContextMenuItemClickListener.onViewEntryClick(getSnapshots().getSnapshot(position), position);
                            return true;
                        case 2:
                            onContextMenuItemClickListener.onUpdateEntryClick(getSnapshots().getSnapshot(position), position);
                            return true;
                        case 3:
                            onContextMenuItemClickListener.onDeleteEntryClick(getSnapshots().getSnapshot(position), position);
                            return true;
                        default:
                            return false;
                    }
                }
            }
            return false;
        }
    }

    public interface OnContextMenuItemClickListener {
        void onViewEntryClick(DocumentSnapshot documentSnapshot, int position);

        void onUpdateEntryClick(DocumentSnapshot documentSnapshot, int position);

        void onDeleteEntryClick(DocumentSnapshot documentSnapshot, int position);

    }

    public void setOnContextMenuItemClickListener(OnContextMenuItemClickListener listener) {
        this.onContextMenuItemClickListener = listener;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.onClickListener = listener;
    }

    public interface OnClickListener {
        void OnClick(DocumentSnapshot documentSnapshot, int position);

        void onDataChanged();
    }
}
