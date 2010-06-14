package com.sa.mwa;

import android.app.Dialog;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends Dialog {

	private Button btn_connect;
	private Button btn_cancel;
	private EditText edt_username;
	private EditText edt_password;
	private PeerServiceConnector peerServiceConnector;
	
	public Login(Main mwaClient) {
		super(mwaClient);
	}
	
	public String getUsername()
	{
		return edt_username.getText().toString();
	}
	
	public String getPassword()
	{
		return edt_password.getText().toString();
	}
	
	public void Prepare(PeerServiceConnector connector)
	{
		this.peerServiceConnector = connector;
	}
	
	private void uiElementInitializing()
	{
		btn_connect = (Button) findViewById(R.id.btn_connect);
		btn_connect.setOnClickListener(btn_login_onClick);
		
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
		btn_cancel.setOnClickListener(btn_cancel_onClick);
		
		edt_username = (EditText) findViewById(R.id.edt_username);
		edt_password = (EditText) findViewById(R.id.edt_password);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		uiElementInitializing();
	}
	
	private Button.OnClickListener btn_login_onClick = new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			
			Thread thread = new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						peerServiceConnector.getRemoteService().establishConnection(
								getUsername(), getPassword());
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
			});
			
			thread.start();
			dismiss();
		}
		
	};
	
	private Button.OnClickListener btn_cancel_onClick = new Button.OnClickListener(){

		@Override
		public void onClick(View v) {
			
			dismiss();
		}
		
	};

}
