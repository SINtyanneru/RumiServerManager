package su.rumishistem.rumiservermanager.PARENT;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiservermanager.Main.CONFIG_DATA;
import static su.rumishistem.rumiservermanager.Main.EXP;
import static su.rumishistem.rumiservermanager.Main.VERSION;

import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import com.rumisystem.rumi_java_lib.HASH;
import com.rumisystem.rumi_java_lib.HASH.HASH_TYPE;
import com.rumisystem.rumi_java_lib.URLParam;
import com.rumisystem.rumi_java_lib.HTTP_SERVER.HTTP_EVENT;
import com.rumisystem.rumi_java_lib.HTTP_SERVER.HTTP_EVENT_LISTENER;
import com.rumisystem.rumi_java_lib.HTTP_SERVER.HTTP_SERVER;
import com.rumisystem.rumi_java_lib.RESOURCE.RESOURCE_MANAGER;

public class WebUI {
	private HashMap<String, String> SESSION_LIST = new HashMap<String, String>();

	public void Main(ParentMain MAIN) throws IOException {
		HTTP_SERVER HTTP = new HTTP_SERVER(CONFIG_DATA.get("HTTP").asInt("PORT"));
		HTTP.SET_EVENT_VOID(new HTTP_EVENT_LISTENER() {
			@Override
			public void REQUEST_EVENT(HTTP_EVENT e) {
				try {
					if (e.getEXCHANGE().getRequestURI().getPath().equals("/favicon.ico")) {
						//Favicon
						e.setHEADER("Content-Type", "image/vnd.microsoft.icon; charset=binary");
						e.REPLY_BYTE(200, new RESOURCE_MANAGER().getResourceData("/HTML/favicon.ico"));
						return;
					} else if (e.getEXCHANGE().getRequestURI().getPath().equals("/login")) {
						//ログイン
						HashMap<String, String> LOGIN_DATA = URLParam.Parse(e.getPOST_DATA());

						if (MAIN.USER_LIST.get(LOGIN_DATA.get("UID")) != null) {
							if (MAIN.USER_LIST.get(LOGIN_DATA.get("UID")).equals(HASH.Gen(HASH_TYPE.SHA256, LOGIN_DATA.get("PASSWORD").getBytes()))) {
								String SESSION = UUID.randomUUID().toString();
								SESSION_LIST.put(SESSION, LOGIN_DATA.get("UID"));

								e.setCookie("SESSION", SESSION, 7 * 24 * 60 * 60, null, "/", false, false);
								e.setHEADER("Location", "/");
								e.REPLY_BYTE(302, "Haj".getBytes());
								return;
							} else {
								e.REPLY_BYTE(401, "ユーザーが存在しないか、パスワードが違います".getBytes());
								return;
							}
						} else {
							e.REPLY_BYTE(401, "ユーザーが存在しないか、パスワードが違います".getBytes());
							return;
						}
					} else {
						//その他
						e.setHEADER("Content-Type", "text/html; charset=utf-8");

						switch (e.getEXCHANGE().getRequestURI().getPath()) {
							case "/": {
								if (e.getCookie().get("SESSION") != null) {
									String SESSION = SESSION_LIST.get(e.getCookie().get("SESSION"));
									if (SESSION != null) {
										e.REPLY_BYTE(200, new RESOURCE_MANAGER().getResourceData("/HTML/index.html"));
									} else {
										e.REPLY_BYTE(200, new RESOURCE_MANAGER().getResourceData("/HTML/login.html"));
									}
								} else {
									e.REPLY_BYTE(200, new RESOURCE_MANAGER().getResourceData("/HTML/login.html"));
								}
								return;
							}
	
							default: {
								e.REPLY_String(404, "404");
								return;
							}
						}
					}
				} catch (Exception EX) {
					EXP(EX);
				}
			}
		});
		HTTP.START_HTTPSERVER();
	}
}
