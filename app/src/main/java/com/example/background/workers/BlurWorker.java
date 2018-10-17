package com.example.background.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.background.Constants;
import com.example.background.R;

import java.io.FileNotFoundException;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static android.support.constraint.Constraints.TAG;

public class BlurWorker extends Worker {

    public BlurWorker(
            @NonNull Context appContext,
            @NonNull WorkerParameters workerParams) {
        super(appContext, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context applicationContext = getApplicationContext();
        String resourceUri = getInputData().getString(Constants.KEY_IMAGE_URI);

        try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri");
                throw new IllegalArgumentException("Invalid input uri");
            }

            ContentResolver resolver = applicationContext.getContentResolver();
            // Create a bitmap
            Bitmap picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)));

            Bitmap output = WorkerUtils.blurBitmap(picture, applicationContext);

            Uri outputUri = WorkerUtils.writeBitmapToFile(applicationContext, output);
            setOutputData(new Data.Builder().putString(Constants.KEY_IMAGE_URI,outputUri.toString()).build());


            WorkerUtils.makeStatusNotification("Output is "
                    + outputUri.toString(), applicationContext);


            return Worker.Result.SUCCESS;

        } catch (Throwable throwable) {
            throwable.printStackTrace();
            Log.e(TAG, "Error applying blur", throwable);
            return Worker.Result.FAILURE;
        }
    }

}
