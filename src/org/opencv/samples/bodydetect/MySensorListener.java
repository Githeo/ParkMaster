package org.opencv.samples.bodydetect;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.util.Log;

public class MySensorListener implements SensorEventListener {
	float[] mGravity;
	float[] mGeomagnetic;
	float azimut, pitch, roll;
	float[] phoneRotationMatrix;
	private final String TAG = "OpenCVTest";
	private static final float NS2S = 1.0f / 1000000000.0f; // Create a constant to convert nanoseconds to seconds.
	private final float[] deltaRotationVector = new float[4];
	private float timestamp;
	private float EPSILON = 5f;

	
	private boolean mValidData; //true if at least one value has been got from the sensors
	
	//only for testing
	//private int counter = 0;
	
	public void onAccuracyChanged(Sensor sensor, int accurancy) {
		//...
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		/*counter++;
		if(counter < 10){
			return;
		}
		counter = 0;*/
		mValidData = true;
		if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
			mGravity = event.values.clone();
			FdActivity.logger.info("ACCELEROMETER X="+event.values[0] + " Y="+ event.values[1] + " Z="+event.values[2]);
		}
		if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
			mGeomagnetic = event.values.clone();
			FdActivity.logger.info("MAGNETOMETER [microT] X="+event.values[0] + " Y="+ event.values[1] + " Z="+event.values[2]);
		}
		if (mGravity != null && mGeomagnetic != null) {
			float R[] = new float[9];
			float I[] = new float[9];
			boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
			if (success) {
				float orientation[] = new float[3];
				SensorManager.getOrientation(R, orientation);
				azimut =  57.29578F * orientation[0]; // degree = radiant * 180 / PI = 57.29578F
				pitch =  57.29578F * orientation[1];
				roll =  57.29578F * orientation[2];
				phoneRotationMatrix = R;
				Log.i(TAG, event.timestamp + " Azimut="+azimut + " Pitch="+ pitch + " Roll=" + roll);
				/*Log.i(TAG, "Rotation matrix " + phoneRotationMatrix[0]+" "+phoneRotationMatrix[1]+" "+phoneRotationMatrix[2]+
						"//"+phoneRotationMatrix[3]+" "+phoneRotationMatrix[4]+" "+phoneRotationMatrix[5]+
						"//"+phoneRotationMatrix[6]+" "+phoneRotationMatrix[7]+" "+phoneRotationMatrix[8]);
				 */
				FdActivity.logger.info("Azimut="+azimut + " Pitch="+ pitch + " Roll=" + roll);
			}
		}
		if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE){
			// This timestep's delta rotation to be multiplied by the current rotation
			// after computing it from the gyro sample data.
			if (timestamp != 0) {
				final float dT = (event.timestamp - timestamp) * NS2S;
				// Axis of the rotation sample, not normalized yet.
				float axisX = event.values[0];
				float axisY = event.values[1];
				float axisZ = event.values[2];

				// Calculate the angular speed of the sample
				float omegaMagnitude = FloatMath.sqrt(axisX*axisX + axisY*axisY + axisZ*axisZ);

				// Normalize the rotation vector if it's big enough to get the axis
				// (that is, EPSILON should represent your maximum allowable margin of error)
				if (omegaMagnitude > EPSILON) {
					axisX /= omegaMagnitude;
					axisY /= omegaMagnitude;
					axisZ /= omegaMagnitude;
				}

				// Integrate around this axis with the angular speed by the timestep
				// in order to get a delta rotation from this sample over the timestep
				// We will convert this axis-angle representation of the delta rotation
				// into a quaternion before turning it into the rotation matrix.
				float thetaOverTwo = omegaMagnitude * dT / 2.0f;
				float sinThetaOverTwo = FloatMath.sin(thetaOverTwo);
				float cosThetaOverTwo = FloatMath.cos(thetaOverTwo);
				deltaRotationVector[0] = sinThetaOverTwo * axisX;
				deltaRotationVector[1] = sinThetaOverTwo * axisY;
				deltaRotationVector[2] = sinThetaOverTwo * axisZ;
				deltaRotationVector[3] = cosThetaOverTwo;
			}
			timestamp = event.timestamp;
			float[] deltaRotationMatrix = new float[9];
			SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
			// User code should concatenate the delta rotation we computed with the current rotation
			// in order to get the updated rotation.
			// rotationCurrent = rotationCurrent * deltaRotationMatrix;
			Log.i(TAG, "GYRO: " + deltaRotationVector[0] + " " + deltaRotationVector[1] 
					+ " " + deltaRotationVector[2] + " " + deltaRotationVector[3]);
			FdActivity.logger.info("GYRO (Delta): " + deltaRotationVector[0] + " " + deltaRotationVector[1] 
					+ " " + deltaRotationVector[2] + " " + deltaRotationVector[3]);
		}
	}

	public boolean HasValidData(){
		return mValidData;
	}
	
	private String dumpRMatrix(float R []){	
		/*
 		 * Print an input 3x3 matrix
 		 */
 		StringBuffer dump = new StringBuffer();
 		for (int i=0; i<3; i++){
 			int j = i*3;
 			for (int z=j; z<j+3; z++)
 				dump.append(R[j] + " ");
 			dump.append("\n");
 		}
 		return dump.toString();
 	}
}
