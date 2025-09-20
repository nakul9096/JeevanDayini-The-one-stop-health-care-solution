package com.example.unityapp;

import android.graphics.Bitmap;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class QrCodeGenerator {
    public static Bitmap generateQrCode(String data) throws WriterException {
        if (data == null || data.isEmpty()) {
            return null;
        }
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        // Adjust the width and height as needed
        BitMatrix bitMatrix = new QRCodeWriter().encode(data, BarcodeFormat.QR_CODE, 500, 500);
        return barcodeEncoder.createBitmap(bitMatrix);
    }
}