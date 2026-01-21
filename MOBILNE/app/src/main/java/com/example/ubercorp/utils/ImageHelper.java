package com.example.ubercorp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Base64;
import android.widget.ImageView;
import com.example.ubercorp.R;

import java.io.ByteArrayOutputStream;

public class ImageHelper {

    public static void setProfileImage(String base64Image, ImageView ivProfilePic) {
        if (base64Image == null || base64Image.isEmpty()) {
            ivProfilePic.setImageResource(R.drawable.ic_account);
            return;
        }

        try {
            String cleanBase64 = base64Image;
            if (base64Image.contains(",")) {
                cleanBase64 = base64Image.split(",")[1];
            }

            byte[] decodedBytes = Base64.decode(cleanBase64, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);

            if (bitmap != null) {
                Bitmap circularBitmap = getCircularBitmap(bitmap);
                ivProfilePic.setImageBitmap(circularBitmap);
            } else {
                ivProfilePic.setImageResource(R.drawable.ic_account);
            }
        } catch (Exception e) {
            android.util.Log.e("ImageHelper", "Error decoding profile image", e);
            ivProfilePic.setImageResource(R.drawable.ic_account);
        }
    }

    public static Bitmap getCircularBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int diameter = Math.min(width, height);

        Bitmap output = Bitmap.createBitmap(diameter, diameter, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        paint.setAntiAlias(true);

        canvas.drawCircle(diameter / 2f, diameter / 2f, diameter / 2f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        float left = (diameter - width) / 2f;
        float top = (diameter - height) / 2f;
        canvas.drawBitmap(bitmap, left, top, paint);

        return output;
    }

    public static Bitmap resizeBitmap(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > ratioBitmap) {
                finalWidth = (int) ((float)maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float)maxWidth / ratioBitmap);
            }
            return Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
        }
        return image;
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        String base64 = Base64.encodeToString(byteArray, Base64.NO_WRAP);
        return "data:image/jpeg;base64," + base64;
    }
}