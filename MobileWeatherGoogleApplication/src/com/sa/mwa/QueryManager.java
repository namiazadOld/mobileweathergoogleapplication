package com.sa.mwa;

import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.MessageTypeFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import android.os.Handler;

public class QueryManager {

	private XMPPConnection connection;
	private Handler handler;
	
	public QueryManager(Handler handler)
	{
		this.handler = handler;
	}
	
	public void connectToChatServer()
	{
		Thread thread = new Thread(new Runnable() {
			
			@Override
			public void run() {

				try 
				{
					
					ConnectionConfiguration config = new ConnectionConfiguration("jabber.org", 5222, "jabber.org");
					connection = new XMPPConnection(config);
					connection.connect();
					
					connection.login("all_mwa_users", "Intermilan1");
					
					handler.sendMessage(handler.obtainMessage(PeerService.CONNECTION_TO_CHAT_SERVER_ESTABLISHED));

					PacketFilter filter = new MessageTypeFilter(Message.Type.chat);
					connection.addPacketListener(new PacketListener() {
						
						@Override
						public void processPacket(Packet packet) {
								Message message = (Message)packet;
								handler.sendMessage(handler.obtainMessage(PeerService.QUERY_MESSAGE, message.getBody().toString()));
						}
					}, filter);					
					
				} 
				catch (XMPPException e) 
				{
					handler.sendMessage(handler.obtainMessage(PeerService.CONNECTION_TO_CHAT_SERVER_FAILED));
				}
			}
		});
		
		thread.start();
	}
}
