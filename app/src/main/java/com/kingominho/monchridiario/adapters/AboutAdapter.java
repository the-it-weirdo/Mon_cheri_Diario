package com.kingominho.monchridiario.adapters;

import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kingominho.monchridiario.R;
import com.kingominho.monchridiario.models.AboutItem;
import com.kingominho.monchridiario.models.AboutTitleItem;

import java.util.List;

public class AboutAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_HEADER = 1;
    private static final int ITEM_TYPE_ITEM = 2;

    private List<Object> items;

    public AboutAdapter(List<Object> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        if (viewType == ITEM_TYPE_HEADER) {
            View view = layoutInflater.inflate(R.layout.about_header_layout, parent, false);

            return new AboutTitleHolder(view);
        } else {
            View view = layoutInflater.inflate(R.layout.about_item_layout, parent, false);

            return new AboutItemHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Object item = items.get(position);

        if (holder instanceof AboutItemHolder) {
            ((AboutItemHolder) holder).bind((AboutItem) item);
        } else {
            ((AboutTitleHolder) holder).bind((AboutTitleItem) item);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof AboutTitleItem) {
            return ITEM_TYPE_HEADER;
        } else {
            return ITEM_TYPE_ITEM;
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    //Holder classes
    class AboutItemHolder extends RecyclerView.ViewHolder {

        private TextView titleView, descriptionView, linkView;
        public AboutItemHolder(@NonNull View itemView) {
            super(itemView);
            titleView = itemView.findViewById(R.id.text_title);
            descriptionView = itemView.findViewById(R.id.text_description);
            linkView = itemView.findViewById(R.id.text_link);
        }

        public void bind(AboutItem item) {
            titleView.setText(item.getTitle());
            descriptionView.setText(item.getDescription());
            linkView.setText(item.getLink());
            //linkView.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    class AboutTitleHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        public AboutTitleHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_header);
        }


        public void bind(AboutTitleItem item) {
            textView.setText(item.getTitle());
        }
    }
}
