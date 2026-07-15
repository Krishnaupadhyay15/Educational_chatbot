package com.example.educhatbot;

import android.os.Bundle;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import android.widget.Toast;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    List<String> sessionList;
    DatabaseReference ref;

    List<String> titleList = new ArrayList<>();
    List<String> sessionIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recyclerView);
        sessionList = new ArrayList<>();

        SharedPreferences session = getSharedPreferences("user_session", MODE_PRIVATE);
        String userId = session.getString("userId", "default_user");

        ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("chat_sessions");
        Toast.makeText(this, "User: " + userId, Toast.LENGTH_SHORT).show();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                titleList.clear();
                sessionIdList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {

                    String sessionId = data.getKey();
                    String title = data.child("title").getValue(String.class);

                    if (title == null) title = "New Chat";

                    titleList.add(title);
                    sessionIdList.add(sessionId);
                }

                recyclerView.setLayoutManager(new LinearLayoutManager(HistoryActivity.this));
                recyclerView.setAdapter(new HistoryAdapter(titleList, sessionIdList));
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });

    }
}