package com.example.educhatbot;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class BookDetailActivity extends AppCompatActivity {

    ImageView img;
    TextView title, author, desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);

        Toast.makeText(this, "Detail Activity Opened", Toast.LENGTH_LONG).show();

        img = findViewById(R.id.imgBook);
        title = findViewById(R.id.tvTitle);
        author = findViewById(R.id.tvAuthor);
        desc = findViewById(R.id.tvDesc);

        title.setText(getIntent().getStringExtra("name"));
        author.setText("Author: " + getIntent().getStringExtra("author"));
        desc.setText(getIntent().getStringExtra("desc"));

        Glide.with(this)
                .load(getIntent().getStringExtra("image"))
                .into(img);
    }
}