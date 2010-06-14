package com.sa.mwa;

interface INotifyValueChanged 
{
	void temperatureChanged(double value, double humidity);
	void connectionEstablished();
	void connectionFailed();
	void connectionProcessing();
	void disconnected();
	void queryResultReceived();
	void queryReceived(String content);
	void exceptionOccured(String message);
}
