package com.example.demo_filter;

import android.app.Activity;
import android.content.Context;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.StandardMessageCodec;
import io.flutter.plugin.platform.PlatformView;
import io.flutter.plugin.platform.PlatformViewFactory;

public class demo_platfotmview extends PlatformViewFactory {
    Activity activity1;
    BinaryMessenger msg;
    public demo_platfotmview(Activity activity, BinaryMessenger binaryMessenger) {
        super(StandardMessageCodec.INSTANCE);
        this.activity1 = activity;
        this.msg = binaryMessenger;
    }

    @Override
    public PlatformView create(Context context, int viewId, Object args) {
        return  new demo_faceview(activity1,viewId,msg,context);
    }
}
