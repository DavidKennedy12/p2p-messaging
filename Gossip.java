import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Random;

public class Gossip implements Runnable{
	private DatagramSocket sock;
	private Peer myself;
	private ArrayList<Peer> peers;
	
	public Gossip(DatagramSocket sock, Peer myself, ArrayList<Peer> peers) {
		this.sock = sock;
		this.myself = myself;
		this.peers =peers;
	}
	
	public void run() {	
		Random ran = new Random();
		long interval;
		try {
			while(true) {
				interval = ran.nextInt(2000) + 1000;
				Thread.sleep(interval);
				gossipNow();
			}
		}
		catch(Exception e) {	
		}
		
	}
	
	private void gossipNow() {
		try {
			String formatted = convertStatus(myself.getTweet());
			String msg = myself.getUniKey() + ":" + formatted +":" + myself.getSeqNum();
			myself.incrementSeqNum();
			byte[] buffer = msg.getBytes();
			InetAddress ip = null;
			DatagramPacket message;
			for(Peer p : peers) {
				if(p != myself) {
					ip = InetAddress.getByName(p.getIP());
					message = new DatagramPacket(buffer, buffer.length, ip,  p.getPort());
					sock.send(message);
				}
			}
		}
		catch(Exception e) {	
		}		
	}
	

	private String convertStatus(String status) {	
		String[] message = status.split(":");
		String tweet = message[0];
		for(int i=1; i<=message.length - 1; i++) {
			tweet += "\\:" + message[i];
		}
		return tweet;
	}

}

