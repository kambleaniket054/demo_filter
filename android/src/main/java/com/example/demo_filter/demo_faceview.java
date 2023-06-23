package com.example.demo_filter;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.CamcorderProfile;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.google.ar.core.Anchor;
import com.google.ar.core.AugmentedFace;
import com.google.ar.core.CameraConfig;
import com.google.ar.core.CameraConfigFilter;
import com.google.ar.core.Config;
import com.google.ar.core.Frame;
import com.google.ar.core.Pose;
import com.google.ar.core.Session;
import com.google.ar.core.TrackingState;
import com.google.ar.core.exceptions.CameraNotAvailableException;
import com.google.ar.core.exceptions.UnavailableApkTooOldException;
import com.google.ar.core.exceptions.UnavailableArcoreNotInstalledException;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableSdkTooOldException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.ArSceneView;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.Scene;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.Renderable;
import com.google.ar.sceneform.rendering.Texture;
import com.google.ar.sceneform.ux.AugmentedFaceNode;

//import org.opencv.android.BaseLoaderCallback;
//import org.opencv.android.LoaderCallbackInterface;
//import org.opencv.android.OpenCVLoader;
//import org.opencv.core.CvType;
//import org.opencv.core.Mat;
//import org.opencv.core.MatOfRect;
//import org.opencv.core.Rect;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.objdetect.CascadeClassifier;

import java.io.IOException;
import java.nio.ByteBuffer;
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
//        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION, activity1.getApplicationContext(), new BaseLoaderCallback(activity1.getApplicationContext()) {
//            @Override
//            public void onManagerConnected(int status) {
////                super.onManagerConnected(status);
//                switch (status){
//                    case LoaderCallbackInterface.SUCCESS:
//                        System.loadLibrary("Open-android10");
//                        break;
//                    default:
//                        super.onManagerConnected(status);
//                        break;
//                }
//            }
//        });
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

          // arSceneView.getPlaneRenderer().getMaterial().;

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
            loadmesh(call.arguments);
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
            String data = "";
//            Map<String, String> map = (HashMap<String, String>) arguments;
//            String data = (String) map.get("skin3DModelFilename");
//            String textureBytes = (String) map.get("texture");
//
//            File file  = File.createTempFile("modeldata", null, context.getCacheDir());

//            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
//            Uri uri = Uri.parse("URL of file to download");
//            DownloadManager.Request request = new DownloadManager.Request(uri);
//            request.setVisibleInDownloadsUi(true);
//            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//            request.set(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
//            downloadManager.enqueue(request);
//            String data = "";
        //String texture1 = "https://github.com/giandifra/arcore_flutter_plugin/blob/master/example/assets/fox_face_mesh_texture.png";
//             RequestQueue volley = Volley.newRequestQueue(context);
//             String url = data;

//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                    Request.Method.GET,
//                    url,
//                    null,
//                    (Response.Listener<JSONObject>) response -> {
//                        String dogImageUrl;
//                        try {
//                            dogImageUrl = response.getString("message");
//                            try (FileOutputStream stream = new FileOutputStream(file)) {
//                                try {
//                                    stream.write(dogImageUrl.getBytes());
//                                } finally {
//                                    stream.close();
//                                }
//                            }
//                            catch (Exception e){
//                                e.printStackTrace();
//                            }
//                            // load the image into the ImageView using Glide.
////                            Glide.with(MainActivity.this).load(dogImageUrl).into(mDogImageView);
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    },
//
//                    // lambda function for handling the case
//                    // when the HTTP request fails
//                    (Response.ErrorListener) error -> {
//                        // make a Toast telling the user
//                        // that something went wrong
//                        Toast.makeText(context, "Some error occurred! Cannot fetch dog image", Toast.LENGTH_LONG).show();
//                        // log the error message in the error stream
//                        Log.e("MainActivity", "loadDogImage error: ${error.localizedMessage}");
//                    }
//            );
//            volley.add(jsonObjectRequest);

