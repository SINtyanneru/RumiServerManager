package com.rumisystem.rumiservermanager;

import static com.rumisystem.rumi_java_lib.LOG_PRINT.Main.LOG;

import java.util.Iterator;

import com.rumisystem.rumi_java_lib.ArrayNode;
import com.rumisystem.rumi_java_lib.CONFIG;
import com.rumisystem.rumi_java_lib.EXCEPTION_READER;
import com.rumisystem.rumi_java_lib.LOG_PRINT.LOG_TYPE;
import com.rumisystem.rumiservermanager.PARENT.ParentMain;
import com.rumisystem.rumiservermanager.TYPE.START_MODE;

public class Main {
	public static final String VERSION = "0.5";
	public static ArrayNode CONFIG_DATA = null;

	public static void main(String[] args) {
		try {
			START_MODE MODE = null;

			LOG(LOG_TYPE.INFO, "---------------[RumiServerManager]---------------");
			LOG(LOG_TYPE.INFO, "るみ");
			LOG(LOG_TYPE.INFO, "Version:" + VERSION);
			LOG(LOG_TYPE.INFO, "-------------------------------------------------");

			//設定をロード
			CONFIG_DATA = new CONFIG().DATA;
			if (CONFIG_DATA == null) {
				LOG(LOG_TYPE.FAILED, "設定ファイルが読み込めませんでした");
				System.exit(1);
			}

			//コマンドライン引数を解析
			for (int I = 0; I < args.length; I += 2) {
				String KEY = args[I];
				String VAL = args[I + 1];

				switch (KEY) {
					case "--mode": {
						if (VAL.equals("parent")) {
							MODE = START_MODE.PARENT;
						} else if (VAL.equals("child")) {
							MODE = START_MODE.CHILD;
						} else {
							System.err.println("エラー:モード選択が意味不明です");
							System.exit(1);
						}
						break;
					}

					default: {
						System.err.println("エラー:コマンドライン引数が意味不明です");
						System.exit(1);
					}
				}
			}

			if (MODE != null) {
				if (MODE == START_MODE.PARENT) {
					//親
					new ParentMain().Main();
				} else {
					//子
				}
			} else {
				System.out.println("コマンドライン引数に親か子かをセットしてください!");
				System.out.println("親:parent");
				System.out.println("子:child");
			}
		} catch (Exception EX) {
			EXP(EX);
		}
	}

	public static void EXP(Exception EX) {
		String EX_TEXT = EXCEPTION_READER.READ(EX);
		for (String LINE:EX_TEXT.split("\n")) {
			LOG(LOG_TYPE.FAILED, LINE);
		}
	}
}
