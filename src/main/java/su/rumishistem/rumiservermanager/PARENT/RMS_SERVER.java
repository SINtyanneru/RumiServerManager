package su.rumishistem.rumiservermanager.PARENT;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static su.rumishistem.rumiservermanager.Main.CONFIG_DATA;
import static su.rumishistem.rumiservermanager.Main.EXP;
import static su.rumishistem.rumiservermanager.Main.VERSION;

import java.io.IOException;

import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import com.rumisystem.rumi_java_lib.Socket.Server.SocketServer;
import com.rumisystem.rumi_java_lib.Socket.Server.CONNECT_EVENT.CONNECT_EVENT;
import com.rumisystem.rumi_java_lib.Socket.Server.CONNECT_EVENT.CONNECT_EVENT_LISTENER;
import com.rumisystem.rumi_java_lib.Socket.Server.EVENT.CloseEvent;
import com.rumisystem.rumi_java_lib.Socket.Server.EVENT.EVENT_LISTENER;
import com.rumisystem.rumi_java_lib.Socket.Server.EVENT.MessageEvent;
import com.rumisystem.rumi_java_lib.Socket.Server.EVENT.ReceiveEvent;

import su.rumishistem.rumiservermanager.TYPE.CLIENT_TYPE;
import su.rumishistem.rumiservermanager.TYPE.SERVER_INFO;

public class RMS_SERVER {
	public void Main(ParentMain MAIN) throws IOException {
		//サーバーを起動
		SocketServer SS = new SocketServer();
		SS.setEventListener(new CONNECT_EVENT_LISTENER() {
			@Override
			public void CONNECT(CONNECT_EVENT SESSION) {
				try {
					String IP = SESSION.getIP();
					boolean[] HELO = {false}; //←javaのクソ仕様
					CLIENT_TYPE[] TYPE = {CLIENT_TYPE.ADMIN};

					//ログ
					LOG(LOG_TYPE.OK, "Connect:" + IP);

					//ようこそメッセージ
					SESSION.sendMessage("SALUTON " + VERSION + "\r\n");

					SESSION.setEventListener(new EVENT_LISTENER() {
						@Override
						public void Receive(ReceiveEvent e) {
							try {
								String[] CMD = e.getString().split(" ");

								if (HELO[0]) {
									switch (CMD[0]) {
										case "LIST": {
											for (SERVER_INFO SERVER:MAIN.SERVER_LIST) {
												String STATUS = "OFFLINE";

												//オンラインかオフラインかの判定
												if (SERVER.getSTATUS()) {
													STATUS = "ONLINE";
												}

												SESSION.sendMessage("\t" + SERVER.getID() + "," + SERVER.getNAME() + "," + STATUS + "\r\n");
											}
											SESSION.sendMessage("200 OK\r\n");
											break;
										}

										default: {
											SESSION.sendMessage("400 Command ga nai\r\n");
											break;
										}
									}
								} else {
									//接続後の初期設定を行う必要がある
									if (CMD[0].equals("HELO")) {
										if (CMD[1].equals("SERVER")) {
											//サーバー
											SERVER_INFO SERVER = MAIN.GetIPtoSERVER(IP);
											if (SERVER != null) {
												TYPE[0] = CLIENT_TYPE.SERVER;
												SERVER.setSESSION(SESSION);
											} else {
												//サーバーリストに未登録
												SESSION.sendMessage("400 Server ga mitouroku desu\r\n");
												SESSION.close();
												return;
											}
										} else if (CMD[1].equals("ADMIN")) {
											//管理者
											TYPE[0] = CLIENT_TYPE.ADMIN;
										} else {
											//どれでもない
											SESSION.sendMessage("400 Client type ga fucking\r\n");
											SESSION.close();
											return;
										}

										HELO[0] = true;
										SESSION.sendMessage("200 OK saluton\r\n");
									} else {
										//最初に挨拶をしないのは99%別のプロトコルを送ってきているおバカさんなので切断する
										SESSION.close();
									}
								}
							} catch (Exception EX) {
								EXP(EX);
							}
						}

						@Override
						public void Message(MessageEvent e) {
						}

						@Override
						public void Close(CloseEvent e) {
							LOG(LOG_TYPE.OK, "Disconnect:" + IP);

							//サーバーリストのセッションを破棄
							SERVER_INFO SERVER = MAIN.GetIPtoSERVER(IP);
							if (SERVER != null) {
								SERVER.setSESSION(null);
							}
						}
					});
				} catch (Exception EX) {
					EXP(EX);
				}
			}
		});
		SS.START(CONFIG_DATA.get("RSM").asInt("PORT"));
	}
}
