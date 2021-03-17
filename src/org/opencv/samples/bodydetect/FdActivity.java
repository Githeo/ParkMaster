/**
 * Camera orientation issue:
 * 	http://developer.android.com/reference/android/hardware/Camera.Parameters.html#setPreviewFpsRange%28int,%20int%29
 * 	http://answers.opencv.org/question/7313/rotating-android-camera-to-portrait/
 * 	http://littlecheesecake.me/blog/13804736/opencv-android-orientation
 * 
 * Sensors:
 * 
 *  Broadcom BCM47521 GPS  on my S4
I/OpenCV  (13908): SENSOR NAME = K330 3-axis Accelerometer TYPE = 1
I/OpenCV  (13908): SENSOR NAME = YAS532 Magnetic Sensor TYPE = 2
I/OpenCV  (13908): SENSOR NAME = K330 Gyroscope sensor TYPE = 4
I/OpenCV  (13908): SENSOR NAME = Barometer Sensor TYPE = 6
I/OpenCV  (13908): SENSOR NAME = MAX88920 Proximity Sensor TYPE = 8
I/OpenCV  (13908): SENSOR NAME = CM3323 RGB Sensor TYPE = 5
I/OpenCV  (13908): SENSOR NAME = SHTC1 relative humidity sensor TYPE = 12
I/OpenCV  (13908): SENSOR NAME = SHTC1 ambient temperature sensor TYPE = 13
I/OpenCV  (13908): SENSOR NAME = YAS532 Magnetic Sensor UnCalibrated TYPE = 14
I/OpenCV  (13908): SENSOR NAME = SAMSUNG Significant Motion Sensor TYPE = 17
I/OpenCV  (13908): SENSOR NAME = SAMSUNG Step Detector Sensor TYPE = 18
I/OpenCV  (13908): SENSOR NAME = SAMSUNG Step Counter Sensor TYPE = 19
I/OpenCV  (13908): SENSOR NAME = UnCalibrated Gyroscope sensor TYPE = 16
I/OpenCV  (13908): SENSOR NAME = Screen Orientation Sensor TYPE = 65558
I/OpenCV  (13908): SENSOR NAME = Rotation Vector Sensor TYPE = 11
I/OpenCV  (13908): SENSOR NAME = Gravity Sensor TYPE = 9
I/OpenCV  (13908): SENSOR NAME = Linear Acceleration Sensor TYPE = 10
I/OpenCV  (13908): SENSOR NAME = Orientation Sensor TYPE = 3

 */

package org.opencv.samples.bodydetect;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Range;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;
import org.opencv.objdetect.CascadeClassifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



//import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.FileAppender;
import ch.qos.logback.core.util.StatusPrinter;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.view.View.OnTouchListener;

//import org.bytedeco.javacv.FFmpegFrameRecorder;
//import org.bytedeco.javacv.FrameRecorder.Exception;
//import com.googlecode.javacv.cpp.opencv_core;
//import com.googlecode.javacv.cpp.opencv_core.IplImage;
//import com.googlecode.javacv.cpp.*;
//import com.googlecode.javacv.*;
//import org.bytedeco.javacpp.*;
//import org.bytedeco.javacv.*;
//import org.bytedeco.javacv.FFmpegFrameRecorder;
//import static org.bytedeco.javacpp.opencv_core.*;


