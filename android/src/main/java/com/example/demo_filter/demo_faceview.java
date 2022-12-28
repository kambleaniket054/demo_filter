package com.example.demo_filter;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.media.CamcorderProfile;
import android.net.Uri;
import android.opengl.GLES20;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.ar.core.AugmentedFace;
import com.google.ar.core.CameraConfig;
import com.google.ar.core.CameraConfigFilter;
import com.google.ar.core.Config;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.collision.CollisionShape;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;

import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;
import com.google.ar.sceneform.ux.TransformableNode;
import com.google.ar.sceneform.ux.TransformationSystem;

import java.io.ByteArrayInputStream;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.Collection;
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
    ArSceneView arSceneViewnew;
    ArSceneView arSceneView;
     Context context;
    Session session;
    VideoRecorder recorder;
    int orintation;
    private ModelRenderable facerender;
    private Texture facetexture ;
    private final HashMap<AugmentedFace, AugmentedFaceNode> facenode = new HashMap<AugmentedFace, AugmentedFaceNode>();
    private final Scene.OnUpdateListener faceupdatelistener;
    public demo_faceview(Activity activity1, int viewId, BinaryMessenger msg, Context context)  {
        this.context = context;
        this.activity1 = activity1;
        try {
            arSceneView = new ArSceneView(activity1);
            ActivityCompat.requestPermissions(activity1,new String[]{Manifest.permission.CAMERA},0x123);
            session = new Session( context=  activity1, EnumSet.of(Session.Feature.FRONT_CAMERA));
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

            arSceneView.setupSession(session);
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
                       face.getMeshVertices();

                        facesetnode.setParent(arSceneView.getScene());
                        facesetnode.setAugmentedFace(face);
                        facesetnode.setFaceRegionsRenderable(facerender);
                        facesetnode.setFaceMeshTexture(facetexture);

                        Vector3 v1 = arSceneView.getScene().getCamera().getWorldPosition();
                        Vector3 v2 = arSceneView.getScene().getCamera().getForward();
                        Vector3 v3  =  Vector3.add(v1, v2.scaled(0.5f));
                        facesetnode.setWorldPosition(v3);
//                        facesetnode.setWorldScale(arSceneView.getScene().getSunlight().getWorldScale());
//                        facesetnode.setWorldPosition(arSceneView.getScene().getSunlight().getWorldPosition());
                        facesetnode.setLocalPosition(new Vector3(0.5f,0.1f,0.5f));
                       facesetnode.setLocalRotation(arSceneView.getScene().getCamera().getLocalRotation());
                        //facesetnode.setLocalScale(arSceneView.getScene().getCamera().getLocalScale());
                        facesetnode.setLocalScale(arSceneView.getScene().getCamera().getLocalScale());
                      // facesetnode.setLocalScale(new Vector3( face.getCenterPose().tx(), face.getCenterPose().ty(), face.getCenterPose().tz()));
                        facenode.put(face, facesetnode);
                    }
                    else if(facenode.get(face).getFaceRegionsRenderable() != facerender  ||  facenode.get(face).getFaceMeshTexture() != facetexture ){
                        facenode.get(face).setLocalPosition(arSceneView.getScene().getCamera().getLocalPosition());
                        facenode.get(face).setLocalRotation(arSceneView.getScene().getCamera().getLocalRotation());
                        //facesetnode.setLocalScale(arSceneView.getScene().getCamera().getLocalScale());
                        facenode.get(face).setLocalScale(arSceneView.getScene().getCamera().getLocalScale());
                        facenode.get(face).setFaceRegionsRenderable(facerender);
                        facenode.get(face).setFaceMeshTexture(facetexture);
                    }
                    if(face.getTrackingState() == TrackingState.STOPPED){
                        facenode.remove(face);
                    }
                }

            }
        };


    }



    private void setupLifeCycle(Context context, Activity activity1) {
        activityLifecycleCallbacks = new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
                try {
                    session = new Session(activity, EnumSet.of(Session.Feature.FRONT_CAMERA));
                } catch (UnavailableArcoreNotInstalledException e) {
                    e.printStackTrace();
                } catch (UnavailableApkTooOldException e) {
                    e.printStackTrace();
                } catch (UnavailableSdkTooOldException e) {
                    e.printStackTrace();
                } catch (UnavailableDeviceNotCompatibleException e) {
                    e.printStackTrace();
                }
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
            }

            @Override
            public void onActivityStarted(@NonNull Activity activity) {
                orintation =  activity.getResources().getConfiguration().orientation;
            }

            @Override
            public void onActivityResumed(@NonNull Activity activity) {
                orintation =  activity.getResources().getConfiguration().orientation;
                onresume();

            }

            @Override
            public void onActivityPaused(@NonNull Activity activity) {
//                arSceneView.getSession().pause();
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
                 arSceneView.getSession().close();
                 activity.finish();
            }
        };
        activity1.getApplication().registerActivityLifecycleCallbacks(this.activityLifecycleCallbacks);
    }

    private void onresume() {
        try {
           if (session == null){
               session = new Session(context, EnumSet.of(Session.Feature.FRONT_CAMERA));
               //SharedCamera sharecamera = session.getSharedCamera();
               Config config = new Config(session);
               config.setLightEstimationMode(Config.LightEstimationMode.AMBIENT_INTENSITY);
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
           }
//           else{
//               session.resume();
//               arSceneView.getSession().resume();
//           }
            session.resume();
            arSceneView.resume();
        } catch (UnavailableDeviceNotCompatibleException | UnavailableSdkTooOldException e) {
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
           // loadmesh(call.arguments);
            arviewset(call,result);
        }
        else if(call.method.equals("loadmesh")){
            loadmesh(call.arguments);

        }
        else if(call.method.equals("record")){
            Recordvideo(call.arguments,result);
        }
        else{
            result.error("001","no method found",null);
            result.notImplemented();
        }
    }

    private void Recordvideo(Object arguments, MethodChannel.Result result) {
        Map<String, String> map = (HashMap<String, String>) arguments;
        String data = (String) map.get("onoff");
        if(recorder == null){
            recorder = new VideoRecorder();
           recorder.setSceneView(arSceneView);

          recorder.setVideoQuality(CamcorderProfile.QUALITY_1080P,orintation);

        }
       boolean onoff = recorder.onToggleRecord();
      if(onoff){
          Toast.makeText(activity1, "Recording started", Toast.LENGTH_SHORT).show();
          result.success("success recording");
          channel.invokeMethod("success",data);
      }
      else {
          String videoPath = recorder.getVideoPath().getAbsolutePath();
          Toast.makeText(activity1, "Recording end with path: "+videoPath, Toast.LENGTH_SHORT).show();
          result.success(videoPath);
          channel.invokeMethod("success",data);
      }
       }

    private void loadmesh(Object arguments) {
        try {
           // String data = "";
        //String texture1 = "https://github.com/giandifra/arcore_flutter_plugin/blob/master/example/assets/fox_face_mesh_texture.png";
       Map map = (HashMap<String, String>) arguments;
        String data = (String) map.get("skin3DModelFilename");
        byte[] textureBytes = (byte[]) map.get("texturedata");
       if(data!=null){
           ModelRenderable.builder().setSource(activity1, Uri.parse(data)).setRegistryId(data).build().thenAccept(
                   modelRenderable -> {
                       AnimationData danceData = null;
                       facerender = modelRenderable;
                       if(facerender.getAnimationDataCount() > 0){
                            danceData = facerender.getAnimationData(0);
                           facerender.setShadowCaster(false);
                           facerender.setShadowReceiver(false);

//                           ModelAnimator andyAnimator = new ModelAnimator(danceData, facerender);
//                           andyAnimator.start();
                       }
                       else {
                           facerender.setShadowCaster(false);
                           facerender.setShadowReceiver(false);
                       }
                       //facerender.getBoneCount();

                       Toast.makeText(activity1,"succes model",Toast.LENGTH_SHORT).show();
                       //faceupdatelistener.notify();
                      // arSceneView.resume();

                   }

           ).exceptionally(
                   throwable -> {
                       Toast.makeText(activity1,"error model",Toast.LENGTH_SHORT).show();

                       Log.e("error","unable to load",throwable);
                       return null;
                   }
           );
       }
            if (textureBytes != null) {
                Texture.builder().setSource(BitmapFactory.decodeByteArray(textureBytes,0,textureBytes.length)).build().thenAccept(
                        texture -> {
                            facetexture = texture;
                        }
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity1,e.toString(),Toast.LENGTH_SHORT).show();
        }
    }

    private void arviewset(MethodCall call, MethodChannel.Result result) {
        try {
            arSceneView.setCameraStreamRenderPriority(Renderable.RENDER_PRIORITY_FIRST);
            arSceneView.getScene().addOnUpdateListener(faceupdatelistener);
            result.success("success");

        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(activity1,e.toString(),Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public View getView() {
        return arSceneView;
    }

    @Override
    public void dispose() {

    }
}
