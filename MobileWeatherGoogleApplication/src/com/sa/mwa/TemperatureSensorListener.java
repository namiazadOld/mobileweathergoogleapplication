package com.sa.mwa;

import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;
import android.content.Context;
import android.hardware.SensorListener;
import android.hardware.SensorManager;

public class TemperatureSensorListener implements SensorListener{

	private SensorManagerSimulator sensorManager;
	private float currentTemperature;
	
	@Override
	public void onAccuracyChanged(int sensor, int accuracy) {
		
	}

	@Override
	public void onSensorChanged(int sensor, float[] values) {

		if (sensor == SensorManager.SENSOR_TEMPERATURE)
			currentTemperature = values[0];
	}

	public void connect(Context context, String sensorService)
	{
		sensorManager = SensorManagerSimulator.getSystemService(context, sensorService);
		sensorManager.connectSimulator();
	}
	
	public float getCurrentTemperature() {
		return currentTemperature;
	}
	
	public void registerListener(int sensors, int rate)
	{
		sensorManager.registerListener(this, SensorManager.SENSOR_TEMPERATURE, SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	public void unregisterListener()
	{
		sensorManager.unregisterListener(this);
	}
}
