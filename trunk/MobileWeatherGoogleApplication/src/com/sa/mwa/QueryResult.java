package com.sa.mwa;

public class QueryResult {

	private double min;
	private double max;
	private double average;
	private String deviceName;
	private String username;
	
	public QueryResult(double min, double max, double average, String deviceName, String username)
	{
		this.min = min;
		this.max = max;
		this.average = average;
		this.deviceName = deviceName;
		this.username = username;
	}
	
	public double getMin() {
		return min;
	}
	
	public double getMax() {
		return max;
	}
	
	public double getAverage() {
		return average;
	}
	
	public String getDeviceName() {
		return deviceName;
	}
	
	public String getUsername() {
		return username;
	}
}
