package com.example.ubercorp.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import com.example.ubercorp.R;

import java.io.ByteArrayOutputStream;

public class ImageHelper {

    public static void setProfileImage(String imageString, ImageView imageView) {
        if (imageString == null || imageView == null) return;

        try {
            if (imageString.startsWith("data:image") || isBase64(imageString)) {
                String base64Data = imageString;
                if (imageString.contains(",")) {
                    base64Data = imageString.split(",")[1];
                }

                byte[] decodedBytes = Base64.decode(base64Data, Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imageView.setImageBitmap(bitmap);
            } else {
                int resId = imageView.getContext().getResources()
                        .getIdentifier(imageString.replace(".png", ""), "drawable", imageView.getContext().getPackageName());
                if (resId != 0) imageView.setImageResource(resId);
            }
        } catch (Exception e) {
            e.printStackTrace();
            imageView.setImageResource(R.drawable.ic_account);
        }
    }

    private static boolean isBase64(String string) {
        try {
            Base64.decode(string, Base64.DEFAULT);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
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