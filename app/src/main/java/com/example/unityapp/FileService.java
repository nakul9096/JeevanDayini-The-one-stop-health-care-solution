package com.example.unityapp;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface FileService {
    @GET
    @Streaming
    Call<ResponseBody> downloadPdf(@Url String fileUrl);
}
