package com.sa.mwa;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.http.util.ByteArrayBuffer;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class QueryManager {

	private XMPPConnection broadcastConnection;
	private XMPPConnection connection;
	private Handler handler;
	private final String broadCastUsername = "all_mwa_users";
	private final String broadCastPassword = "Intermilan1";
	private final String chatDomain = "jabber.org";
	private String username;
	
	public QueryManager(Handler handler)
	{
		this.handler = handler;
	}
	
	private Double Min(List<Double> list)
	{
		double min = list.get(0);
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i) < min)
				min = list.get(i);
		}
		return min;	
	}
	
	private Double Max(List<Double> list)
	{
		double max = list.get(0);
		for (int i = 0; i < list.size(); i++)
		{
			if (list.get(i) > max)
				max = list.get(i);
		}
		return max;
	}
	
	private Double Average(List<Double> list)
	{
		double sum = 0;
		
		for (int i = 0; i < list.size(); i++)
		{
			sum += list.get(i);
		}
		
		return sum / list.size();
	}
	
	public void analyzeQuery(Context context, String query)
	{
		String[] parts = query.split("\\-");
		
		String username = parts[0];
		double longitude = Double.parseDouble(parts[1]);
		double latitude = Double.parseDouble(parts[2]);
		int duration = Integer.parseInt(parts[3]);
		int radius = Integer.parseInt(parts[4]);
		
		Log.d("QUERY_COORDINATE", latitude + "--" + longitude);
		
		Calendar calendar = new GregorianCalendar();
		int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
		
		List<Double> results = new ArrayList<Double>();
		
		try {
			FileInputStream fis = context.openFileInput(LogNotifyValueChanged.FILENAME);
			BufferedInputStream bis = new BufferedInputStream(fis);
            
            /* Read bytes to the Buffer until
             * there is nothing more to read(-1). */
            ByteArrayBuffer baf = new ByteArrayBuffer(50);
            int current = 0;
            while((current = bis.read()) != -1){
                    baf.append((byte)current);
            }

            /* Convert the Bytes read to a String. */
            String content = new String(baf.toByteArray());
			
			
            String[] lines = content.split("\n");
            for (int i = 0; i < lines.length; i++)
            {
            	String line = lines[i];
            	Log.d("Line" + i, line);
            	
            	String[] logParts = line.split("\\-");
            	
            	int hour = Integer.parseInt(logParts[2]);
            	if (currentHour - duration >= hour)
            		continue;
            	
            	double lon = Double.parseDouble(logParts[0]);
            	double lat = Double.parseDouble(logParts[1]);
            	            	
            	if (GeopointDistance.IsResult(lat, lon, latitude, longitude, radius))
            	{
            		double temp = Double.parseDouble(logParts[3]);
            		results.add(temp);
            		Log.d("TEMPERATURE_RESULT", Double.toString(temp));
            	}
            	
            }
            
            fis.close();
            
            if (results.size() == 0)
            {
            	handler.sendMessage(handler.obtainMessage(PeerService.QUERY_ANALYZED));
            	return;
            }
			
			
			handler.sendMessage(handler.obtainMessage(
					PeerService.QUERY_ANALYZED, new QueryResult(Min(results),
							Max(results), Average(results),
							EnvironmentVariables.getDeviceName(context),
							username)));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void connectToChatServer(final String username, final String password)
	{
		this.username = username;
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {

				try 
				{
					
					handler.sendMessage(handler.obtainMessage(PeerService.CONNECTION_TO_CHAT_SERVER_PROCESSING));
					
					ConnectionConfiguration config = new ConnectionConfiguration(chatDomain, 5222, chatDomain);
					
					connection = new XMPPConnection(config);
					connection.connect();
					connection.login(username, password);
					
					broadcastConnection = new XMPPConnection(config);
					broadcastConnection.connect();
					broadcastConnection.login(broadCastUsername, broadCastPassword);
					
					handler.sendMessage(handler.obtainMessage(PeerService.CONNECTION_TO_CHAT_SERVER_ESTABLISHED));
					
					
					
					
					PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
					connection.addPacketListener(new PacketListener() {
						
						@Override
						public void processPacket(Packet packet) {
								Message message = (Message)packet;
								handler.sendMessage(handler.obtainMessage(PeerService.QUERY_RESULT, message.getBody()));
						}
					}, filter);					
					
					
					PacketFilter broadCastFilter = new MessageTypeFilter(Message.Type.chat);
					broadcastConnection.addPacketListener(new PacketListener() {
						
						@Override
						public void processPacket(Packet packet) {
								Message message = (Message)packet;
								handler.sendMessage(handler.obtainMessage(PeerService.QUERY_MESSAGE, message.getBody()));
								handler.sendMessage(handler.obtainMessage(PeerService.QUERY_PROCESSING, message.getBody()));
								
						}
					}, broadCastFilter);
					
				} 
				catch (XMPPException e) 
				{
					handler.sendMessage(handler.obtainMessage(PeerService.CONNECTION_TO_CHAT_SERVER_FAILED));
				}
			}
		});
		
		thread.start();
		
		
	}
	
	public void disconnectFromChatServer()
	{
		this.username = null;
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {

				connection.disconnect();
				broadcastConnection.disconnect();
				handler.sendMessage(handler.obtainMessage(PeerService.CONNECTION_TO_CHAT_SERVER_DISCONNECTED));
			}
		});
		
		thread.start();
	}
	
	public void findWeather(double longitude, double latitude, int duration, int radius) throws CustomException
	{
		if (connection == null || broadcastConnection == null || !connection.isConnected() || !broadcastConnection.isConnected())
			throw new CustomException(CustomException.CONNECTION_FAILED);
		
		if (this.username == null)
			throw new CustomException(CustomException.CONNECTION_FAILED);
		
		final String content = (username + "-" + longitude + "-" + latitude + "-" + duration + "-" + radius).toString();
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message msg = new Message(broadCastUsername + "@" + chatDomain, Message.Type.chat);
				msg.setBody(content);
				broadcastConnection.sendPacket(msg);
			}
		});
		thread.start();
	}
	
	public void SendResult(final QueryResult result)
	{
		
		final String content = result.getDeviceName() + "-" + result.getMin()
				+ "-" + result.getMax() + "-" + result.getAverage();
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {
				Message msg = new Message(result.getUsername() + "@" + chatDomain, Message.Type.chat);
				Log.d("USERNAME", result.getUsername());
				msg.setBody(content);
				connection.sendPacket(msg);
			}
		});
		thread.start();
	}
}
