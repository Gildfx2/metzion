package com.example.pre_alpha.notifications;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers( //specifies the content type as JSON and includes an authorization header with an FCM (firebase cloud messaging) server key
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAqmWorso:APA91bGWQz8YJRmCCGV2yLCSp91zeLX4u3B82EB--2q3qWqtdJN3NtWPjeOVlXogtYrt2MtrYU7xbg1M8HZ7XFzpiUulAxVgYJRjSNp3bRpg_Ys8kGvs4eLhCOqia2FN8NADeOoYuBrC"
            }
    )
    @POST("fcm/send") //specifies the HTTP POST request method and the endpoint URL to which the request will be sent (FCM)
    Call<Response> sendNotification(@Body Sender body); //specifies the structure of the HTTP request
}