package com.example.educhatbot;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.List;

public class BookRecommendationActivity extends AppCompatActivity {

    Spinner subjectSpinner;
    RecyclerView recyclerView;
    EditText searchBox;

    List<BookModel> bookList;
    List<BookModel> fullList;

    BookAdapter adapter;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_recommendation);

        // ✅ INIT UI
        searchBox = findViewById(R.id.searchBox);
        subjectSpinner = findViewById(R.id.subjectSpinner);
        recyclerView = findViewById(R.id.recyclerView);

        // ✅ USER ID
        SharedPreferences session = getSharedPreferences("user_session", MODE_PRIVATE);
        userId = session.getString("userId", "default_user");

        // ✅ SUBJECTS
        String[] subjects = {"java", "python", "dbms", "os", "ai"};

        subjectSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                subjects
        ));

        // ✅ LIST INIT
        bookList = new ArrayList<>();
        fullList = new ArrayList<>();

        adapter = new BookAdapter(bookList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // ✅ SPINNER
        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loadBooks(subjects[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // ✅ SEARCH (ONLY ONCE)
        searchBox.addTextChangedListener(new TextWatcher() {
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                List<BookModel> filtered = new ArrayList<>();

                for (BookModel b : fullList) {
                    if (b.getName().toLowerCase().contains(s.toString().toLowerCase())) {
                        filtered.add(b);
                    }
                }

                adapter.updateList(filtered);
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void afterTextChanged(Editable s) {}
        });
        loadBooks("os");
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (subjectSpinner != null && subjectSpinner.getSelectedItem() != null) {
            String subject = subjectSpinner.getSelectedItem().toString();
            System.out.println("ON RESUME SUBJECT: " + subject);

            loadBooks(subject);
        }
    }

    // ✅ LOAD BOOKS
    private void loadBooks(String subject) {

        System.out.println("SUBJECT: " + subject);
        System.out.println("Loading subject: " + subject);
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("books")
                .child(subject);

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                System.out.println("DATA RECEIVED");
                System.out.println("Children count: " + snapshot.getChildrenCount());
                bookList.clear();
                fullList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    BookModel book = data.getValue(BookModel.class);

                    if (book != null) {
                        bookList.add(book);
                        fullList.add(book);

                        // 🔥 DEBUG (check description)
                        System.out.println("DESC: " + book.getDescription());
                    }
                }

                adapter.updateList(bookList);

                // ✅ SAVE ACTIVITY
                DatabaseReference activityRef = FirebaseDatabase.getInstance()
                        .getReference("users")
                        .child(userId)
                        .child("activity");

                activityRef.push().setValue("Viewed books for: " + subject);
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });
    }
}