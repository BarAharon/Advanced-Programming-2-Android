package com.example.advanced_programming_2_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.advanced_programming_2_android.classes.Chat;
import com.example.advanced_programming_2_android.viewModels.PreferencesViewModel;
import com.example.advanced_programming_2_android.viewModels.PreferencesViewModelFactory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private List<Chat> chats;
    private EditText etSearch;
    private FloatingActionButton addChatBtn;
    private ImageView settings;

    private ChatAdapter chatAdapter;
    private TextView displayName;
    private RoundedImageView profilePic;

    private Button btnSearch;
    private PreferencesViewModel preferencesViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        PreferencesViewModelFactory factory = new PreferencesViewModelFactory(getApplicationContext());
        preferencesViewModel = new ViewModelProvider(this, factory).get(PreferencesViewModel.class);

        String token = preferencesViewModel.getTokenLiveData().getValue();
        String username = preferencesViewModel.getUsernameLiveData().getValue();

        displayName = findViewById(R.id.displayName);
        profilePic = findViewById(R.id.profilePic);
        btnSearch = findViewById(R.id.btnSearch);
        etSearch = findViewById(R.id.etSearch);
        addChatBtn = findViewById(R.id.btnAddChat);
        settings = findViewById(R.id.settings_action_bar);

        displayName.setText(username);
        /*
        Glide.with(this)
                .load(uri)
                .into(profilePic);
         */

        btnSearch.setOnClickListener(view -> {
            String nameToSearch = etSearch.getText().toString();
            if (!nameToSearch.equals("")) {
                List<Chat> filteredChats = new ArrayList<>();
                for (Chat chat : chats) {
                    if (chat.getDisplayName().toLowerCase().startsWith(nameToSearch.toLowerCase())) {
                        filteredChats.add(chat);
                    }
                }
                chatAdapter.updateChats(filteredChats);
            } else {
                chats = generateChats();
                chatAdapter.updateChats(chats);
            }
        });

        ListView lvChats =  findViewById(R.id.lvChats);
        chats = generateChats();

        chatAdapter = new ChatAdapter(chats);
        lvChats.setAdapter(chatAdapter);

        lvChats.setOnItemClickListener((parent, view, position, id) -> {
            Chat c = chats.get(position);
            chatAdapter.notifyDataSetChanged();

            // for now not for later
            Intent intent = new Intent(this, MessageActivity.class);
            intent.putExtra("profilePic", c.getProfilePic().toString());
            intent.putExtra("displayName", c.getDisplayName());
            startActivity(intent);
        });

        addChatBtn.setOnClickListener(view -> {
            Intent intent = new Intent(this, AddChatActivity.class);
            startActivity(intent);
        });

        settings.setOnClickListener(view -> {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        });
    }

    private List<Chat> generateChats() {
        List<Chat> chats = new ArrayList<>();

        Chat c1 = new Chat(Uri.parse("data:image/jpeg;base64,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")
                , "Avi", "hi", "22.01.22");

        Chat c2 = new Chat(Uri.parse("data:image/jpeg;base64,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")
                , "Noa", "hello", "24.01.22");

        Chat c3 = new Chat(Uri.parse("data:image/jpeg;base64,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")
                , "Gabi", "how are you", "25.01.22");

        chats.add(c1);
        chats.add(c2);
        chats.add(c3);
        chats.add(c1);
        chats.add(c2);
        chats.add(c3);
        chats.add(c1);
        chats.add(c2);
        chats.add(c3);
        chats.add(c1);
        chats.add(c2);
        chats.add(c3);
        chats.add(c1);
        chats.add(c2);
        chats.add(c3);

        return chats;
    }
}