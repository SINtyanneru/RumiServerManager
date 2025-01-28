package su.rumishistem.rumiservermanager.TYPE;

import com.rumisystem.rumi_java_lib.Socket.Server.CONNECT_EVENT.CONNECT_EVENT;

public class SERVER_INFO {
	private String ID = null;
	private String IP = null;
	private String NAME = null;
	private String NOTE = null;
	private CONNECT_EVENT SESSION = null;

	public SERVER_INFO(String ID, String IP, String NAME, String NOTE) {
		this.ID = ID;
		this.IP = IP;
		this.NAME = NAME;
		this.NOTE = NOTE;
	}

	public String getID() {
		return ID;
	}

	public String getIP() {
		return IP;
	}

	public String getNAME() {
		return NAME;
	}

	public String getNOTE() {
		return NOTE;
	}

	public CONNECT_EVENT getSESSION() {
		return SESSION;
	}

	public void setSESSION(CONNECT_EVENT SESSION) {
		this.SESSION = SESSION;
	}

	public boolean getSTATUS() {
		if (SESSION != null) {
			return true;
		} else {
			return false;
		}
	}
}