public class FdActivity extends Activity 
	implements CvCameraViewListener2, OnTouchListener, com.google.android.gms.location.LocationListener,
	com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener,
	com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks  {

	/*
	static {
	    System.loadLibrary("avutil");
	    System.loadLibrary("opencv_core");
	}*/
	
    public static final String    TAG                 = "OpenCVTest";
    public static final String    LOCATION_TAG       = "LOCATION_LOG";
    
    private static final Scalar    FACE_RECT_COLOR     = new Scalar(0, 255, 0, 255);
    private static final Scalar    FACE_RECT_COLOR_CAR     = new Scalar(0, 255, 255, 255);
    public static final int        JAVA_DETECTOR       = 0;
    public static final int        NATIVE_DETECTOR     = 1;
    public static final float	  CAR_HEIGHT		  = 140; // mm			
    public static final float	  CAR_WIDTH		  	= 400; // mm
    public static final float	  CAR_WIDTH_BEHIND		  = 170; // mm
    public static final float	  SENSOR_HEIGHT		  = 140; // mm
    
    
    //CALIBRATION
    public static int 			CALIBRATION_COUNTER			= 0;
    public static int 			CALIBRATION_COUNTER_LIMIT	= 20;
    public static int			FRAME_PAUSE = 120;	//how many frames will be skipped after detecting an object during the calibration
	private static int 			framePause = FRAME_PAUSE; 
    public static final float	PARKING_SIGNAL_HEIGHT		  = (float)193;//480; // mm   7.5 inches /  7.75 ->190.5 / 196.85
    public static final float	PARKING_SIGNAL_WIDTH		  =  (float) 193; //480; // mm
    public static final float	PARKING_SIGNAL_ELEVATION		  = (float) 1100;//;((float)495-60/*108*/); //1041;//1041;// 114;//1041;//2300; // mm   41 inches ->1041 mm
    public static final Size 	PARKING_MIN_PIXEL_SIZE = new Size (50,50); //objects smaller that this will not be detected
    public static final double 	CALIBRATION_SCALE_FACTOR = 1.1;
    public static final int		CALIBRATION_MIN_NEIGH = 2;
    
    
    //CAR DETECTION
    public static final Size 	CAR_MIN_PIXEL_SIZE = new Size (216,216); //objects smaller that this will not be detected
    public static final double 	CAR_SCALE_FACTOR = 1.1;
    public static final int		CAR_MIN_NEIGH = 2;
    
    private MenuItem               mItemFace50;
    private MenuItem               mItemFace40;
    private MenuItem               mItemFace30;
    private MenuItem               mItemFace20;
    private MenuItem               mItemType;

    private Mat                    mRgba;
    private Mat                    mGray;
    private File                   mCascadeFile;
    private CascadeClassifier      mJavaDetector;
    //private DetectionBasedTracker  mNativeDetector;

    private int                    mDetectorType       = JAVA_DETECTOR;
    private String[]               mDetectorName;

    //private float                  mRelativeFaceSize   = 0.2f;
    //private int                    mAbsoluteFaceSize   = 0;

    //private CameraBridgeViewBase   mOpenCvCameraView;
    private MyCameraView   				mOpenCvCameraView;

    private List<Camera.Size> mResolutionList;
    private MenuItem[] mEffectMenuItems;
    private SubMenu mColorEffectsMenu;
    private MenuItem[] mResolutionMenuItems;
    private SubMenu mResolutionMenu;
    private SubMenu mFPSRangeMenu;
    private MenuItem[] mFPSMenuItems;
    
    //----------- SENSORS----------------//
    private SensorManager sm;
    private Sensor magnetometer;
    private Sensor accelerometer;
    private Sensor gyro;
    private MySensorListener mSensorListener;
    //------------------------------------//
    
    //-------------LOCATION---------------//
    private LocationManager locationManager;
    private MyLocator mlocationListener;
    private Location mLastLocation;
    private Location mCurrentLocation;
    private static final String REQUESTING_LOCATION_UPDATES_KEY  = "gps_update_req";
    private static final String LOCATION_KEY = "location_key";
    private static final String LAST_UPDATED_TIME_STRING_KEY = "last_update_key";
    private boolean mRequestingLocationUpdates = true;
    private String mLastUpdateTime;
    //-------------------------------------//

    //----------- GOOGLE SERVICES ---------------------//
    public GoogleApiClient mGoogleApiClient;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private boolean mResolvingError = false;
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private LocationRequest mLocationRequest;
    //--------------------------------------------------//
    
    //------------- Logger------------------------------//
    // static private final Logger logger = LoggerFactory.getLogger(FdActivity.class);
    public static Logger logger;
    public static final String LOGGER_FOLDER = "ParkingTest/";
    public static String experimentDate;
    public Camera recCamera;
    public MediaRecorder mMediaRecorder;
    public Mat videoImage;
    private String ffmpeg_link;
    //private volatile FFmpegFrameRecorder recorder;
    private int frameRate = 30;
    private boolean initRecorderStep = Boolean.TRUE;
    private boolean isRecording = false;
    //---------------------------------------------------//
    
    private boolean carDetecting = false;

    public static TextView textViewGPS;
    public static TextView textViewSensor;
    
    public NumberFormat nf = NumberFormat.getInstance();
    
    public ArrayList<Mat>  objectPointsCalibration = new ArrayList<Mat>();
    public ArrayList<Point> imagePointsCalibration = new ArrayList<Point>();
   
    
    private List<Mat> mCornersBuffer = new ArrayList<Mat>();

   // public ArrayList<Mat> rvecs = new ArrayList<Mat>();
    //public ArrayList<Mat> tvecs = new ArrayList<Mat>();
    public Mat rMatrix;
    public Mat rVec;
    public Mat tVec;
    public Mat cameraMatrix; // cameraMatrix = |fx 0 cx| |0 fy cy| |0 0 1|
    
    private boolean storeNextFrame = false;
    private String picDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)+"/"+"cardetection/";

    private double mRms;
    private boolean testing = false;
    
    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");

                    File file = new File(picDirectory, "cardetection" );
                    if (!file.mkdirs()) {
                        Log.d(TAG, "onDirectory not created " + file);
                    } else {
                        Log.d(TAG, "onTouch album created " + file);
                    }
                    
                    // Load native library after(!) OpenCV initialization
                    System.loadLibrary("detection_based_tracker");
                    
                    if (!carDetecting){
                    	loadClassifier(R.raw.parking_classifier04); 
                    	
                        // init calibration variables
                        rMatrix = new Mat();
                        rVec = new Mat();
                        tVec = new Mat();      	
                        mRms = 0;
                    	double f =  1115;//getFocalLengthPixel(1280);
                    	Log.i(TAG, "F : " + f);
                		cameraMatrix = new Mat(3, 3, CvType.CV_64FC1); // |fx 0 cx| |0 fy cy| |0 0 1|
                		cameraMatrix.put(0, 0, f);
                		cameraMatrix.put(0, 1, 0);
                		cameraMatrix.put(0, 2, 639.5);
                		cameraMatrix.put(1, 0, 0);
                		cameraMatrix.put(1, 1, f/*1115*/);
                		cameraMatrix.put(1, 2, 479.5);
                		cameraMatrix.put(2, 0, 0);
                		cameraMatrix.put(2, 1, 0);
                		cameraMatrix.put(2, 2, 1);
                		
                		
                		/*cameraMatrix.put(0, 0, 2550);
                		cameraMatrix.put(0, 1, 0);
                		cameraMatrix.put(0, 2, -(3264-1)/2);
                		cameraMatrix.put(1, 0, 0);
                		cameraMatrix.put(1, 1, 2550);
                		cameraMatrix.put(1, 2, -(2448-1)/2);
                		cameraMatrix.put(2, 0, 0);
                		cameraMatrix.put(2, 1, 0);
                		cameraMatrix.put(2, 2, 1);*/
                		
                		Log.i("CALIBRATION", cameraMatrix.dump());
                   
                		//roll 0(90); azimuth 0; pitch 0
                		int Cr = 1;
                		int Sr = 0;
                		int Cp = 1;
                		int Sp = 0;
                		int Cy = 1;
                		int Sy = 0;
                		rMatrix = new Mat(3, 3, CvType.CV_64FC1);
                		rMatrix.put(0, 0, Cr*Cy+Sr*Sy*Sp);
                		rMatrix.put(0, 1, Cp*Sy);
                		rMatrix.put(0, 2, -Sr*Cy+Cr*Sp*Sy);
                		rMatrix.put(1, 0, -Cr*Sy+Sr*Sp*Cy);
                		rMatrix.put(1, 1, Cp*Cy);
                		rMatrix.put(1, 2, Sr*Sy+Cr*Sp*Cy);
                		rMatrix.put(2, 0, Sr*Cp);
                		rMatrix.put(2, 1, -Sp);
                		rMatrix.put(2, 2, Cr*Cp);
         

    /*          
                		rMatrix.put(0, 0, 0.999998401842087);
                		rMatrix.put(0, 1, 0.0001649324871630203);
                		rMatrix.put(0, 2, 0.001780199580512963);
                		rMatrix.put(1, 0, -0.0001687299621436826);
                		rMatrix.put(1, 1, -0.999997710418017);
                		rMatrix.put(1, 2, 0.002133234380875089);
                		rMatrix.put(2, 0, -0.001779843664947936);
                		rMatrix.put(2, 1, -0.002133531344637513);
                		rMatrix.put(2, 2, 0.9999961400928155);
                		*/
                		Log.i("DEBUG", "Rotation Matrix : " + rMatrix.dump());

                		//tVec = new Mat(1, 3, CvType.CV_64FC1);
                		//Log.i("DEBUG", "tvec : " + tVec.rows() + " " + tVec.cols());

                		
                		/*tVec.put(0, 0, 20.35131686);
                		tVec.put(1, 0, 5.67119128);//-5.5);
                		tVec.put(2, 0, -5.9418193);*/
                		
                		/*
                		tVec.put(0, 0, 0);
                		tVec.put(1, 0, 5.08);
                		tVec.put(2, 0, 0);*/
                    	
                    }
                    //VideoCapture cap(0);
                    //cap.set(CV_CAP_PROP_FPS, 10);
            		
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.setOnTouchListener(FdActivity.this);
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public void loadClassifier(int classifierID){
        try {
            // load cascade file from application resources
            InputStream is = getResources().openRawResource(classifierID);
            File cascadeDir = getDir("cascade", Context.MODE_PRIVATE);
            mCascadeFile = new File(cascadeDir, "parking_classifier04.xml");
            FileOutputStream os = new FileOutputStream(mCascadeFile);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            is.close();
            os.close();

            mJavaDetector = new CascadeClassifier(mCascadeFile.getAbsolutePath());
            if (mJavaDetector.empty()) {
                Log.e(TAG, "Failed to load cascade classifier");
                mJavaDetector = null;
            } else
                Log.i(TAG, "Loaded cascade classifier from " + mCascadeFile.getAbsolutePath());
            
            cascadeDir.delete();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "Failed to load cascade. Exception thrown: " + e);
        }
    }
    
    public FdActivity() {
        mDetectorName = new String[2];
        mDetectorName[JAVA_DETECTOR] = "Java";
        mDetectorName[NATIVE_DETECTOR] = "Native (tracking)";
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Log.i(TAG, "called onCreate");
    	super.onCreate(savedInstanceState);
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    	setContentView(R.layout.face_detect_surface_view);
    	nf.setMaximumFractionDigits(3);

    	initLogger();
	
    	mResolvingError = savedInstanceState != null
    	            && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

    	// mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.fd_activity_surface_view);  // uncomment to use opencv Camera 
    	mOpenCvCameraView = (MyCameraView) findViewById(R.id.fd_activity_surface_view); // uncomment to use Java Camera
    	// mOpenCvCameraView.setCvCameraViewListener(this); // commented to record video

    	textViewGPS = (TextView) findViewById(R.id.textViewGPSData);
    	textViewSensor = (TextView) findViewById(R.id.textViewSensorData);

    	// these for Google Service init
    	buildGoogleApiClient();
    	createLocationRequest();
    	updateValuesFromBundle(savedInstanceState);
		
    	// init all the sensors
    	initSensors();
    	
    	// Initiate Localization System
    	locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocationListener);
		//locationManager.getLastKnownLocation(provider)
		
		/*
		if (prepareVideoRecorder()){
			Log.i("Recorder", "prepared, now starting...");
            mMediaRecorder.start();
            isRecording = true;
		} else
            releaseMediaRecorder();
		*/
		// initRecorder();
    }
      
    private void initLogger() {
    	File loggerFolder = new File(Environment.getExternalStorageDirectory(), LOGGER_FOLDER);
    	if (!loggerFolder.exists()) 
    		loggerFolder.mkdirs();
    	
    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd_HH:mm");
    	experimentDate = df.format(Calendar.getInstance().getTime());
    	
    	String loggerFile = new String(loggerFolder.getAbsolutePath() + "/" + experimentDate);

    	LoggerContext lc = (LoggerContext)LoggerFactory.getILoggerFactory();
    	lc.reset();

    	// setup FileAppender
    	PatternLayoutEncoder encoder1 = new PatternLayoutEncoder();
    	encoder1.setContext(lc);
    	// check here the layout http://logback.qos.ch/manual/layouts.html
    	// S = msec, r = relative time [msec] since application starting
    	// encoder1.setPattern("%d{HH:mm:ss.SSS} %r [%thread] %-5level %logger{0} - %msg%n"); 
    	encoder1.setPattern("%d{HH:mm:ss.SSS} %r - %msg%n");
    	encoder1.start();

    	FileAppender<ILoggingEvent> fileAppender = new FileAppender<ILoggingEvent>();
    	fileAppender.setContext(lc);
    	//fileAppender.setFile(this.getFileStreamPath("logger2").getAbsolutePath());
    	fileAppender.setFile(loggerFile);
    	fileAppender.setEncoder(encoder1);
    	fileAppender.start();

    	// add the newly created appenders to the root logger;
    	// qualify Logger to disambiguate from org.slf4j.Logger
    	ch.qos.logback.classic.Logger root = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    	root.addAppender(fileAppender);
    	
    	logger = LoggerFactory.getLogger(FdActivity.class);
        logger.info("Logger Test!!!!!!!!!!!!!!");
    }

	@Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY,
                mRequestingLocationUpdates);
        outState.putParcelable(LOCATION_KEY, mCurrentLocation);
        outState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(outState);
    }
    
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                // setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocationis not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(
                        LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }
    
    @Override
    public void onPause()
    {
        if (mOpenCvCameraView != null){
            mOpenCvCameraView.disableView();
        }
        sm.unregisterListener(mSensorListener);
        stopLocationUpdates();
        
        if (isRecording) {
        	// stop recording and release camera
        	mMediaRecorder.stop();  // stop the recording
        	releaseMediaRecorder(); // release the MediaRecorder object
        	recCamera.lock();         // take camera access back from MediaRecorder
        	Log.i("Recording", "Stop recording now !");
        	isRecording = false;
        }
        
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mResolvingError) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onResume()
    {
        super.onResume();
        
        // comment this to unable opencv camera
        // OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
        
        sm.registerListener(mSensorListener, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
        sm.registerListener(mSensorListener, magnetometer, SensorManager.SENSOR_DELAY_FASTEST);
        sm.registerListener(mSensorListener, gyro, SensorManager.SENSOR_DELAY_FASTEST);
        
        checkGooglePlayServices();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

	public void onDestroy() {
        super.onDestroy();
        CALIBRATION_COUNTER = 0;
        mOpenCvCameraView.disableView();
        
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }
    
	protected synchronized void buildGoogleApiClient() {
	    mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(LocationServices.API)
	        .build();
	}
	
	protected void createLocationRequest() {
	    mLocationRequest = new LocationRequest();
	    mLocationRequest.setInterval(5000); // msec
	    mLocationRequest.setFastestInterval(1000); //msec
	    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}
	
	private void checkGooglePlayServices() {
		int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
		if (resultCode != ConnectionResult.SUCCESS){
			Log.e(LOCATION_TAG, "No GOOGLE SERVIVE AVAILABLE ON THIS PHONE");
			GooglePlayServicesUtil.getErrorDialog(resultCode, this, 1).show();
		}
	}
    
    public void onCameraViewStarted(int width, int height) {
        mGray = new Mat();
        mRgba = new Mat();
    }

    public void onCameraViewStopped() {
        mGray.release();
        mRgba.release();
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
    	
    	//http://developer.android.com/reference/android/hardware/Camera.Parameters.html
    	Log.i(TAG, "FOCAL LENGHT = "+ mOpenCvCameraView.getFocalLength()); // focal lenght = 4.2
    	Log.i(TAG, "IMAGE HEIGHT = "+ Integer.toString(mOpenCvCameraView.getCameraPreviewSize().height)); // focal lenght = 4.2
    	logger.info("Focal lenght [mm]="+ mOpenCvCameraView.getFocalLength() + " - [pixel]="+getFocalLengthPixel(mOpenCvCameraView.getCameraPreviewSize().width));
    	
//    	if (initRecorderStep){
//    		//initRecorder();
//    		prepareVideoRecorder();
//    		initRecorderStep = Boolean.FALSE;
//    	}
//    	
    	if (testing){
    		framePause--;
    		if (mSensorListener.HasValidData()){
    			if(framePause<0){
    				framePause=5;
    				Mat mtxR = new Mat(3, 3, CvType.CV_64FC1);
    				Mat mtxQ = new Mat(3, 3, CvType.CV_64FC1); // |fx 0 cx| |0 fy cy| |0 0 1|

    				Mat rx = new Mat(3, 3, CvType.CV_64FC1);
    				Mat ry = new Mat(3, 3, CvType.CV_64FC1);
    				Mat rz = new Mat(3, 3, CvType.CV_64FC1);

    				Mat phoneR = new Mat(3, 3, CvType.CV_64FC1);
    				phoneR.put(0,0,mSensorListener.phoneRotationMatrix[0]);
    				phoneR.put(0,1,mSensorListener.phoneRotationMatrix[1]);
    				phoneR.put(0,2,mSensorListener.phoneRotationMatrix[2]);
    				phoneR.put(1,0,mSensorListener.phoneRotationMatrix[3]);
    				phoneR.put(1,1,mSensorListener.phoneRotationMatrix[4]);
    				phoneR.put(1,2,mSensorListener.phoneRotationMatrix[5]);
    				phoneR.put(2,0,mSensorListener.phoneRotationMatrix[6]);
    				phoneR.put(2,1,mSensorListener.phoneRotationMatrix[7]);
    				phoneR.put(2,2,mSensorListener.phoneRotationMatrix[8]);
    				Calib3d.RQDecomp3x3(phoneR, mtxR,mtxQ, rx,ry,rz);

    				//Log.i("POSITION", "Orientation " +mSensorListener.azimut + " " + mSensorListener.pitch + " " + mSensorListener.roll);
    				//Log.i("POSITION", "rx" + rx.dump());
    				//Log.i("POSITION", "ry" + ry.dump());
    				//Log.i("POSITION", "rz" + rz.dump());

    				//x picture -> y phone
    				//y picture -> x phone
    				Point3 p = new Point3(0,3000,3000) ; 
    				Point3  tmpP;// = GetRealWorldCoordinate(p,  rx,new Point(0,0));
    				//Log.i("POSITION", "rx" + tmpP);
    				//tmpP = GetRealWorldCoordinate(p,  ry,new Point(0,0));
    				//Log.i("POSITION", "ry" + tmpP);
    				//tmpP = GetRealWorldCoordinate(p,  rz,new Point(0,0));
    				//Log.i("POSITION", "rz" + tmpP);
    				tmpP = GetRealWorldCoordinate(p,  phoneR,new Point(0,0));
    				Log.i("POSITION", "phoneR" + tmpP + " Azim " + mSensorListener.azimut);

    			}
    		}

    	}	
    	
        mRgba = inputFrame.rgba(); // channel=4, depth=0, type=24-CV_8U
        mGray = inputFrame.gray();
        
        Log.i("OPENCVRECORDER", "RGBA Channels=" + mRgba.channels() + " Depth=" + mRgba.depth()
        	+ " Height=" + mRgba.height() + " Widht=" + mRgba.width() + " Rows="+mRgba.rows()
        	+ " Colns=" + mRgba.cols() + " TYPE="+mRgba.type());

        Date time = new Date();
    	Core.putText(mRgba, "Time: " + time,new Point(20, 20), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);

        
    	Log.i("CALIBRATION", "NEW FRAME! " + mRgba.width()+"x"+mRgba.height());

        if (CALIBRATION_COUNTER < CALIBRATION_COUNTER_LIMIT){
    		framePause--;
    		 Core.putText(mRgba, Integer.toString(CALIBRATION_COUNTER),
             		new Point(50, 475), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);
             Core.putText(mRgba, rMatrix.get(0,0)[0] +" " + rMatrix.get(0,1)[0] +" " +rMatrix.get(0,2)[0],
             		new Point(50, 500), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);
             Core.putText(mRgba, rMatrix.get(1,0)[0] +" " + rMatrix.get(1,1)[0] +" " +rMatrix.get(1,2)[0],
             		new Point(50, 525), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);
             Core.putText(mRgba, rMatrix.get(2,0)[0] +" " + rMatrix.get(2,1)[0] +" " +rMatrix.get(2,2)[0],
             		new Point(50, 550), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);
         	
             Core.putText(mRgba,tVec.dump()+ " ERR: " + mRms,
             		new Point(50, 600), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);
             
    	    if(framePause<0){
    	   		framePause=FRAME_PAUSE;
    	   	} else {
    	   		return mRgba;
    	   	}    
        }
        // consider only right half of the image:
        // int cols = inputFrame.gray().cols();
        // int rows = inputFrame.gray().rows();
        // Rect roi = new Rect(cols/2, 0, cols/2, rows);
        //mGray = inputFrame.gray().submat(roi);
        
        Log.i(TAG, "MGRAY ORIGINAL" + inputFrame.gray().cols() + " - " + inputFrame.gray().rows());
        Log.i(TAG, "MGRAY ROOOOOOI" + mGray.cols() + " - " + mGray.rows());
        
        /*if (mAbsoluteFaceSize == 0) {
            int height = mGray.rows();
            if (Math.round(height * mRelativeFaceSize) > 0) {
                mAbsoluteFaceSize = Math.round(height * mRelativeFaceSize);
            }
            //mNativeDetector.setMinFaceSize(mAbsoluteFaceSize);
        }*/


        MatOfRect faces = new MatOfRect();

        if (mDetectorType == JAVA_DETECTOR) {
            if (mJavaDetector != null){
            	
            	if (CALIBRATION_COUNTER < CALIBRATION_COUNTER_LIMIT){
            		  mJavaDetector.detectMultiScale(mGray, faces, CALIBRATION_SCALE_FACTOR, CALIBRATION_MIN_NEIGH, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
            				  PARKING_MIN_PIXEL_SIZE, new Size());
            		
            	} else {
            		mJavaDetector.detectMultiScale(mGray, faces, CAR_SCALE_FACTOR, CAR_MIN_NEIGH, 2, // TODO: objdetect.CV_HAAR_SCALE_IMAGE
                            CAR_MIN_PIXEL_SIZE,new Size());
            	}
              
            }
        } else {
            Log.e(TAG, "Detection method is not selected!");
        }

        Rect[] facesArray = faces.toArray();
		int counter = 0;
		
		
		if(facesArray.length==0){
			Core.putText(mRgba, Integer.toString(CALIBRATION_COUNTER),
             		new Point(50, 475), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);
             Core.putText(mRgba, rMatrix.get(0,0)[0] +" " + rMatrix.get(0,1)[0] +" " +rMatrix.get(0,2)[0],
             		new Point(50, 500), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);
             Core.putText(mRgba, rMatrix.get(1,0)[0] +" " + rMatrix.get(1,1)[0] +" " +rMatrix.get(1,2)[0],
             		new Point(50, 525), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);
             Core.putText(mRgba, rMatrix.get(2,0)[0] +" " + rMatrix.get(2,1)[0] +" " +rMatrix.get(2,2)[0],
             		new Point(50, 550), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);
         	
             Core.putText(mRgba,tVec.dump()+ " ERR: " + mRms,
             		new Point(50, 600), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);
		} 

        for (int i = 0; i < facesArray.length; i++){
        	
        	Log.i("CALIBRATION", "Counter="+CALIBRATION_COUNTER + " Limit="+CALIBRATION_COUNTER_LIMIT);
        	if (CALIBRATION_COUNTER < CALIBRATION_COUNTER_LIMIT){
        		
        		CALIBRATION_COUNTER++;
        		Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
        		Core.putText(mRgba, onStreetSignalDetection(mGray.width(), mGray.height(), facesArray[i]), 
        				new Point(facesArray[i].tl().x, facesArray[i].tl().y-10), Core.FONT_HERSHEY_PLAIN, 2, FACE_RECT_COLOR, 3);
        		
        		
        		//u = 1274.03039551
        		//v =   1359.90734863
        		
//        		Core.putText(mRgba, computeCarDistance(/*3264*/mGray.width(),/*2448*/ mGray.height(), facesArray[i]), 
  //      				new Point(facesArray[i].tl().x, facesArray[i].tl().y-10), Core.FONT_HERSHEY_PLAIN, 2, FACE_RECT_COLOR, 3);
        			

        		
        		//} else { // Detect cars here...
        		//	Log.i("CALIBRATION", "FINAL ROTATION MATRIX: " + rMatrix.dump());
        		//	Log.i("CALIBRATION", "DETECTING CARS NOW...");

        		//
        		// right part image detection only:
        		//Core.rectangle(mRgba, new Point(cols/2 + facesArray[i].tl().x, facesArray[i].tl().y), 
        		//		new Point(cols/2 + facesArray[i].br().x, facesArray[i].br().y), FACE_RECT_COLOR, 4);
        		//
        		//
        		//Core.putText(mRgba, nf.format(computeDistance(facesArray[i].height)/1000), new Point(cols/2 + facesArray[i].tl().x, facesArray[i].tl().y), 
        		//		Core.FONT_HERSHEY_PLAIN, 7, FACE_RECT_COLOR, 3);
        		//Core.putText(mRgba, nf.format(computeDistanceMeth2(facesArray[i].height)/1000), new Point(cols/2 + facesArray[i].tl().x, facesArray[i].tl().y-100), 
        		//		Core.FONT_HERSHEY_PLAIN, 7, FACE_RECT_COLOR, 3);
        		//
        		// for parking signals:
        		// Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), FACE_RECT_COLOR, 3);
        		if (CALIBRATION_COUNTER == CALIBRATION_COUNTER_LIMIT){ // CHANGE CLASSIFIER HERE ...
        			Log.i("CALIBRATION", "Changing classifier now");
        			loadClassifier(R.raw.classifier_t1_02);
        			carDetecting = true;
        		}
        		break; 
        	} else {
        		Scalar colourOffset = new Scalar(counter,counter+10,counter+50);

        		Log.i("CALIBRATION", "Detecting cars now..." + facesArray.length+ " " + FACE_RECT_COLOR_CAR +" " + FACE_RECT_COLOR_CAR.mul(colourOffset));
        		
        		Core.rectangle(mRgba, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255/(counter+1),255/(counter+1), 255/(counter+1)), 3);
        		// print X,Z on video to allow accuracy evaluation on the field
///        		Core.putText(mRgba, computeCarDistance(mGray.width(), mGray.height(), facesArray[i]), 
   //     				new Point(facesArray[i].tl().x, facesArray[i].tl().y-10), Core.FONT_HERSHEY_PLAIN, 2, FACE_RECT_COLOR, 3);
        		double cx = (mGray.width()-1)/2;
            	double cy = (mGray.height()-1)/2;
        		
            	double offset = 125;
            	//bottom left
            	Point3 point3d = computeDistanceToPointOnTheFloor(facesArray[i].tl().x, 
            			mGray.height() - facesArray[i].br().y,getFocalLengthPixel(mGray.width()), cx, cy);
            	
            	Core.putText(mRgba, point3d.toString(),
        				new Point(50, 500+50+counter*offset), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255/(counter+1),255/(counter+1), 255/(counter+1)), 3);
        		
            	//TODO Matrix
/*
            	Point phonePosition = mlocationListener.GetLatLongPoint(); //TODO third param GetRealWorldCoordinate (instead of Point(0,0)
                Point3 realWorld = GetRealWorldCoordinate(point3d, mSensorListener.phoneRotationMatrix, new Point(0,0));
                Log.d(TAG, "My Position " + phonePosition.toString() + "; Car real world point " + realWorld.toString());
            	*/
            	//bottom middle point
                point3d = computeDistanceToPointOnTheFloor(facesArray[i].tl().x+facesArray[i].width/2, 
                		mGray.height()-facesArray[i].br().y,getFocalLengthPixel(mGray.width()), cx, cy);
            	Core.putText(mRgba,point3d.toString(),
        				new Point(50, 500+75+counter*offset), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255/(counter+1),255/(counter+1), 255/(counter+1)), 3);
            	
            	//realWorld = GetRealWorldCoordinate(point3d, mSensorListener.phoneRotationMatrix, new Point(0,0));
                //Log.d(TAG, "Real world point " + realWorld.toString());
               	
            	//bottom right
            	point3d = computeDistanceToPointOnTheFloor(facesArray[i].br().x, 
            			mGray.height()-facesArray[i].br().y,getFocalLengthPixel(mGray.width()), cx, cy);
            	Core.putText(mRgba, point3d.toString(),
        				new Point(50, 500+100+counter*offset), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255/(counter+1),255/(counter+1), 255/(counter+1)), 3);
            	//realWorld = GetRealWorldCoordinate(point3d, mSensorListener.phoneRotationMatrix, new Point(0,0));
                //Log.d(TAG, "Real world point " + realWorld.toString());
               	
        		//Core.putText(mRgba, computeCarDistance(
        		///*3264*/mGray.width(),/*2448*/ mGray.height(), facesArray[i]), 
        			//	new Point(50, 50), Core.FONT_HERSHEY_PLAIN, 2, FACE_RECT_COLOR, 3);
            	
            	counter++;

        	}
        }
       
        if(storeNextFrame){
        	//we need to store the frame
        	if((CALIBRATION_COUNTER < CALIBRATION_COUNTER_LIMIT) || facesArray.length>0){
	        	storeNextFrame = false;
	        	

	          	if (Highgui.imwrite(picDirectory+"pic_" + time + ".jpg", mRgba)){
		        //if (Highgui.imwrite("/storage/emulated/0/Pictures/cardetection/pic_" + time + ".jpg", mRgba)){
	        	    Log.d(TAG, "SUCCESS writing image to external storage" + time);
	            	Core.putText(mRgba, "OK",new Point(500, 500), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);

	        	}else{
	        	    Log.d(TAG, "Fail writing image to external storage " + picDirectory+"pic_" + time + ".jpg");
	        	}
	        } else {
            	Core.putText(mRgba, "FAILED",new Point(50, 500), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);
	        }
        }
    	//Core.putText(mRgba, time.toString(),new Point(50, 50), Core.FONT_HERSHEY_PLAIN, 2, new Scalar(0, 255,255, 255), 3);
		
        return mRgba;
    }

    public String onStreetSignalDetection(int imageWidht, int imageHeight, Rect rectangle){
    	/* 
    	 *  Formula:
    	 * 	Z = (Yb - Ya) * f / (Vb - Va)
    		Xa = (cx-ua) * Z / f
    		Xb = (cx-ub) * Z / f
    		Ya = (cy-va) * Z / f
    		Yb = (cy-vb) * Z / f 
    	 */
    	double f = getFocalLengthPixel(imageWidht);
		cameraMatrix.put(0, 0, f);
		cameraMatrix.put(1, 1, f);

		Log.i("CALIBRATIONP", "cameraMatrix: " +  cameraMatrix.dump());
		float cx = imageWidht/2;
    	float cy = 0;//imageHeight/2;
    	float Z = (float)( PARKING_SIGNAL_HEIGHT * f ) / rectangle.height;
    	float Xa = (float) ((rectangle.tl().x-cx) * Z / f); //( -(imageWidht/2 - rectangle.tl().x ) * Z / f);
    	float Xb =  (float)((rectangle.br().x-cx) * Z / f);// ( -(imageWidht/2 - rectangle.br().x) * Z / f);
    	float Yb = PARKING_SIGNAL_ELEVATION;// + (imageHeight/2 - rectangle.tl().y) * Z / f; //+50.8+ (imageHeight/2 - rectangle.tl().y) * Z / f;
    	float Yt = PARKING_SIGNAL_ELEVATION + PARKING_SIGNAL_HEIGHT; //50.8+ (imageHeight/2 - rectangle.br().y) * Z / f;
    	
    	cx=0;
    	
    	StringBuilder info = new StringBuilder("Z=" + Math.round(Z) + " Xa(3d/2d)=" + Math.round(Xa) +"/"+rectangle.tl().x+" Xb=(3d/2d)"  + Math.round(Xb)+"/"+rectangle.br().x);
    	info.append(" Yb=(3d/2d)" + Math.round(Yb)+"/"+rectangle.br().y + " Ytop=(3d/2d)" + Math.round(Yt)+"/"+rectangle.tl().y + " f: " + f);
    	info.append(" BBH=" + rectangle.height);    	
    	Log.i(TAG, info.toString());

    	//MatOfPoint3f objPoint = new MatOfPoint3f(new Point3(Xa, Ya, Z));//, new Point3(Xa, Yb, Z) ,	new Point3(Xb, Ya, Z), new Point3(Xb, Yb, Z));
    	
    	/*
    	Point3 newPoint = new Point3(Xa, Yb, Z);
    	objectPointsCalibration.add(newPoint);
    	newPoint = new Point3(Xb, Yb, Z);
    	objectPointsCalibration.add(newPoint);

    	newPoint = new Point3(Xa, Yt, Z);
    	objectPointsCalibration.add(newPoint);

    	newPoint = new Point3(Xb, Yt, Z);
    	objectPointsCalibration.add(newPoint);

    	*/
    	//MatOfPoint2f imgPoint = new MatOfPoint2f(new Point(rectangle.tl().x, rectangle.tl().y));//, 
 //   			new Point(rectangle.tl().x, rectangle.br().y) ,
   // 			new Point(rectangle.br().x, rectangle.tl().y), new Point(rectangle.br().x, rectangle.br().y));
    	imagePointsCalibration.clear(); //TODO remove it if we go back for our calibration
    	
    	imagePointsCalibration.add(new Point(rectangle.tl().x-cx,imageHeight- ((rectangle.br().y)-cy)));
    	imagePointsCalibration.add(new Point(rectangle.br().x-cx,imageHeight-((rectangle.br().y)-cy)));
    	imagePointsCalibration.add(new Point(rectangle.tl().x-cx,imageHeight- ((rectangle.tl().y)-cy)));
    	imagePointsCalibration.add(new Point(rectangle.br().x-cx,imageHeight- ((rectangle.tl().y)-cy)));
    	
    	
    	
    	imagePointsCalibration.add(new Point((rectangle.br().x - rectangle.width/2)-cx,imageHeight- ((rectangle.tl().y)-cy)));
    	imagePointsCalibration.add(new Point((rectangle.br().x - rectangle.width/2)-cx,imageHeight- ((rectangle.br().y)-cy)));
    	imagePointsCalibration.add(new Point((rectangle.br().x - rectangle.width/2)-cx,(imageHeight- ((rectangle.br().y-rectangle.height/2)-cy))));
    	imagePointsCalibration.add(new Point(rectangle.br().x-cx,imageHeight- ((rectangle.br().y-rectangle.height/2)-cy)));
    	imagePointsCalibration.add(new Point(rectangle.tl().x-cx,imageHeight- ((rectangle.br().y-rectangle.height/2)-cy)));

    	
    	int numberOfPoints = 9;
        Mat objectPoint = new Mat();
        calcBoardCornerPositions(objectPoint,numberOfPoints, Xa, Xb, Yt, Yb, Z);
        objectPointsCalibration.add(objectPoint);
    	
    	
       // List<Point> imagePointsCalibrationList = List<Point>();
        //imagePointsCalibrationList.add(new Point(rectangle.tl().x/*-imageWidht/2*/, (/*imageHeight/2-*/rectangle.br().y)));
    	
        MatOfPoint2f mCorners = new MatOfPoint2f();
        mCorners.fromList(imagePointsCalibration);
        //mCorners.put(0, 0, rectangle.tl().x,rectangle.br().y);
       // mCorners.put(0, 1, );
        
     /*   mCorners.put(0, 0, rectangle.br().x);
        mCorners.put(0, 1, rectangle.br().y);
        
        mCorners.put(0, 0, rectangle.tl().x);
        mCorners.put(0, 1, rectangle.tl().y);
        
        mCorners.put(0, 0, rectangle.tl().x);
        mCorners.put(0, 1, rectangle.tl().y);*/
        Log.i("CALIBRATIONP", "mCorners LIST " + mCorners.dump());

        mCornersBuffer.add(mCorners.clone());
    	
    	//Log.i("DEBUG", "LIST SIZE " + imagePointsCalibration.size() + " " + objectPointsCalibration.size());
    	//Log.i("DEBUG", "LIST" + imagePointsCalibration.toString() + "------" + objectPointsCalibration.toString());

    	if (/*objectPointsCalibration.size()>=*/CALIBRATION_COUNTER  == CALIBRATION_COUNTER_LIMIT){
        	MatOfPoint2f imgPoint = new MatOfPoint2f();
        	imgPoint.fromList(imagePointsCalibration);//, 
        	//MatOfPoint3f imgPoint3 = new MatOfPoint3f();
        	//imgPoint3.fromList(objectPointsCalibration);//
    		calibrate(/*imgPoint3,*/ imgPoint,imageWidht,imageHeight);
    	}
    	return info.toString();
    }
    
    public void calibrate(/*MatOfPoint3f objectPoints,*/ MatOfPoint2f imagePoints, int imageWidht, int imageHeight){	
    	MatOfDouble distCoeffs = new MatOfDouble();
    	Log.i("CALIBRATION", "calibrating after new detection");
    	
    	Size mImageSize = new Size(imageWidht, imageHeight);
    	int  mFlags = Calib3d.CALIB_FIX_PRINCIPAL_POINT +
                Calib3d.CALIB_ZERO_TANGENT_DIST +
                Calib3d.CALIB_FIX_ASPECT_RATIO +
                Calib3d.CALIB_FIX_K4 +
                Calib3d.CALIB_FIX_K5+
                Calib3d.CALIB_FIX_ASPECT_RATIO+
                Calib3d.CALIB_USE_INTRINSIC_GUESS;
    	
    	/*  List<Mat> rvecs = new ArrayList<Mat>();
          List<Mat> tvecs = new ArrayList<Mat>();
    	
          
        Calib3d.calibrateCamera(rvecs, tvecs, mImageSize,
        		cameraMatrix, distCoeffs, rVec, tVec, mFlags);

    	*/
    	int numberOfPoints = 9;
        ArrayList<Mat> objectPointsBis = new ArrayList<Mat>();
        //List<Mat> mCornersBuffer = new ArrayList<Mat>();

        
        
        
        /*
        objectPointsBis.add(Mat.zeros(numberOfPoints, 1, CvType.CV_32FC3));
        calcBoardCornerPositions(objectPointsBis.get(0),numberOfPoints );
        Log.i("CALIBRATIONP", "objectPoints SIZE:  " + objectPointsBis.size() + " " + objectPointsBis.get(0).size());
        for (int i = 1; i < mCornersBuffer.size(); i++) {
        	objectPointsBis.add(objectPointsBis.get(0));
        }
        */	
        ArrayList<Mat> rvecs = new ArrayList<Mat>();
        ArrayList<Mat> tvecs = new ArrayList<Mat>();
        
    /*    for(int i = 0; i < mCornersBuffer.size(); i++){
        	  Mat objpt = objectPointsBis.get(i);
              Mat imgpt1 = mCornersBuffer.get(i);
              int ni = objpt.checkVector(3, CvType.CV_32F);
              int ni1 = imgpt1.checkVector(2, CvType.CV_32F);
              //Log.i("CALIBRATIONP", "objectPointsBis " + ni + " mCornersBuffer " + ni1);
        }
      */  
        
        //Log.i("CALIBRATIONP", "objectPoints SIZE:  " + objectPoints.total());
        Log.i("CALIBRATIONP", "Calibrating the camera " + mCornersBuffer.size());
        Calib3d.calibrateCamera(objectPointsCalibration, mCornersBuffer, mImageSize,
        		cameraMatrix, distCoeffs, rvecs, tvecs, mFlags);

       // Calib3d.Rodrigues(rvecs.get(rvecs.size()-1), rMatrix); // take only the last rotation vector

        
        
        boolean mIsCalibrated = Core.checkRange(cameraMatrix)
                && Core.checkRange(distCoeffs);
        Log.i("CALIBRATIONP", "Is the camera calibrated? " + mIsCalibrated);
        if (!mIsCalibrated){
        	CALIBRATION_COUNTER_LIMIT++;
        	return;
        }
        Mat reprojectionErrors = new Mat();

        mRms = computeReprojectionErrors(objectPointsCalibration, rvecs, tvecs,distCoeffs, reprojectionErrors);
        if(mRms>5){
            Log.i("CALIBRATIONP", "Error too high, restarting " + mRms);
            //resetting everything
            //objectPointsBis.clear();
            objectPointsCalibration.clear();
            mCornersBuffer.clear();
           // cameraMatrix =  new Mat(3, 3, CvType.CV_64FC1);
            rvecs.clear();
            tvecs.clear();
            CALIBRATION_COUNTER =0;
            
            return;
        }
        
        Log.i(TAG, String.format("Average re-projection error: %f", mRms));
        Log.i(TAG, "Camera matrix: " + cameraMatrix.dump());
        Log.i(TAG, "Distortion coefficients: " + distCoeffs.dump());
        // adding print for rotation vector
        Log.i(TAG, "Number of rotation vectors: " + rvecs.size());
        for (int i=0; i<rvecs.size(); i++)
        	Log.i(TAG, "Vector " + i + " " + rvecs.get(i).dump());
        //Mat rMatrix = new Mat(3, 3, CvType.CV_64FC1);
        Log.i(TAG, "Rotation Matrix. Calling Rodrigues.");
        Calib3d.Rodrigues(rvecs.get(rvecs.size()-1), rMatrix); // take only the last rotation vector
        Log.i("CALIBRATIONP", "Rotation Matrix: " + rMatrix.dump());
        tVec=tvecs.get(tvecs.size()-1);
        Log.i(TAG, "Translation vector: " + tvecs.get(tvecs.size()-1).dump());
        
        
    	//Calib3d.solvePnP(objectPoints, imagePoints, cameraMatrix, distCoeffs, rVec, tVec);
    	//Calib3d.Rodrigues(rVec, rMatrix);
    	

    	Log.i("CALIBRATIONP", "PUTTANA tVec rows="+tVec.rows() + "tVec cols="+tVec.cols() + " " +rMatrix.t().rows() + " " + rMatrix.t().cols() );

    	/*rMatrix = rMatrix.t();
    	Mat tmp = new Mat(3, 1, CvType.CV_64FC1);
    	for (int row=0; row<3; row++){
			tmp.put(row,0, 0);
    		for(int col=0; col <3; col++){
    			tmp.put(row, 0, tmp.get(row,0)[0] + rMatrix.get(row,col)[0]*tVec.get(row,0)[0]);
    		}
			tmp.put(row,0, tmp.get(row,0)[0]*-1);

    	}
    	tVec = tmp;
*/
    	//Log.i("CALIBRATIONP", "rVec="+ rVec.dump() + "\ntVec=" + tVec.dump() + "\nrMatrix="+rMatrix.dump());
    	//Log.i("CALIBRATIONP", objectPoints.size() + " " + imagePoints.size()+ " 3d point: "+ objectPoints.dump() + " 2d points " + imagePoints.dump());
    	Log.i("CALIBRATIONP", "tVec rows="+tVec.rows() + "tVec cols="+tVec.cols());
    }
    
    private void calcBoardCornerPositions(Mat corners, int numberOfPoints, float xl, float xr, float yb, float yt, float z) {
        final int cn = 3;
        float positions[] = new float[numberOfPoints * cn];
        int counter =0;
        float Z = z;
        positions[0+cn*counter] = xl;
        positions[1+cn*counter] = PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = xl+PARKING_SIGNAL_WIDTH;
        positions[1+cn*counter] = PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = xl;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT+PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++; 
        positions[0+cn*counter] = xl+PARKING_SIGNAL_WIDTH;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT+PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = xl+PARKING_SIGNAL_WIDTH/2;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT+PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = xl+PARKING_SIGNAL_WIDTH/2;
        positions[1+cn*counter] = PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = xl+PARKING_SIGNAL_WIDTH/2;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT/2 +PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = xl+PARKING_SIGNAL_WIDTH;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT/2 +PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = xl;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT/2 +PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        
        
        
        
        /*positions[0+cn*counter] = 0;
        positions[1+cn*counter] = PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = PARKING_SIGNAL_WIDTH;
        positions[1+cn*counter] = PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = 0;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT+PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++; 
        positions[0+cn*counter] = PARKING_SIGNAL_WIDTH;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT+PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = PARKING_SIGNAL_WIDTH/2;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT+PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = PARKING_SIGNAL_WIDTH/2;
        positions[1+cn*counter] = PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = PARKING_SIGNAL_WIDTH/2;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT/2 +PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = PARKING_SIGNAL_WIDTH;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT/2 +PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        counter++;
        positions[0+cn*counter] = 0;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT/2 +PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = Z;
        */
    	//Log.i(TAG, "3dPoints LIST " + positions.length + " " + positions[11]);
        corners.create(numberOfPoints, 1, CvType.CV_32FC3);
        corners.put(0, 0, positions);
    }
    
    private Point3 computeDistanceToPointOnTheFloor(double x, double y, double f, double cx, double cy){
    	Log.i("CALIBRATION", rMatrix.dump());
    	Log.i("DEBUG", "point: "+x + " " + y);
    	
    	
    	//Log.i("CALIBRATION", "rMatrix.get(2, 2)[0]=" + rMatrix.get(2, 2)[0] + " rMatrix.get(0, 2)[0]="+rMatrix.get(0, 2)[0]);
    	double a = rMatrix.get(2, 2)[0] * x - rMatrix.get(2, 2)[0] *cx - f * rMatrix.get(0, 2)[0]; 
    	
    	Log.i("fFormula", "A components " +rMatrix.get(2, 2)[0] + "x" + x + " " +  rMatrix.get(2, 2)[0] *cx + " " +  f * rMatrix.get(0, 2)[0]);
    	//a : 775.777549633 -921.142506106 1563.54912905
    	double ww = rMatrix.get(2, 2)[0] * y - rMatrix.get(2, 2)[0] * cy - f * rMatrix.get(1, 2)[0];
    	
    	double XXX_num = f * tVec.get(1, 0)[0]*a/ww - tVec.get(2, 0)[0]*y*a/ww 
    			+ tVec.get(2, 0)[0]*cy*a/ww - f*tVec.get(0, 0)[0] + tVec.get(2, 0)[0]*x - tVec.get(2, 0)[0]*cx;
    	
    	double X = XXX_num / (f*rMatrix.get(0, 0)[0] - rMatrix.get(2, 0)[0]*x + rMatrix.get(2, 0)[0]*cx 
    			- f*rMatrix.get(1, 0)[0]*a/ww + rMatrix.get(2, 0)[0]*y*a/ww - rMatrix.get(2, 0)[0]*cy*a/ww);
    	
    	double Z = (f*rMatrix.get(0,0)[0]*X + f*tVec.get(0,0)[0] - rMatrix.get(2,0)[0]*X*x + rMatrix.get(2,0)[0]*X*cx 
    				- tVec.get(2,0)[0]*x + tVec.get(2, 0)[0]*cx)
    				/ (rMatrix.get(2,2)[0]*x - rMatrix.get(2,2)[0]*cx - f*rMatrix.get(0,2)[0]);
    	
    	
    	Log.i("FFormula", "a: " + a + " ww: "+ ww + "XXX_: " + XXX_num + " Z " + Z + " X " +X);
    	Log.i("CALIBRATION", "Car at : " + Double.toString(X) + " / " + Double.toString(Z));
    	//return "Z="+Double.toString(Z) + " X="+Double.toString(X);
    	return new Point3(X,0,Z);
    }
    
    private String computeCarDistance(double imageWidht, double imageHeight, Rect rectangle){
    /*
    X = XXX_num / (fx*R[0,0] - R[2,0]*u + R[2,0]*cx - fy*R[1,0]*a/ww+R[2,0]*v*a/ww-sR[2,0]*cy*a/ww)
    		Z = (fx*R[0,0]*X + fx*t[0] - R[2,0]*X*u - R[2,0]*X*0.5*(w-1) - t[2]*u - t[2]*0.5*(w-1))/(R[2,2]*u+R[2,2]*0.5*(w-1) - fx*R[0,2])

    		a = R[2,2]*u-R[2,2]*cx-fx*R[0,2]
    		ww = R[2,2]*v -R[2,2]*cy - fy*R[1,2]
    		XXX_num = fy*t[1]*a/ww - t[2]*v*a/ww + t[2]*cy*a/ww - fx*t[0] + t[2]*u - t[2]*cx
    */
    	Log.i("CALIBRATION", rMatrix.dump());
    	
    	Log.i("DEBUG", "rectangle: "+rectangle.toString());

    	
    	
    	//rectangle = new Rect(-1888, -1022, 10,10);
    	Log.i("BoxSize", "u:" + rectangle.x + " v:" +rectangle.br().y + " width " + rectangle.width + " h " + rectangle.height);
    	double f = getFocalLengthPixel(imageWidht); //1115;
    	//////
    	//f = 2550;
    	double cx = (imageWidht-1)/2;
    	double cy = (imageHeight-1)/2;
    			
    	//Log.i("CALIBRATION", "rMatrix.get(2, 2)[0]=" + rMatrix.get(2, 2)[0] + " rMatrix.get(0, 2)[0]="+rMatrix.get(0, 2)[0]);
    	double a = rMatrix.get(2, 2)[0] * rectangle.tl().x - rMatrix.get(2, 2)[0] *cx - f * rMatrix.get(0, 2)[0]; 
    	
    	Log.i("fFormula", "A components " +rMatrix.get(2, 2)[0] + "x" + rectangle.tl().x + " " +  rMatrix.get(2, 2)[0] *cx + " " +  f * rMatrix.get(0, 2)[0]);
    	//a : 775.777549633 -921.142506106 1563.54912905
    	double ww = rMatrix.get(2, 2)[0] * rectangle.br().y - rMatrix.get(2, 2)[0] * cy - f * rMatrix.get(1, 2)[0];
    	
    	double XXX_num = f * tVec.get(1, 0)[0]*a/ww - tVec.get(2, 0)[0]*rectangle.br().y*a/ww 
    			+ tVec.get(2, 0)[0]*cy*a/ww - f*tVec.get(0, 0)[0] + tVec.get(2, 0)[0]*rectangle.tl().x - tVec.get(2, 0)[0]*cx;
    	
    	double X = XXX_num / (f*rMatrix.get(0, 0)[0] - rMatrix.get(2, 0)[0]*rectangle.tl().x + rMatrix.get(2, 0)[0]*cx 
    			- f*rMatrix.get(1, 0)[0]*a/ww + rMatrix.get(2, 0)[0]*rectangle.br().y*a/ww - rMatrix.get(2, 0)[0]*cy*a/ww);
    	
    	double Z = (f*rMatrix.get(0,0)[0]*X + f*tVec.get(0,0)[0] - rMatrix.get(2,0)[0]*X*rectangle.tl().x + rMatrix.get(2,0)[0]*X*cx 
    				- tVec.get(2,0)[0]*rectangle.tl().x + tVec.get(2, 0)[0]*cx)
    				/ (rMatrix.get(2,2)[0]*rectangle.tl().x - rMatrix.get(2,2)[0]*cx - f*rMatrix.get(0,2)[0]);
    	
    	
    	Log.i("FFormula", "a: " + a + " ww: "+ ww + "XXX_: " + XXX_num + " Z " + Z + " X " +X);
    	Log.i("CALIBRATION", "Car at : " + Double.toString(X) + " / " + Double.toString(X));
    	return "Z="+Double.toString(Z) + " X="+Double.toString(X);
    }
    
    private float computeDistance(int rectHeight){
    	return mOpenCvCameraView.getFocalLength() * CAR_HEIGHT * mOpenCvCameraView.getCameraPreviewSize().height 
    			/ (rectHeight * SENSOR_HEIGHT) ;
    }
    
    private float computeDistanceMeth2(int rectHeight){
    	/*
    Given the real size of the traffic lights S(w; h) (in meters) it is trivial to estimate their position
    P (x; y; z) in the world space coordinate (the origin being the camera optical center). Assuming
    that the lens distortion is negligible there is a simple way to map the image space coordinates to
    the world coordinates using the intrinsic and extrinsic camera parameters. Here we need only the
    distance (Pz ) that may be computed with:

    Pz = (f × Sh) / (sh × ku)

    where f the focal (m), ku the pixel size (m), and s(w; h) the size in the image space Our settings are:
	f = 12 × 10−3 m, ku = 8.3 × 10−6 m
    	 */
    	return mOpenCvCameraView.getFocalLength() * CAR_HEIGHT / (rectHeight * 0.0083f); // 0.5f*(Math.PI/180) = 0.0083f 
    }
    
    private double getFocalLengthPixel(double imageWidht){
    	/*
    	 * translate focal length from mm to pixel
    	 * Formula: focal_pixel = (image_width_in_pixels * 0.5) / tan(FOV * 0.5 * PI/180)
    	 */
    	return (imageWidht*0.5f)/Math.tan(mOpenCvCameraView.getFOV()*0.5f*(Math.PI/180));
    }
    
    private void initSensors(){
    	mlocationListener = new MyLocator(textViewGPS);
    	// Initiate Sensor System
    	mSensorListener = new MySensorListener();
    	sm =(SensorManager) getSystemService(SENSOR_SERVICE);
    	List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ALL);
    	for (Sensor sensor : sensors){
    		Log.i(TAG, "SENSOR NAME = " + sensor.getName() + " TYPE = "+sensor.getType());
    	}
    	if (sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null){
    		  gyro = sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    	} else { Log.e(TAG, "NO default gyro sensor present");}
        if (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
        	accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        } else { Log.e(TAG, "NO default accelerometer sensor present");}
        if (sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null){
  		   magnetometer = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        } else { Log.e(TAG, "NO default magnetic field sensor present");}
        
    }
    
 /*   LocationListener locationListener = new LocationListener() {
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {
        	
        }
        public void onProviderDisabled(String provider) {
        	Toast.makeText(getApplicationContext(), "GPS disabled", Toast.LENGTH_LONG).show();
        }
		@Override
		public void onLocationChanged(Location location) {
			// Called when a new location is found by the network/GPS location provider.
			//StringBuilder locationInfo = new StringBuilder("TIME="+location.getTime() +" LAT="+location.getLatitude() + 
				//	" LON=" + location.getLongitude());
			Date t = new Date();
			StringBuilder locationInfo = new StringBuilder("TIME="+t +" LAT="+location.getLatitude() + 
					" LON=" + location.getLongitude());
			
			
			if (location.hasAccuracy()) locationInfo.append(" ACC="+location.getAccuracy());
			if (location.hasSpeed()) locationInfo.append(" SP="+location.getSpeed());
			Log.i("OpenCV", "LATITUDE="+location.getLatitude() + " LONGITUDE=" + location.getLongitude());
			textViewGPS.setText(locationInfo);
			
			m_position = location;
		}
		
		 public Point GetLatLongPoint() {
	    	  return new Point(m_position.getLatitude(), m_position.getLongitude());
	      }
	      
	      private Location m_position;

      };
*/    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
    	List<int[]> fpsRangeList = mOpenCvCameraView.getSupportedFPS();
    	for (int[] temp : fpsRangeList) {
    		Log.i(TAG, "fpsRange = [" + temp[0] + " - " + temp[1] + "]");
    	}
    	
        Log.i(TAG, "called onCreateOptionsMenu");
        mItemFace50 = menu.add("Car size 50%");
        mItemFace40 = menu.add("Car size 40%");
        mItemFace30 = menu.add("Car size 30%");
        mItemFace20 = menu.add("Car size 20%");
        mItemType   = menu.add(mDetectorName[mDetectorType]);
        return true;
        */
    	
    	// ---- MENU ITEM EFFECTS SUPPORTED ---- //
        List<String> effects = mOpenCvCameraView.getEffectList();
        if (effects == null) {
            Log.e(TAG, "Color effects are not supported by device!");
            return true;
        }
        mColorEffectsMenu = menu.addSubMenu("Color Effect");
        mEffectMenuItems = new MenuItem[effects.size()];
        int idx = 0;
        ListIterator<String> effectItr = effects.listIterator();
        while(effectItr.hasNext()) {
           String element = effectItr.next();
           mEffectMenuItems[idx] = mColorEffectsMenu.add(1, idx, Menu.NONE, element);
           idx++;
        }

        // ---- MENU ITEM RESOLUTION ---- //
        mResolutionMenu = menu.addSubMenu("Resolution");
        mResolutionList = mOpenCvCameraView.getResolutionList();
        mResolutionMenuItems = new MenuItem[mResolutionList.size()];
        ListIterator<Camera.Size> resolutionItr = mResolutionList.listIterator();
        idx = 0;
        while(resolutionItr.hasNext()) {
            Camera.Size element = resolutionItr.next();
            mResolutionMenuItems[idx] = mResolutionMenu.add(2, idx, Menu.NONE,
                    Integer.valueOf(element.width).toString() + "x" + Integer.valueOf(element.height).toString());
            idx++;
         }

        // ---- MENU ITEM FPS SUPPORTED ---- //
        List<Integer> fpsList = mOpenCvCameraView.getSupportedFrameRate();
        if (fpsList == null) {
            Log.e(TAG, "fps are not supported by device!");
            return true;}
        mFPSRangeMenu = menu.addSubMenu("Frame Rate");
        mFPSMenuItems = new MenuItem[fpsList.size()];
        Log.i(TAG, "fps rangeList size = " + fpsList.size());
        idx = 0;
        ListIterator<Integer> FPSItr = fpsList.listIterator();
        while(FPSItr.hasNext()) {
        	String element = FPSItr.next().toString();
        	Log.i(TAG, "fps frame rate = " + element);
        	mFPSMenuItems[idx] = mFPSRangeMenu.add(3, idx, Menu.NONE, element);
        	idx++;
        }

    	List<int[]> fpsRange = mOpenCvCameraView.getSupportedFPS();
    	for (int[] temp : fpsRange) {
    		Log.i(TAG, "fpsRange = [" + temp[0] + " - " + temp[1] + "]");
    	}
    	
    	//mOpenCvCameraView.setFrameRate(20);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*
    	Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item == mItemFace50)
            setMinFaceSize(0.5f);
        else if (item == mItemFace40)
            setMinFaceSize(0.4f);
        else if (item == mItemFace30)
            setMinFaceSize(0.3f);
        else if (item == mItemFace20)
            setMinFaceSize(0.2f);
        else if (item == mItemType) {
            int tmpDetectorType = (mDetectorType + 1) % mDetectorName.length;
            item.setTitle(mDetectorName[tmpDetectorType]);
            setDetectorType(tmpDetectorType);
        }
        return true;
        */
        Log.i(TAG, "called onOptionsItemSelected; selected item: " + item);
        if (item.getGroupId() == 1)
        {
            mOpenCvCameraView.setEffect((String) item.getTitle());
            Toast.makeText(this, mOpenCvCameraView.getEffect(), Toast.LENGTH_SHORT).show();
        }
        else if (item.getGroupId() == 2)
        {
            int id = item.getItemId();
            Camera.Size resolution = mResolutionList.get(id);
            mOpenCvCameraView.setResolution(resolution);
            resolution = mOpenCvCameraView.getResolution();
            String caption = Integer.valueOf(resolution.width).toString() + "x" + Integer.valueOf(resolution.height).toString();
            Toast.makeText(this, caption, Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /*private void setMinFaceSize(float faceSize) {
        mRelativeFaceSize = faceSize;
        mAbsoluteFaceSize = 0;
    }*/

    private void setDetectorType(int type) {
        if (mDetectorType != type) {
            mDetectorType = type;

            if (type == NATIVE_DETECTOR) {
                Log.i(TAG, "Detection Based Tracker enabled");
                //mNativeDetector.start();
            } else {
                Log.i(TAG, "Cascade detector enabled");
                //mNativeDetector.stop();
            }
        }
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.i(TAG, "On touch");
        storeNextFrame = true;
        return false;
    }
    
    
    //@todo
    private Point3 GetRealWorldCoordinate(Point3 p, Mat rotationByY/*float[] phoneAbsRotation*/, Point phonePosition){
    	Mat pointMatrix = new Mat(3, 1, CvType.CV_64FC1);
    	Mat result = new Mat(3, 1, CvType.CV_64FC1);

       	//Mat phoneMatrix = new Mat(3, 3, CvType.CV_64FC1);
    	//phoneMatrix.put(0, 0,phoneAbsRotation);

    	pointMatrix.put(0,0,p.x);
    	pointMatrix.put(1,0,p.y);
    	pointMatrix.put(2,0,p.z);

    	for (int row=0; row<3; row++){
    		result.put(row,0, 0);
    		for(int col=0; col <3; col++){
    			result.put(row, 0, result.get(row,0)[0] + rotationByY.get(row,col)[0]*pointMatrix.get(col,0)[0]);
    		}
    		//?result.put(row,0, result.get(row,0)[0]*-1);

    	}
    	//TODO is this correct? Should phonePosition be UTM or lat/long is good too
    	result.put(0,0,result.get(0, 0)[0]+phonePosition.x);
    	result.put(1,0,result.get(1, 0)[0]+0);
    	result.put(2,0,result.get(2, 0)[0]+phonePosition.y);
    	//http://stackoverflow.com/questions/2839533/adding-distance-to-a-gps-coordinate
    	//lat = lat0 + (180/pi)*(dy/6378137)
    	 //lon = lon0 + (180/pi)*(dx/6378137)/cos(lat0)
    	
    	Log.i(TAG, "Point3d : " + result.get(0, 0)[0] + " " + result.get(1, 0)[0] + " " +result.get(2, 0)[0] );
    	
    	Point3 realWorldPoint = new Point3(result.get(0, 0)[0],result.get(1, 0)[0],result.get(2, 0)[0]);
    	Log.i(TAG, "Point3d : " + p.toString() + " -> Real World: " + realWorldPoint.toString() + " Azimut " + mSensorListener.azimut);
    	
    	return realWorldPoint;
    }
    
    private double computeReprojectionErrors(List<Mat> objectPoints,
            List<Mat> rvecs, List<Mat> tvecs, Mat distortionCoefficientsP, Mat perViewErrors) {
        MatOfPoint2f cornersProjected = new MatOfPoint2f();
        double totalError = 0;
        double error;
        float viewErrors[] = new float[objectPoints.size()];

        MatOfDouble distortionCoefficients = new MatOfDouble(distortionCoefficientsP);
        int totalPoints = 0;
        for (int i = 0; i < objectPoints.size(); i++) {
            MatOfPoint3f points = new MatOfPoint3f(objectPoints.get(i));
            Calib3d.projectPoints(points, rvecs.get(i), tvecs.get(i),
            		cameraMatrix, distortionCoefficients, cornersProjected);
            error = Core.norm(mCornersBuffer.get(i), cornersProjected, Core.NORM_L2);

            int n = objectPoints.get(i).rows();
            viewErrors[i] = (float) Math.sqrt(error * error / n);
            totalError  += error * error;
            totalPoints += n;
        }
        perViewErrors.create(objectPoints.size(), 1, CvType.CV_32FC1);
        perViewErrors.put(0, 0, viewErrors);

        return Math.sqrt(totalError / totalPoints);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
        	String positionToLog = new String ("Locator Service, Latitude=" + String.valueOf(mLastLocation.getLatitude()
        			+ " Longitude=" + String.valueOf(mLastLocation.getLongitude())));
        	Log.i(LOCATION_TAG, positionToLog);
        	logger.info(positionToLog);
        	textViewGPS.setText(positionToLog);
        } else {
        	Log.e(LOCATION_TAG, "No last location available");
        }
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

	private void startLocationUpdates() {
		LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
        // This callback is important for handling errors that
        // may occur while attempting to connect with Google.
		Log.e(LOCATION_TAG, "NOT CONNECTED TO G SERVICES");
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (result.hasResolution()) {
            try {
                mResolvingError = true;
                result.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(result.getErrorCode());
            mResolvingError = true;
        }
	}

	/* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public void onDialogDismissed() {
        mResolvingError = false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() &&
                        !mGoogleApiClient.isConnected()) {
                	Log.i(LOCATION_TAG, "Trying to connect to G services again.");
                    mGoogleApiClient.connect();
                }
            }
        }
    }
 
	@Override
	public void onConnectionSuspended(int cause) {
		// This callback is important for handling errors that
        // may occur while attempting to connect with Google.
        //
		
	}

	@Override
	public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        StringBuilder locationInfo = new StringBuilder("Locator Service, Latitude="+mCurrentLocation.getLatitude() + 
        		" Longitude=" + mCurrentLocation.getLongitude());
        Log.i(LOCATION_TAG, locationInfo.toString());
        logger.info(locationInfo.toString());
        updateUI();
	}

    private void updateUI() {
    	StringBuilder locationInfo = new StringBuilder(mLastUpdateTime +" Latitude="+mCurrentLocation.getLatitude() + 
        		" Longitude=" + mCurrentLocation.getLongitude());
    	textViewGPS.setText(locationInfo.toString());
    }

	protected void stopLocationUpdates() {
	    LocationServices.FusedLocationApi.removeLocationUpdates(
	            mGoogleApiClient, this);
	}

	private boolean prepareVideoRecorder(){

		File loggerFolder = new File(Environment.getExternalStorageDirectory(), LOGGER_FOLDER);
    	String videoFile = new String(loggerFolder.getAbsolutePath() + "/" + experimentDate + ".mp4");

    	/*
        // record a video by intent
    	int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 200;
    	File video = new File(loggerFolder, experimentDate + ".mp4");
        Uri fileUri = Uri.fromFile(video);
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        //fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);  // create a file to save the video
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);  // set the image file name
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); // set the video image quality to high
        // start the Video Capture Intent
        startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
		*/
    	
	    recCamera = getCameraInstance();
//		try {
//			recCamera.setPreviewDisplay(null);
//		} catch (java.io.IOException ioe) {
//			Log.d(TAG, "IOException nullifying preview display: " + ioe.getMessage());
//		}
//		recCamera.stopPreview();
	    recCamera.unlock();
	    mMediaRecorder = new MediaRecorder();
	    mMediaRecorder.setCamera(recCamera);
	    
//	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//	    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//	    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);
//	    mMediaRecorder.setOutputFile(videoFile);
	    
//	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
//	    mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
//	    mMediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
//	    mMediaRecorder.setOutputFile(videoFile);
	    
	    mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
	    mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
	    mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
	    mMediaRecorder.setOutputFile(videoFile);
	    mMediaRecorder.setPreviewDisplay(null);
	    // Step 5: Set the preview output
	    // mMediaRecorder.setPreviewDisplay(mPreview.getHolder().getSurface());
	    
	    try {
	        mMediaRecorder.prepare();
	        Log.i(TAG, "MediaRecorder Prepare() OK");
	    } catch (IllegalStateException e) {
	        Log.e(TAG, "IllegalStateException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    } catch (IOException e) {
	        Log.e(TAG, "IOException preparing MediaRecorder: " + e.getMessage());
	        releaseMediaRecorder();
	        return false;
	    }
	    
	    return true;
	}
	
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
	    Camera c = null;
	    try {
	        c = Camera.open(); // attempt to get a Camera instance
	    }
	    catch (Exception e){
	    	Log.e(TAG, "Camera is not available (in use or does not exist)");
	    }
	    return c; // returns null if camera is unavailable
	}
	
	private void releaseMediaRecorder(){
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            recCamera.lock();           // lock camera for later use
        }
    }
	
    private void initRecorder() {

    	/*
    	File loggerFolder = new File(Environment.getExternalStorageDirectory(), LOGGER_FOLDER);
    	String videoFilePath = new String(loggerFolder.getAbsolutePath() + "/" + experimentDate + ".mp4");
    	File videoFile = new File(videoFilePath);
    	
        //int depth = com.googlecode.javacv.cpp.opencv_core.IPL_DEPTH_8U;
        //int channels = 4;
	if (isRecording) {
		// stop recording and release camera
		mMediaRecorder.stop();  // stop the recording
		releaseMediaRecorder(); // release the MediaRecorder object
		recCamera.lock();         // take camera access back from MediaRecorder
		Log.i("Recording", "Stop recording now !");
		isRecording = false;
	}

        // if (yuvIplimage == null) {
        // Recreated after frame size is set in surface change method
        // videoImage = IplImage.create(imageWidth, imageHeight, depth, channels);
        // Mat m = new Mat(rows, cols, type)
        //yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_32S, 2);
        videoImage = new Mat(1080, 1920, CvType.CV_8UC4);
        
        //Log.v(LOG_TAG, "IplImage.create");
        // }

        // File videoFile = new File(getExternalFilesDir(null), "VideoTest/images/video.mp4");
        // boolean mk = videoFile.getParentFile().mkdirs();
        // Log.v(LOG_TAG, "Mkdir: " + mk);

        // boolean del = videoFile.delete();
        // Log.v(LOG_TAG, "del: " + del);

        try {
            boolean created = videoFile.createNewFile();
            Log.i(TAG, "videoFile Created");
        } catch (IOException e) {
            Log.i(TAG, "videoFile not created");
            e.printStackTrace();
        }

        ffmpeg_link = videoFile.getAbsolutePath();
        recorder = new FFmpegFrameRecorder(ffmpeg_link, 1920, 1080, 0);
        //Log.v(TAG, "FFmpegFrameRecorder: " + ffmpeg_link + " imageWidth: " + mOpenCvCameraView.getWidth() + 
        //		" imageHeight " + mOpenCvCameraView.getHeight());

        recorder.setFormat("mp4");
        Log.v(TAG, "recorder.setFormat(\"mp4\")");

        //recorder.setSampleRate(sampleAudioRateInHz);
        //Log.v(TAG, "recorder.setSampleRate(sampleAudioRateInHz)");

        // re-set in the surface changed method as well
        recorder.setFrameRate(frameRate);
        Log.v(TAG, "recorder.setFrameRate(frameRate)");

        // Create audio recording thread
        // audioRecordRunnable = new AudioRecordRunnable();
        // audioThread = new Thread(audioRecordRunnable);
        */
    }

}