//            RenderableSource.builder().setSource(activity1,Uri.parse("https://github.com/KhronosGroup/glTF-Sample-Models/raw/master/2.0/Duck/glTF/Duck.gltf"), RenderableSource.SourceType.GLTF2).build()
//       if(data!=null){
//            Mat gray = new Mat();
//
//            Frame frame = arSceneView.getArFrame();
//
//            if (frame == null) {
//                return;
//            }
//
//            // Get the camera image
//            Image image = frame.acquireCameraImage();
//
//            if (image == null) {
//                return;
//            }
//
//            // Convert the image to Mat format
//            int width = image.getWidth();
//            int height = image.getHeight();
//            ByteBuffer buffer = image.getPlanes()[0].getBuffer();
//            byte[] bytes = new byte[buffer.remaining()];
//            buffer.get(bytes);
//            Mat yuvMat = new Mat(height + height / 2, width, CvType.CV_8UC1);
//            yuvMat.put(0, 0, bytes);
//            Imgproc.cvtColor(yuvMat, gray, Imgproc.COLOR_YUV2RGBA_NV21);
////
////            // Release the camera image
//            image.close();
////
//            CascadeClassifier cascadeClassifier = new CascadeClassifier("haarcascade_hair.xml");
//            MatOfRect hairRects = new MatOfRect();
//            cascadeClassifier.detectMultiScale(gray, hairRects);
//            Rect hairRect = hairRects.toList().get(0);
//            float[] hairPosition = new float[]{(hairRect.x + hairRect.width / 2) / (float) yuvMat.width(),
//                    (hairRect.y + hairRect.height / 2) / (float) yuvMat.height(), -0.2f};
//            float scaleFactor = hairRect.width / 0.1f; // Scale hair model to size of hair in image
//            float[] hairRotation = new float[]{0f, 0f, 0f, 1f};
            Anchor anchor =  arSceneView.getSession().createAnchor(new Pose(new float[]{0,0,0}, new float[]{0, 0, 0, 1}));

            AnchorNode anchorNode = new AnchorNode(anchor);
            anchorNode.setParent(arSceneView.getScene());
//            Frame frame1
//            TransformableNode hairNode = new TransformableNode(arSceneView.getScene().getView().getRenderer().getContext);
//            hairNode.setParent(anchorNode);

            ModelRenderable.builder().setSource(activity1,Uri.parse(data)).setRegistryId(data).build().thenAccept(
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
//       }
//            if (textureBytes != null) {
//            URL u = new URL("https://github.com/kambleaniket054/database/blob/main/native_face.png");
//            byte[] imageBytes = IOUtils.toByteArray(u.openStream());
//            new loadimage().execute("https://github.com/kambleaniket054/database/blob/main/native_face.png");
//            Bitmap texte = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), Uri.parse("https://github.com/kambleaniket054/database/blob/main/native_face.png")));
//                ByteArrayOutputStream stream = new ByteArrayOutputStream();
//                texte.compress(Bitmap.CompressFormat.JPEG,80,stream);
//                byte[] byteArray = stream.toByteArray();
//                Bitmap compressedBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length);
               // ivCompressed.setImageBitmap(compressedBitmap);

//            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity1,"error data"+e.toString(),Toast.LENGTH_SHORT).show();
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


    class loadimage extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap texte = null;
            try {
                texte = ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.getContentResolver(), Uri.parse("https://github.com/kambleaniket054/database/blob/main/native_face.png")));
            } catch (IOException e) {
                e.printStackTrace();
            }

            return texte;
        }

        @Override
       protected void onPostExecute(Bitmap bitmap) {
            Toast.makeText(activity1.getApplicationContext(),
                    "ByteArray created new..",
                    Toast.LENGTH_SHORT).show();

            Texture.builder().setSource(bitmap).setRegistryId(bitmap).build().thenAccept(
                    texture -> {
                        facetexture = texture;
                        Log.e("success","load texture successful");
                    }
            ).exceptionally(
                    throwable -> {
                        Toast.makeText(activity1,"error texture",Toast.LENGTH_SHORT).show();
                        Log.e("error","unable to load texture",throwable);
                        return null;
                    }
            );
       }
   }
}
