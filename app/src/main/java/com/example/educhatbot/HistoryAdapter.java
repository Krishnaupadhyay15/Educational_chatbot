package com.example.educhatbot;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    List<String> titleList;
    List<String> sessionIdList;

    public HistoryAdapter(List<String> titles, List<String> ids) {
        this.titleList = titles;
        this.sessionIdList = ids;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView text;

        public ViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.textView);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // ✅ Get title
        String title = titleList.get(position);
        holder.text.setText(title);

        // ✅ Get sessionId
        String sessionId = sessionIdList.get(position);

        // ✅ Click → open that chat
        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), MainActivity.class);
            intent.putExtra("sessionId", sessionId);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }
}