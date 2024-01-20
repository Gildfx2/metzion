package com.example.pre_alpha.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAqmWorso:APA91bGWQz8YJRmCCGV2yLCSp91zeLX4u3B82EB--2q3qWqtdJN3NtWPjeOVlXogtYrt2MtrYU7xbg1M8HZ7XFzpiUulAxVgYJRjSNp3bRpg_Ys8kGvs4eLhCOqia2FN8NADeOoYuBrC"
            }
    )
    @POST("fcm/send")
    Call<Response> sendNotification(@Body Sender body);
}