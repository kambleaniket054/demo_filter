package com.example.demo_filter;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.view.TintableBackgroundView;

import com.google.ar.core.AugmentedFace;
import com.google.ar.core.CameraConfig;
import com.google.ar.core.CameraConfigFilter;
import com.google.ar.core.Config;
import com.google.ar.core.Session;
import com.google.ar.core.SharedCamera;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.rendering.ModelRenderable;

import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.platform.PlatformView;

public class demo_faceview implements PlatformView, MethodChannel.MethodCallHandler {
    Application.ActivityLifecycleCallbacks activityLifecycleCallbacks;
    MethodChannel channel;
    Activity activity1;
    ArSceneView arSceneView;
     Context context;
    Session session;
    private ModelRenderable facerender;
    private Texture facetexture;
    private HashMap<AugmentedFace, AugmentedFaceNode> facenode = new HashMap<AugmentedFace, AugmentedFaceNode>();
    private final Scene.OnUpdateListener faceupdatelistener;
    public demo_faceview(Activity activity1, int viewId, BinaryMessenger msg, Context context)  {
        this.context = context;
        this.activity1 = activity1;
        try {
            arSceneView = new ArSceneView(activity1);
            ActivityCompat.requestPermissions(activity1,new String[]{Manifest.permission.CAMERA},0x123);
          /*  session = new Session(*//* context= *//* activity1, EnumSet.of(Session.Feature.FRONT_CAMERA));
            //SharedCamera sharecamera = session.getSharedCamera();
            Config config = new Config(session);
            config.setAugmentedFaceMode(Config.AugmentedFaceMode.MESH3D);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            session.configure(config);
            CameraConfigFilter filter  = new CameraConfigFilter(session);
            filter.setFacingDirection(CameraConfig.FacingDirection.FRONT);
//            filter.setDepthSensorUsage(EnumSet.of(CameraConfig.DepthSensorUsage.DO_NOT_USE));
           // filter.setTargetFps(EnumSet.of(CameraConfig.TargetFps.TARGET_FPS_30));
            List<CameraConfig> cameraConfigs =  session.getSupportedCameraConfigs(filter);
            session.setCameraConfig(cameraConfigs.get(0));

            arSceneView.setupSession(session);*/
        } catch (Exception e) {
            e.printStackTrace();
        }

       // session.setCameraTextureName();


        channel = new MethodChannel(msg,"demo_filter"+viewId);
        channel.setMethodCallHandler(this);

        setupLifeCycle(context,activity1);
        faceupdatelistener = new Scene.OnUpdateListener() {
            @Override
            public void onUpdate(FrameTime frameTime) {
                Collection<AugmentedFace> facelist = Objects.requireNonNull(arSceneView.getSession()).getAllTrackables(AugmentedFace.class);
                for (AugmentedFace face : facelist) {
                    if (!facenode.containsKey(face)) {
                        AugmentedFaceNode facesetnode = new AugmentedFaceNode(face);
                        facesetnode.setParent(arSceneView.getScene());
                        facesetnode.setFaceRegionsRenderable(facerender);
                        facesetnode.setFaceMeshTexture(facetexture);
                        facenode.put(face, facesetnode);
                    }
                }

            }
        };


    }



    private void setupLifeCycle(Context context, Activity activity1) {
        activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {

            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
              onresume();
            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {

            }

            @Override
            public void onActivityStopped(@NonNull Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(@NonNull Activity activity) {
                 arSceneView.getScene().removeOnUpdateListener(faceupdatelistener);
            }
        };
        activity1.getApplication().registerActivityLifecycleCallbacks(this.activityLifecycleCallbacks);
    }

    private void onresume() {
        try {
            session = new Session(context, EnumSet.of(Session.Feature.FRONT_CAMERA));
            //SharedCamera sharecamera = session.getSharedCamera();
            Config config = new Config(session);
            config.setAugmentedFaceMode(Config.AugmentedFaceMode.MESH3D);
           config.setFocusMode(Config.FocusMode.AUTO);
            config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE);
            session.configure(config);
            CameraConfigFilter filter  = new CameraConfigFilter(session);
            filter.setFacingDirection(CameraConfig.FacingDirection.FRONT);
//            filter.setDepthSensorUsage(EnumSet.of(CameraConfig.DepthSensorUsage.DO_NOT_USE));
//            filter.setStereoCameraUsage(EnumSet.of(CameraConfig.StereoCameraUsage.REQUIRE_AND_USE));
             filter.setTargetFps(EnumSet.of(CameraConfig.TargetFps.TARGET_FPS_30));
            List<CameraConfig> cameraConfigs =  session.getSupportedCameraConfigs(filter);
            session.setCameraConfig(cameraConfigs.get(0));
           // session.setCameraTextureName(arSceneView.getId());
            arSceneView.setupSession(session);
            arSceneView.resume();
        } catch (UnavailableDeviceNotCompatibleException e) {
            e.printStackTrace();
        } catch (UnavailableSdkTooOldException e) {
            e.printStackTrace();
        } catch (UnavailableArcoreNotInstalledException e) {
            e.printStackTrace();
        } catch (UnavailableApkTooOldException e) {
            e.printStackTrace();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        if (call.method.equals("init")){
            arviewset(call,result);
        }
        else if(call.method.equals("loadmesh")){
            loadmesh(call.arguments);
        }
        else{
            result.error("001","no method found",null);
            result.notImplemented();
        }
    }

    private void loadmesh(Object arguments) {
        String texture1 = "sampledata/ArcticFox_Diffuse.png";
        Map map = (HashMap<String, String>) arguments;
        String data = (String) map.get("skin3DModelFilename");
       if(data!=null){
           ModelRenderable.builder().setSource(activity1, Uri.parse(data)).setRegistryId(data).build().thenAccept(
                   modelRenderable -> {
                       facerender = modelRenderable;
                   }

           ).exceptionally(
                   throwable -> {
                       Log.e("error","unable to load",throwable);
                       return null;
                   }
           );
       }
        Texture.builder().setSource(activity1,Uri.parse(texture1)).build().thenAccept(
                texture -> {
                    facetexture = texture;
                }
        );
        try {
            arSceneView.resume();
        } catch (CameraNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void arviewset(MethodCall call, MethodChannel.Result result) {
        String texture1 = "sampledata/ArcticFox_Diffuse.png";
        arSceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
        arSceneView.getScene().addOnUpdateListener(faceupdatelistener);

        result.success(null);
    }

    @Override
    public View getView() {
        return arSceneView;
    }

    @Override
    public void dispose() {

    }
}
