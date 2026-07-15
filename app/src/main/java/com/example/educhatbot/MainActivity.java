package com.example.educhatbot;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;

import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    RecyclerView chatRecyclerView;
    EditText messageEditText;
    Button sendButton;

    List<Message> messageList;
    ChatAdapter chatAdapter;

    DatabaseReference databaseReference;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    DatabaseReference chatRef;

    String lastSubject = "";

    boolean isFirstMessage = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);

        SharedPreferences session = getSharedPreferences("user_session", MODE_PRIVATE);
        String username = session.getString("username", "User");

        Menu menu = navigationView.getMenu();
        MenuItem item = menu.findItem(R.id.menu_username);
        item.setTitle("👤 " + username);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_menu);

        toolbar.setNavigationOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        String userId = session.getString("userId", "default_user");

        String tempSessionId = getIntent().getStringExtra("sessionId");

        if (tempSessionId == null) {
            tempSessionId = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(userId)
                    .child("chat_sessions")
                    .push()
                    .getKey();
        }

        final String sessionId = tempSessionId;

        DatabaseReference newSessionRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("chat_sessions")
                .child(tempSessionId);

        chatRef = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(userId)
                .child("chat_sessions")
                .child(sessionId)
                .child("messages");

        // Firebase books reference (kept)
        databaseReference = FirebaseDatabase.getInstance().getReference("books");

        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageEditText = findViewById(R.id.messageEditText);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        chatAdapter = new ChatAdapter(messageList);

        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        navigationView.setNavigationItemSelectedListener(menuItem -> {

            if (menuItem.getItemId() == R.id.menu_history) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
                return true;
            }

            if (menuItem.getItemId() == R.id.menu_logout) {

                getSharedPreferences("user_session", MODE_PRIVATE)
                        .edit().clear().apply();

                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();

                return true;
            }

            if (menuItem.getItemId() == R.id.menu_books) {
                startActivity(new Intent(MainActivity.this, BookRecommendationActivity.class));
                return true;
            }

            if (menuItem.getItemId() == R.id.menu_compare) {
                startActivity(new Intent(MainActivity.this, BookComparisonActivity.class));
                return true;
            }

            return false;

        });

        // Default message
        messageList.add(new Message("Hello 👋 I am your Educational Chatbot.", false));

        // Load old chat
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                messageList.clear();

                for (DataSnapshot data : snapshot.getChildren()) {
                    Message msg = data.getValue(Message.class);
                    if (msg != null) {
                        messageList.add(msg);
                    }
                }


                chatAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
            }

            @Override
            public void onCancelled(DatabaseError error) { }
        });

        // SEND BUTTON
        sendButton.setOnClickListener(v -> {

            final String userMessage = messageEditText.getText().toString().trim();

            if (!userMessage.isEmpty()) {

                messageList.add(new Message(userMessage, true));
                chatAdapter.notifyDataSetChanged();

                // Save both messages
                chatRef.push().setValue(new Message(userMessage, true));

                getResponseFromFirebase(userMessage);


                if (isFirstMessage) {
                    FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(userId)
                            .child("chat_sessions")
                            .child(sessionId)
                            .child("title")
                            .setValue(userMessage);

                    isFirstMessage = false;
                }

                isFirstMessage = messageList.isEmpty();

                messageEditText.setText("");
                chatRecyclerView.scrollToPosition(messageList.size() - 1);
            }
        });
    }

    private void getResponseFromFirebase(String userMessage) {

        String input = userMessage.toLowerCase().trim();

        String subject = "";
        String topic = "";

        // SUBJECT DETECTION
        if (input.contains("java")) {
            subject = "java";
            lastSubject = "java";
        }
        else if (input.contains("dbms")) {
            subject = "dbms";
            lastSubject = "dbms";
        }
        else if (input.contains("os")) {
            subject = "os";
            lastSubject = "os";
        }
        else if (input.contains("python")) {
            subject = "python";
            lastSubject = "python";
        }
        else if (input.contains("ai")) {
            subject = "ai";
            lastSubject = "ai";
        }
        else {
            subject = lastSubject;
        }

        final String finalSubject = subject;

        // TOPIC DETECTION
        if (input.contains("oop")) topic = "oop";
        else if (input.contains("inheritance")) topic = "inheritance";
        else if (input.contains("sql")) topic = "sql";
        else if (input.contains("normalization")) topic = "normalization";
        else if (input.contains("process")) topic = "process";
        else if (input.contains("memory")) topic = "memory";
        else if (input.contains("loops")) topic = "loops";
        else if (input.contains("ml")) topic = "ml";

        // ❌ SUBJECT NOT FOUND
        if (subject.isEmpty()) {
            messageList.add(new Message("I only support DBMS, OS, Java, Python, and AI.", false));
            chatAdapter.notifyDataSetChanged();
            return;
        }



        // 🔥 FETCH FROM FIREBASE
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("topics")
                .child(subject);


        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                if (!snapshot.exists()) {
                    messageList.add(new Message("No data found for this topic.", false));
                    chatAdapter.notifyDataSetChanged();
                    return;
                }

                String description = snapshot.child("description").getValue(String.class);
                DataSnapshot subtopicsSnapshot = snapshot.child("subtopics");

                boolean foundSubtopic = false;
                String userInputLower = userMessage.toLowerCase();

                // 🔍 Check subtopics
                for (DataSnapshot child : subtopicsSnapshot.getChildren()) {

                    String key = child.getKey();

                    if (userInputLower.contains(key)) {
                        String answer = child.getValue(String.class);

                        messageList.add(new Message(answer, false));
                        chatAdapter.notifyDataSetChanged();
                        chatRecyclerView.scrollToPosition(messageList.size() - 1);

                        chatRef.push().setValue(new Message(answer, false));

                        foundSubtopic = true;
                        break;
                    }
                }

                if (!foundSubtopic) {

                    StringBuilder subtopicsList = new StringBuilder();
                    subtopicsList.append("\n\nYou can also ask about:\n");

                    for (DataSnapshot child : subtopicsSnapshot.getChildren()) {
                        String key = child.getKey();
                        subtopicsList.append("• ").append(key.toUpperCase()).append("\n");
                    }

                    String finalAnswer = description + subtopicsList.toString();

                    messageList.add(new Message(finalAnswer, false));
                    chatAdapter.notifyDataSetChanged();
                    chatRecyclerView.scrollToPosition(messageList.size() - 1);

                    chatRef.push().setValue(new Message(finalAnswer, false));
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                messageList.add(new Message("Database error", false));
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

}
