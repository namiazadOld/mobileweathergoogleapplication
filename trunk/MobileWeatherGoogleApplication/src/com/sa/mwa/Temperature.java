package com.sa.mwa;

public class Temperature 
{
	public Temperature(double value, double humidity)
	{
		this.value = value;
		this.humidity = humidity;
	}
	
	private double value;
	private double humidity;
	
	public double getValue() {
		return value;
	}
	
	public double getHumidity() {
		return humidity;
	}
}
