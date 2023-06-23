package com.example.advanced_programming_2_android.repositories;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.advanced_programming_2_android.api.ChatsAPI;
import com.example.advanced_programming_2_android.database.AppDB;
import com.example.advanced_programming_2_android.database.Conversation;
import com.example.advanced_programming_2_android.database.ConversationDao;
import com.example.advanced_programming_2_android.database.Message;
import com.example.advanced_programming_2_android.database.User;

import java.util.ArrayList;

public class ConversationRepository {
    private ConversationDao conversationDao;
    private ConversationData conversationData;
    private ChatsAPI chatsAPI;
    private String token;
    private int conversationId;

    public ConversationRepository(int conversationId, String token) {
        AppDB db = AppDB.getInstance();
        conversationDao = db.getConversationDao();
        conversationData = new ConversationData();
        chatsAPI = new ChatsAPI();
        this.token = token;
        this.conversationId = conversationId;
    }

    class ConversationData extends MutableLiveData<Conversation> {
        public ConversationData() {
            super();
            Conversation conversation = conversationDao.getConversationById(conversationId);
            setValue(conversation);
        }

        @Override
        protected void onActive() {
            super.onActive();

            new Thread(() -> {
                conversationData.postValue(conversationDao.getConversationById(conversationId));
            }).start();
            chatsAPI.getChatById(conversationData, conversationId, token);
        }
    }

    public LiveData<Conversation> getConversation() {
        return conversationData;
    }

    public void getConversationById(){
        chatsAPI.getChatById(conversationData, conversationId, token);
    }

    public void sendMessageApi(String message, int chatId) {
        chatsAPI.createMessage(message, chatId, token);
        String content = trimString(message);
        // TODO - to change the username, displayname, profilePic
        User user = new User("username", "displayname", "pic");
        // maybe not necessary
        if (conversationData.getValue().getMessages() == null) {
            conversationData.getValue().setMessages(new ArrayList<>());
        }
        conversationData.getValue().getMessages().add(new Message(1, "createdDate", user, message));
    }

    public static String trimString(String input) {
        if (input == null) {
            return null;
        }

        String trimmedString = input.trim();
        trimmedString = trimmedString.replaceAll("(^\\n+)|(\\n+$)", "");

        return trimmedString;
    }
}
