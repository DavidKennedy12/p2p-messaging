import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

public class P2PServer implements Runnable {
	private ArrayList<Peer> peers;
	private DatagramSocket sock;
	private Peer myself;
	public P2PServer(Peer myself, ArrayList<Peer> peers, DatagramSocket sock) {
		this.peers = peers;
		this.sock = sock;
		this.myself = myself;
	}

	public void run() {
		try{
			byte[] buffer = new byte[1048];
			DatagramPacket msg;
			
			while(true) {
				msg = new DatagramPacket(buffer, buffer.length);
				sock.receive(msg);
				String received = new String(msg.getData(), 0, msg.getLength(), "ISO-8859-1");//"ISO-8859-1");
				String[] message = received.split("(?<!\\\\):");
				int seqNum = -1;
				if(message.length == 3) {
					seqNum = getNumber(message[2]);
				}
				message[1] = message[1].replace("\\:", ":");
				for(Peer p: peers) {
					if(p.getUniKey().equals(message[0])) {
						if(seqNum == -1) {
							p.newTweet(message[1], p.getSeqNum());
						}
						else if(seqNum == 0) {
							p.newTweet(message[1], 0);
						}
						else if( seqNum >= p.getSeqNum()){
							p.newTweet(message[1], seqNum);
						}
						break;
					}
				}
			}
		}
		catch (Exception e){	
		}
	}
	
	private static int getNumber(String num) {
		int value = 0;
		int i = 0;
		for(i = 0; i<num.length(); i++) {
			value *= 10;
			value += (num.charAt(i) - 48);
		}
		return value;
	}
}

