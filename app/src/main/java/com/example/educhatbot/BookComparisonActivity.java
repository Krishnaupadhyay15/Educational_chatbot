package com.example.educhatbot;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class BookComparisonActivity extends AppCompatActivity {

    Spinner spinner1, spinner2;
    Button compareBtn;
    List<BookModel> bookList = new ArrayList<>();
    List<String> bookNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_comparison);
        SharedPreferences session = getSharedPreferences("user_session", MODE_PRIVATE);
        String userId = session.getString("userId", "default_user");

        spinner1 = findViewById(R.id.spinnerBook1);
        spinner2 = findViewById(R.id.spinnerBook2);
        compareBtn = findViewById(R.id.compareBtn);
        loadBooks();

        compareBtn.setOnClickListener(v -> compareBooks());
    }

    private void loadBooks() {

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("books");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                bookList.clear();
                bookNames.clear();

                for (DataSnapshot subject : snapshot.getChildren()) {
                    for (DataSnapshot bookSnap : subject.getChildren()) {

                        BookModel book = bookSnap.getValue(BookModel.class);
                        bookList.add(book);
                        bookNames.add(book.getName());
                    }
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        BookComparisonActivity.this,
                        android.R.layout.simple_spinner_dropdown_item,
                        bookNames
                );

                spinner1.setAdapter(adapter);
                spinner2.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private void compareBooks() {

        int pos1 = spinner1.getSelectedItemPosition();
        int pos2 = spinner2.getSelectedItemPosition();

        BookModel b1 = bookList.get(pos1);
        BookModel b2 = bookList.get(pos2);

        TextView name1 = findViewById(R.id.book1Name);
        TextView author1 = findViewById(R.id.book1Author);
        TextView level1 = findViewById(R.id.book1Level);
        TextView desc1 = findViewById(R.id.book1Desc);

        TextView name2 = findViewById(R.id.book2Name);
        TextView author2 = findViewById(R.id.book2Author);
        TextView level2 = findViewById(R.id.book2Level);
        TextView desc2 = findViewById(R.id.book2Desc);

        name1.setText("📘 " + b1.getName());
        author1.setText("Author: " + b1.getAuthor());
        level1.setText("Level: " + b1.getLevel());
        desc1.setText(b1.getDescription());

        name2.setText("📗 " + b2.getName());
        author2.setText("Author: " + b2.getAuthor());
        level2.setText("Level: " + b2.getLevel());
        desc2.setText(b2.getDescription());

        SharedPreferences session = getSharedPreferences("user_session", MODE_PRIVATE);
        String userId = session.getString("userId", "default_user");

        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("activity");

        String log = "Compared: " + b1.getName() + " vs " + b2.getName();

        ref.push().setValue(log);
    }
}