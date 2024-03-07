package com.example.unityapp;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FileClient {

    private static final String TAG = "FileClient";
    private static final String BASE_URL = "http://your_file_server_base_url.com/";

    private FileService fileService;

    public FileClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .build();

        fileService = retrofit.create(FileService.class);
    }

    public void downloadFile(final Context context, final String fileUrl, final String fileName) {
        Call<ResponseBody> call = fileService.downloadPdf(fileUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    boolean writtenToDisk = writeResponseBodyToDisk(context, response.body(), fileName);
                    if (writtenToDisk) {
                        Log.d(TAG, "File downloaded successfully");
                        // Notify the UI that the file has been downloaded successfully
                    } else {
                        Log.e(TAG, "Failed to write file to disk");
                        // Notify the UI that an error occurred while writing the file to disk
                    }
                } else {
                    Log.e(TAG, "Server responded with error: " + response.message());
                    // Notify the UI that the server responded with an error
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Failed to download file: " + t.getMessage());
                // Notify the UI that the file download failed
            }
        });
    }

    private boolean writeResponseBodyToDisk(Context context, ResponseBody body, String fileName) {
        try {
            File directory = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "pdfs");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File file = new File(directory, fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                }

                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
}
