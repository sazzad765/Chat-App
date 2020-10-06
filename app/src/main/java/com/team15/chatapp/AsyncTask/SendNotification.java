package com.team15.chatapp.AsyncTask;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonObject;
import com.team15.chatapp.Clients.APIService;
import com.team15.chatapp.Clients.Client;
import com.team15.chatapp.MessageActivity;
import com.team15.chatapp.Notifications.Data;
import com.team15.chatapp.Notifications.MyResponse;
import com.team15.chatapp.Notifications.Sender;
import com.team15.chatapp.Notifications.Token;
import com.team15.chatapp.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SendNotification extends Observable {
    private APIService apiService;
    private static SendNotification sendNotification;
    ArrayList<String> list=new ArrayList<String>();

    public static SendNotification getInstance() {
        if (sendNotification == null) {
            sendNotification = new SendNotification();
        }
        return sendNotification;
    }

    public SendNotification() {
        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    }

    public void send(String receiver, final String username, final String message,String fUserId,String userId) {
        list.add(receiver);
        list.add(username);
        list.add(message);
        list.add(fUserId);
        list.add(userId);

        new SendNotificationAsyncTask(apiService).execute(list);
    }

    private static class SendNotificationAsyncTask extends AsyncTask<List, Void, Void> {
        private APIService apiService;
        public SendNotificationAsyncTask(APIService apiService) {
            this.apiService = apiService;
        }

        @Override
        protected Void doInBackground(List... lists) {
            String receiver = (String) lists[0].get(0);
            final String username = (String) lists[0].get(1);
            final String message = (String) lists[0].get(2);
            final String fUserId = (String) lists[0].get(3);
            final String userId = (String) lists[0].get(4);

            DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
            Query query = tokens.orderByKey().equalTo(receiver);
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        Token token = snapshot.getValue(Token.class);
                        Data data = new Data(fUserId, R.mipmap.ic_launcher, username+": "+message, "New Message", userId);

                        Sender sender = new Sender(data, token.getToken());

                        apiService.sendNotification(sender)
                                .enqueue(new Callback<MyResponse>() {
                                    @Override
                                    public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                        if (response.code() == 200){
                                            if (response.body().success != 1){
//                                                Toast.makeText(, "Failed!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MyResponse> call, Throwable t) {

                                    }
                                });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            return null;
        }
    }


}
