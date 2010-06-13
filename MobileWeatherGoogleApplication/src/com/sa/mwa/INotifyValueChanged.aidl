package com.sa.mwa;

interface INotifyValueChanged 
{
	void temperatureChanged(double value, double humidity);
	void connectionEstablished();
	void connectionFailed();
}
