package com.team15.chatapp.Clients;
import com.team15.chatapp.Notifications.MyResponse;
import com.team15.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAEohnQwc:APA91bEI10HSdX-mcya96y0lsrIiVd_cQa8Jcfw4yTSV5kxF0nZx6RIOiz6PCeiMXlKwsRn8V7lyVmkFXJglf2apchZ-ls-3sdsTxcQGL2cD35CtQ22a2KHGMq6zh3uN2Yxg5tRrmmcl"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
