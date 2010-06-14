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
	float findWeather(String destination);
	void settings(String deviceName, int refreshRate);
	double retrieveLongitude();
	double retrieveLatitude();
}
