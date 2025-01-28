package su.rumishistem.rumiservermanager.PARENT;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiservermanager.Main.CONFIG_DATA;
import static su.rumishistem.rumiservermanager.Main.EXP;
import static su.rumishistem.rumiservermanager.Main.VERSION;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rumisystem.rumi_java_lib.HASH;
import com.rumisystem.rumi_java_lib.HASH.HASH_TYPE;

import su.rumishistem.rumiservermanager.TYPE.SERVER_INFO;

public class ParentMain {
	public ParentMain THIS = this;
	public List<SERVER_INFO> SERVER_LIST = new ArrayList<SERVER_INFO>();
	public HashMap<String, String> USER_LIST = new HashMap<String, String>();

	public void Main() throws IOException, NoSuchAlgorithmException {
		//Server.jsonを読み込む
		File SERVER_LIST_FILE = new File(CONFIG_DATA.get("RSM").asString("SERVER_LIST"));
		if (SERVER_LIST_FILE.exists()) {
			//ロード
			JsonNode SERVER_JSON = new ObjectMapper().readTree(SERVER_LIST_FILE);
			for (int I = 0; I < SERVER_JSON.size(); I++) {
				JsonNode SERVER_DATA = SERVER_JSON.get(I);
				SERVER_LIST.add(new SERVER_INFO(
					SERVER_DATA.get("ID").asText(),
					SERVER_DATA.get("IP").asText(),
					SERVER_DATA.get("NAME").asText(),
					SERVER_DATA.get("NOTE").asText()
				));
			}
		} else {
			//無いなら作る
			SERVER_LIST_FILE.createNewFile();
			Files.write(SERVER_LIST_FILE.toPath(), "[]".getBytes());
		}

		//ユーザーデータロード
		File USER_FILE = new File(CONFIG_DATA.get("RSM").asString("USER"));
		if (USER_FILE.exists()) {
			for (String LINE:Files.readAllLines(USER_FILE.toPath())) {
				String UID = LINE.split(":")[0];
				String PASS = LINE.split(":")[1];
				USER_LIST.put(UID, PASS);
			}
		} else {
			USER_FILE.createNewFile();
			Files.write(USER_FILE.toPath(), ("admin:" + HASH.Gen(HASH_TYPE.SHA256, "admin".getBytes())).getBytes());
		}

		//サーバー起動
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new RMS_SERVER().Main(THIS);
				} catch (Exception EX) {
					EXP(EX);
				}
			}
		}).start();

		//WebUIを起動
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					new WebUI().Main(THIS);
				} catch (Exception EX) {
					EXP(EX);
				}
			}
		}).start();
	}

	public SERVER_INFO GetIPtoSERVER(String IP) {
		for (SERVER_INFO SERVER:SERVER_LIST) {
			if (SERVER.getIP().equals(IP)) {
				return SERVER;
			}
		}
		return null;
	}

	public SERVER_INFO GetIDtoSERVER(String ID) {
		for (SERVER_INFO SERVER:SERVER_LIST) {
			if (SERVER.getID().equals(ID)) {
				return SERVER;
			}
		}
		return null;
	}
}
