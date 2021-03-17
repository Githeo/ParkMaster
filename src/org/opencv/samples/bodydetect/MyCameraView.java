package org.opencv.samples.bodydetect;

import java.io.FileOutputStream;
import java.util.List;

import org.opencv.android.JavaCameraView;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.util.AttributeSet;
import android.util.Log;

public class MyCameraView extends JavaCameraView implements PictureCallback {

    private static final String TAG = "OpenCV::MyCameraView";
    private String mPictureFileName;

    public MyCameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public float getFocalLength(){
    	return mCamera.getParameters().getFocalLength();
    }
    
    public List<int[]> getSupportedFPS(){
    	return mCamera.getParameters().getSupportedPreviewFpsRange();
    }
    
    public float getFOV(){
    	return mCamera.getParameters().getHorizontalViewAngle();
    }
    
    public List<Integer> getSupportedFrameRate(){
    	return mCamera.getParameters().getSupportedPreviewFrameRates();
    }
    
    public Size getCameraPreviewSize(){
    	return mCamera.getParameters().getPreviewSize();
    }
    
    public void setFrameRate(int rate){
    	Camera.Parameters params = mCamera.getParameters();
    	params.setPreviewFrameRate(rate);
    	mCamera.setParameters( params );
    }
    
    public List<String> getEffectList() {
        return mCamera.getParameters().getSupportedColorEffects();
    }

    public boolean isEffectSupported() {
        return (mCamera.getParameters().getColorEffect() != null);
    }

    public String getEffect() {
        return mCamera.getParameters().getColorEffect();
    }

    public void setEffect(String effect) {
        Camera.Parameters params = mCamera.getParameters();
        params.setColorEffect(effect);
        mCamera.setParameters(params);
    }

    public void setPreviewFPSMinMax(int min, int max){
        Camera.Parameters params = mCamera.getParameters();
        params.setPreviewFpsRange(min*1000, max*1000);
        mCamera.setParameters(params);
    }
    
    public void setPreviewFPS(int fps){
    	Camera.Parameters params = mCamera.getParameters();
    	params.setPreviewFrameRate(fps);
    	mCamera.setParameters(params);
    }
    
    public List<Size> getResolutionList() {
        return mCamera.getParameters().getSupportedPreviewSizes();
    }

    public void setResolution(Size resolution) {
        disconnectCamera();
        mMaxHeight = resolution.height;
        mMaxWidth = resolution.width;
        connectCamera(getWidth(), getHeight());
    }

    public Size getResolution() {
        return mCamera.getParameters().getPreviewSize();
    }

    public void takePicture(final String fileName) {
        Log.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        // Postview and jpeg are sent in the same buffers if the queue is not empty when performing a capture.
        // Clear up buffers to avoid mCamera.takePicture to be stuck because of a memory issue
        mCamera.setPreviewCallback(null);

        // PictureCallback is implemented by the current class
        mCamera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        // The camera preview was automatically stopped. Start it again.
        mCamera.startPreview();
        mCamera.setPreviewCallback(this);

        // Write the image in a file (in jpeg format)
        try {
            FileOutputStream fos = new FileOutputStream(mPictureFileName);

            fos.write(data);
            fos.close();

        } catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }

    }
}
