package com.example.unityapp;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface APIService {
    @POST("send-notification")
    Call<Void> sendDataToServer(@Body NotificationData notificationData);
    @POST("/generate-invoice")
        // Endpoint for generating invoice
    Call<Void> sendInvoiceDataToServer(@Body User userData);
}
