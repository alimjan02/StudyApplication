package com.sxt.chat.view.searchview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.sxt.chat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Suggestions Adapter.
 *
 * @author Miguel Catalan Bañuls
 */
public class SearchAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {

    private Context context;
    private ArrayList<String> data;
    private String[] suggestions;
    private Drawable suggestionIcon;
    private LayoutInflater inflater;
    private boolean ellipsize;
    private OnItemClickListener onItemClickListener;

    public SearchAdapter(Context context, String[] suggestions) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        data = new ArrayList<>();
        this.suggestions = suggestions;
    }

    public SearchAdapter(Context context, String[] suggestions, Drawable suggestionIcon, boolean ellipsize) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        data = new ArrayList<>();
        this.suggestions = suggestions;
        this.suggestionIcon = suggestionIcon;
        this.ellipsize = ellipsize;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (!TextUtils.isEmpty(constraint)) {

                    // Retrieve the autocomplete results.
                    List<String> searchData = new ArrayList<>();

                    for (String string : suggestions) {
                        if (string.toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                            searchData.add(string);
                        }
                    }

                    // Assign the data to the FilterResults
                    filterResults.values = searchData;
                    filterResults.count = searchData.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    data = (ArrayList<String>) results.values;
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public String getItem(int position) {
        if (data != null && data.size() > position) {
            return data.get(position);
        }
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SuggestionsViewHolder(inflater.inflate(R.layout.suggest_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        SuggestionsViewHolder viewHolder = (SuggestionsViewHolder) holder;
        String currentListData = data.get(position);

        viewHolder.textView.setText(currentListData);
        if (ellipsize) {
            viewHolder.textView.setSingleLine();
            viewHolder.textView.setEllipsize(TextUtils.TruncateAt.END);
        }
        viewHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    public class SuggestionsViewHolder extends RecyclerView.ViewHolder {

        public View root;
        public TextView textView;
        public ImageView imageView;

        public SuggestionsViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            textView = (TextView) itemView.findViewById(R.id.suggestion_text);
            if (suggestionIcon != null) {
                imageView = (ImageView) itemView.findViewById(R.id.suggestion_icon);
                imageView.setImageDrawable(suggestionIcon);
            }
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}