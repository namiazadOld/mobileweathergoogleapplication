package com.sa.mwa;

import android.app.Dialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Configuration extends Dialog{

	private Main mwaClient;
	private Button btn_save;
	private Button btn_cancel;
	private EditText edt_deviceName;
	private EditText edt_refreshRate;
	private PeerServiceConnector peerServiceConnector;
	
	public Configuration(Main mwaClient) {
		super(mwaClient);
		this.mwaClient = mwaClient;
	}

	private void uiElementInitializing()
	{

		btn_save = (Button) findViewById(R.id.btn_save);
		btn_save.setOnClickListener(btn_save_onClick);
		
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(btn_cancel_onClick);
		
		edt_deviceName = (EditText) findViewById(R.id.edt_deviceName);
		edt_refreshRate = (EditText) findViewById(R.id.edt_refreshRate);
		
		
		edt_deviceName.setText(EnvironmentVariables.getDeviceName(mwaClient));
		edt_refreshRate.setText(Integer.toString(EnvironmentVariables.getRefreshRate(mwaClient)));
	}
	
	public void Prepare(PeerServiceConnector connector)
	{
		this.peerServiceConnector = connector;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.configuration);
		uiElementInitializing();
	}
	
	private Button.OnClickListener btn_save_onClick = new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			
			try {
				peerServiceConnector.getRemoteService().settings(edt_deviceName.getText().toString(), 
						Integer.parseInt(edt_refreshRate.getText().toString()));
				dismiss();
			} catch (NumberFormatException e) {
				Toast.makeText(mwaClient, e.getMessage(), Toast.LENGTH_LONG).show();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			
		}
		
	};
	
	private Button.OnClickListener btn_cancel_onClick = new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			
			dismiss();
		}
		
	};
}
