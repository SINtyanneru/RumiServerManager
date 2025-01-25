package com.rumisystem.rumiservermanager.PARENT;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;
import static com.rumisystem.rumiservermanager.Main.CONFIG_DATA;
import static com.rumisystem.rumiservermanager.Main.VERSION;
import static com.rumisystem.rumiservermanager.Main.EXP;

import java.io.IOException;

import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import com.rumisystem.rumi_java_lib.Socket.Server.SocketServer;
import com.rumisystem.rumi_java_lib.Socket.Server.CONNECT_EVENT.CONNECT_EVENT;
import com.rumisystem.rumi_java_lib.Socket.Server.CONNECT_EVENT.CONNECT_EVENT_LISTENER;
import com.rumisystem.rumi_java_lib.Socket.Server.EVENT.CloseEvent;
import com.rumisystem.rumi_java_lib.Socket.Server.EVENT.EVENT_LISTENER;
import com.rumisystem.rumi_java_lib.Socket.Server.EVENT.MessageEvent;
import com.rumisystem.rumi_java_lib.Socket.Server.EVENT.ReceiveEvent;

public class ParentMain {
	public void Main() throws IOException {
		SocketServer SS = new SocketServer();
		SS.setEventListener(new CONNECT_EVENT_LISTENER() {
			@Override
			public void CONNECT(CONNECT_EVENT SESSION) {
				try {
					String IP = SESSION.getIP();
					boolean[] HELO = {false}; //←javaのクソ仕様

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
										default: {
											SESSION.sendMessage("400 Command ga nai\r\n");
											break;
										}
									}
								} else {
									//接続後の初期設定を行う必要がある
									if (CMD[0].equals("HELO")) {
										HELO[0] = true;
										SESSION.sendMessage("200 OK\r\n");
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
						}
					});
				} catch (Exception EX) {
					EXP(EX);
				}
			}
		});
		SS.START(CONFIG_DATA.get("RMS").asInt("PORT"));
	}
}
