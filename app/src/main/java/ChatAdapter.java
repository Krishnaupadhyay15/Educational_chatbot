package com.example.educhatbot;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<Message> messageList;

    public ChatAdapter(List<Message> messageList) {
        this.messageList = messageList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_message, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        Message message = messageList.get(position);

        holder.messageText.setText(message.getMessage());

        if (message.isUser()) {
            // User message (right)
            holder.container.setGravity(Gravity.END);
            holder.messageText.setBackgroundColor(Color.parseColor("#DCF8C6"));
        } else {
            // Bot message (left)
            holder.container.setGravity(Gravity.START);
            holder.messageText.setBackgroundColor(Color.parseColor("#EDEDED"));
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        TextView messageText;
        LinearLayout container;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            container = itemView.findViewById(R.id.messageContainer);
        }
    }
}
