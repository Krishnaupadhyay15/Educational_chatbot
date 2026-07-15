package com.example.educhatbot;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    List<BookModel> list;

    public BookAdapter(List<BookModel> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView name, author, level, desc, rating;

        public ViewHolder(View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.bookImage);
            name = itemView.findViewById(R.id.bookName);
            author = itemView.findViewById(R.id.bookAuthor);
            level = itemView.findViewById(R.id.bookLevel);
            desc = itemView.findViewById(R.id.bookDesc);
            rating = itemView.findViewById(R.id.bookRating);
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        BookModel book = list.get(position);

        holder.rating.setText("⭐ " + book.getRating());

        holder.name.setText(book.getName());
        holder.author.setText("Author: " + book.getAuthor());
        holder.level.setText("Level: " + book.getLevel());
        holder.desc.setText(book.getDescription());

        // ✅ SET RATING HERE
        holder.rating.setText("⭐ Rating: " + book.getRating());

        Glide.with(holder.itemView.getContext())
                .load(book.getImage())
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_delete)
                .centerCrop()
                .into(holder.image);

        holder.itemView.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), BookDetailActivity.class);
            intent.putExtra("name", book.getName());
            intent.putExtra("author", book.getAuthor());
            intent.putExtra("desc", book.getDescription());
            intent.putExtra("image", book.getImage());

            v.getContext().startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<BookModel> newList) {
        list = newList;
        notifyDataSetChanged();
    }
}