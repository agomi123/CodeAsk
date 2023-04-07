package com.example.codeask.Fragments;

import com.example.codeask.Notifications.MyResponse;
import com.example.codeask.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAtd97KdU:APA91bGpn8OdkekVL1kuHv3Ok50ge8Ah0M7TcociPmvof9YqAXAsguBJycqOkIwKuBtUCD9LL6HCUrCg507xjQV8WBcX9Z95Ni729dZEULfKtbgPuXDhOd5SKH1-02gvR8_BG1Ki3qdb"

            }
    )
    @POST("fcm/send")
    Call<MyResponse>sendNotification(@Body Sender body);
}
