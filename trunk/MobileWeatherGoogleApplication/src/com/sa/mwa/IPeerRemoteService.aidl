package com.sa.mwa;

import com.sa.mwa.INotifyValueChanged;

interface IPeerRemoteService 
{
	float retrieveTemparature();
	float retrieveHumidity();
	void registerCallBack(INotifyValueChanged ntc);
	void unregisterCallBack(INotifyValueChanged ntc);
	void establishConnection(String username, String password);
	void disconnect();
	void findWeather(double longitude, double latitude, int duration, int radius);
	void settings(String deviceName, int refreshRate);
	double retrieveLongitude();
	double retrieveLatitude();
}
