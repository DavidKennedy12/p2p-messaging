public class Peer {
	private String ip;
	private String pseudo;
	private String unikey;
	private int port;
	private State state;
	private Long startTime;
	private String tweet;
	private int seqNum;
	
	public Peer(String ip, String pseudo, String unikey, String port) {
		this.ip = ip;
		this.pseudo = pseudo;
		this.unikey = unikey;
		this.port = getNumber(port);
		this.state = State.uninitialized;
		this.startTime = System.currentTimeMillis();
		this.tweet = null;
		this.seqNum = 0;
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
	
	public void print()
	{	
		if(state == State.uninitialized) {
			System.out.println("# [" + pseudo + " (" + unikey + "): not yet initialized]");
		}
		
		else if(state == State.idle) {
			System.out.println("# [" + pseudo + " (" + unikey + "): " + "idle]");
		}
		
		else if(state == State.active) {
			System.out.println("# " + pseudo + " (" + unikey + "): " + tweet);		}
		
	}
	
	
	public String getPseudo() {
		return pseudo;
	}
	
	public String getUniKey() {
		return unikey;
	}
	
	public String getIP () {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	public State getState() {
		return state;
	}
	
	public void updateState() {
		if(System.currentTimeMillis() > (startTime + 20000)) {
			state = State.stale;
		}
		
		else if(System.currentTimeMillis() > (startTime + 10000)) {
			state = State.idle;
		}
	}
	
	public void newTweet(String tweet, int newNum) {
		this.tweet = tweet;
		this.startTime = System.currentTimeMillis();
		this.state = State.active;
		this.seqNum = newNum;
	}
	
	public int getSeqNum() {
		return seqNum;
	}
	public void incrementSeqNum() {
		seqNum++;
	}

	public String getTweet() {
		return tweet;
	}
	
	public void setState(State state) {
		this.state = state;
	}
	
}

