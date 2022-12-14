package com.selfieliveness.selfie_liveness;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import io.flutter.FlutterInjector;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.loader.FlutterLoader;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;
/**
 * Created by Jaimin Sarvaiya on 11-11-2019.
 * Copyright (c) 2019 MTPL. All rights reserved.
 */
public class SelfieDelegate  {
    Activity activity;
    private MethodChannel.Result pendingResult;
    private MethodCall methodCall;
    public SelfieDelegate(Activity activity) {
        this.activity = activity;
    }
    private void finishWithAlreadyActiveError(MethodChannel.Result result) {
        result.error("already_active", "Image picker is already active", null);
        clearMethodCallAndResult();
    }
    private void clearMethodCallAndResult() {
        methodCall = null;
        pendingResult = null;
    }
    public void detectLivelinesss(MethodCall methodCall, MethodChannel.Result result) {
        if (!setPendingMethodCallAndResult(methodCall, result)) {
            finishWithAlreadyActiveError(result);
            return;
        }


        this.pendingResult = result;



        //get asset path
        String logo = methodCall.argument("assetPath");
        String msgselfieCapture = methodCall.argument("msgselfieCapture");
        String poweredBy = methodCall.argument("poweredBy");
        String msgBlinkEye = methodCall.argument("msgBlinkEye");
        Intent intent = new Intent(activity, FaceTrackerActivity.class);
        intent.putExtra("msgselfieCapture", msgselfieCapture);
        intent.putExtra("logo",logo);
        intent.putExtra("poweredBy",poweredBy);
        intent.putExtra("msgBlinkEye", msgBlinkEye);
        activity.startActivityForResult(intent, 2899);
    }
    private boolean setPendingMethodCallAndResult(
            MethodCall methodCall, MethodChannel.Result result) {
        if (this.pendingResult != null) {
            return false;
        }
        this.methodCall = methodCall;
        pendingResult = result;
        return true;
    }

    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 2899) {
            if (resultCode == Activity.RESULT_OK) {
                if (null != data && null != data.getExtras()) {
                    String filePath = data.getStringExtra("filePath");
                    finishWithSuccess(filePath);
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                finishWithSuccess(null);
            }
        }
        return false;
    }
    private void finishWithSuccess(String imagePath) {
        Log.e("ImagePath","==="+imagePath);
        pendingResult.success(imagePath);
        clearMethodCallAndResult();
    }

}
