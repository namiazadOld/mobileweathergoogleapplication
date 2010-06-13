package com.sa.mwa;

import com.sa.mwa.INotifyValueChanged;

interface IPeerRemoteService 
{
	float retrieveTemparature();
	float retrieveHumidity();
	void registerCallBack(INotifyValueChanged ntc);
	void unregisterCallBack(INotifyValueChanged ntc);
}
