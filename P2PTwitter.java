import java.io.*;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

public class P2PTwitter {

	public static void main (String[] args) {
		try {
			ArrayList<Peer> peers = new ArrayList<Peer>();
			int i = 0;
			String key = null;
			String ip = null;
			String pseudo = null;
			String unikey = null;
			String port = null;
			
			File file = new File("participants.properties");
			FileInputStream fileInput = new FileInputStream(file);
			Properties properties = new Properties();
			properties.load(fileInput);
			fileInput.close();
		
			String participants = properties.getProperty("participants");
			String[] participant = participants.split(",");
		
			for(i=1; i<=participant.length; i++) {
				key = "peer" + i;
				ip = properties.getProperty(key+".ip");
				pseudo = properties.getProperty(key+".pseudo");
				unikey = properties.getProperty(key+".unikey");
				port = properties.getProperty(key+".port");
				peers.add(new Peer(ip, pseudo, unikey, port));	
				
			}
			
			String myUniKey = args[0];
			Peer myself = null;
			for(Peer p : peers) {
				if(p.getUniKey().equals(myUniKey)) {
					myself = p;
					break;
				}
			}
			
			InetAddress addr = InetAddress.getByName(myself.getIP());
			DatagramSocket sock = new DatagramSocket(myself.getPort(), addr);
			Thread myClient = new Thread(new P2PClient(myself, peers, sock));
			Thread myServer = new Thread(new P2PServer(myself, peers, sock));
			myClient.start();
			myServer.start();
				
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
}