/**


 positions[0+cn*counter] = 0;
        positions[1+cn*counter] = PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = 0;
        counter++;
        positions[0+cn*counter] = PARKING_SIGNAL_WIDTH;
        positions[1+cn*counter] = PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = 0;
        counter++;
        positions[0+cn*counter] = 0;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT+PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = 0;
        counter++; 
        positions[0+cn*counter] = PARKING_SIGNAL_WIDTH;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT+PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = 0;
        counter++;
        positions[0+cn*counter] = PARKING_SIGNAL_WIDTH/2;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT+PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = 0;
        counter++;
        positions[0+cn*counter] = PARKING_SIGNAL_WIDTH/2;
        positions[1+cn*counter] = PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = 0;
        counter++;
        positions[0+cn*counter] = PARKING_SIGNAL_WIDTH/2;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT/2 +PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = 0;
        counter++;
        positions[0+cn*counter] = PARKING_SIGNAL_WIDTH;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT/2 +PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = 0;
        counter++;
        positions[0+cn*counter] = 0;
        positions[1+cn*counter] = PARKING_SIGNAL_HEIGHT/2 +PARKING_SIGNAL_ELEVATION;
        positions[2+cn*counter] = 0;
        
        	imagePointsCalibration.clear(); //TODO remove it if we go back for our calibration
    	imagePointsCalibration.add(new Point(rectangle.tl().x, (imageHeight-rectangle.br().y)));
    	imagePointsCalibration.add(new Point(rectangle.br().x, (imageHeight-rectangle.br().y)));
    	imagePointsCalibration.add(new Point(rectangle.tl().x, (imageHeight-rectangle.tl().y)));
    	imagePointsCalibration.add(new Point(rectangle.br().x, (imageHeight-rectangle.tl().y)));
    	
    	
    	
    	imagePointsCalibration.add(new Point(rectangle.br().x - rectangle.width/2, imageHeight-rectangle.tl().y));
    	imagePointsCalibration.add(new Point(rectangle.br().x - rectangle.width/2, imageHeight-rectangle.br().y));
    	imagePointsCalibration.add(new Point(rectangle.br().x - rectangle.width/2, imageHeight-(rectangle.br().y-rectangle.height/2)));
    	imagePointsCalibration.add(new Point(rectangle.br().x, imageHeight-(rectangle.br().y-rectangle.height/2)));
    	imagePointsCalibration.add(new Point(rectangle.tl().x, imageHeight-(rectangle.br().y-rectangle.height/2)));

    	
        
*/