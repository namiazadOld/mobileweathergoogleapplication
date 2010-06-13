package com.sa.mwa;

import java.util.List;
import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class PeerServiceConnector implements ServiceConnection {

	
	private IPeerRemoteService remoteService;
	List<INotifyValueChanged> registeredServices;
	
	public IPeerRemoteService getRemoteService()
	{
		return remoteService;
	}
	
	public PeerServiceConnector(List<INotifyValueChanged> registeredServices)
	{
		this.registeredServices = registeredServices;
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		remoteService = IPeerRemoteService.Stub.asInterface(service);
		try 
		{
			for (INotifyValueChanged notifyValueChanged : registeredServices)
				remoteService.registerCallBack(notifyValueChanged);
        } 
		catch (RemoteException e) 
        {
			
        }
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		try 
		{
			for (INotifyValueChanged notifyValueChanged : registeredServices)
				remoteService.unregisterCallBack(notifyValueChanged);
        } 
		catch (RemoteException e) 
        {
			
        }
	}
}
