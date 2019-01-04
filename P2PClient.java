import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Scanner;

public class P2PClient implements Runnable {

	private Peer myself;
	private ArrayList<Peer> peers;
	private DatagramSocket sock;
	
	public P2PClient(Peer myself, ArrayList<Peer> peers, DatagramSocket sock) {
		this.myself = myself;
		this.peers = peers;
		this.sock = sock;
	}
	
	@Override
	public void run() {
		Scanner sc = new Scanner(System.in);
		String status = null;
		boolean started = false;;
		while(true) {
			if(myself.getState() == State.active && !started) {
				Thread gossip = new Thread(new Gossip(sock, myself, peers));
				gossip.start();
				started = true;
			}
			
			System.out.print("Status: ");
			
			status = sc.nextLine();
			if(status.length() <= 140 && status.length() > 0) {
				send(status);
				myself.setState(State.active);
				System.out.println("### P2P tweets ###");
				System.out.println("# " + myself.getPseudo() + " (myself) : " + myself.getTweet());
				updatePeers();
				for(Peer p :peers) {
					if(p != myself ) {
						p.print();
					}	
				}
				System.out.println("### End tweets ###\n");
			}
			else if(status.length() > 140){
				System.out.println("Status is too long, 140 characters max. Retry.\n");
			}
			else {
				System.out.println("Status is empty. Retry.\n");
			}
		}
	}
	
	private void send(String status) {
		try {
			myself.newTweet(status, myself.getSeqNum() +1 );
			String formatted = convertStatus(status);
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
	private void updatePeers() {
		for(Peer p : peers) {
			if(p != myself) {
				p.updateState();
			}
		}
	}

}

