/**
 * Somewhere Now - 今どこにいるのか 1 クリックでつぶやく Twitter クライアント
 * 
 * 
 * ライセンス:
 * 		BSD License
 * 
 * ウェブサイト:
 * 		http://apps.aomikan.org/somewherenow
 * 
 * @author Y.K., 2010
 * @version 0.2.0
 */
package org.aomikan.SomewhereNow;

import java.io.*;

/**
 * SomewhereNow クラスは、 main() を含む最初のクラスで、引数の授受や必要なクラスの初期化を行い
 * ます。
 *
 */
public class SomewhereNow {

	// 定数
	public static final String VERSION = "0.2.0 beta1";
	public static final String APPLICATION_NAME = "SomewhereNow!";
	public static final String URL = "http://apps.aomikan.org/somewherenow";
	public static final int ONETOUCH_BUTTONS = 4;
	public static final int PROPERTY_LIST = 1;
	public static final int MESSAGE_FILE = 2;
	public static final String DEFAULT_PROPERTY_LIST = "config.properties";
	public static final String DEFAULT_MESSAGE_FILE = "buttons.conf";
	
	// メンバー変数
	public static String propertyFileURI = null;
	private static TwitterAccount user = null;
	
	/**
	 * @param args プロパティファイル URI
	 */
	public static void main(String[] args) {
		
		// 引数チェック
		switch(args.length) {
		
		case 0:	// 引数なし
			propertyFileURI = DEFAULT_PROPERTY_LIST;
			checkFile(MESSAGE_FILE, DEFAULT_MESSAGE_FILE);	// buttons.conf 確認
			break;
			
		case 1:	// プロパティファイル指定
			propertyFileURI = args[0];
			break;
			
		default:
			new DialogManager().showErrorDialog(1, null, false);
			return;
		}
		
		// ファイルチェック
		checkFile(PROPERTY_LIST, propertyFileURI);
				
		// アカウントインスタンス生成
		user = new TwitterAccount();
		
		// GUI パネル起動
		new SomewhereNowUI(user);
	}
	
	/**
	 * デフォルトの設定ファイルパスで起動する場合、その存在を確認します。
	 * @param fileType ファイルの種別を定数で指定
	 * @param fileURI ファイルのパスを指定
	 */
	private static void checkFile(int fileType, String fileURI) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileURI));
			
			// 試し読みで空っぽなら中身を生成してクローズ
			if(br.readLine() == null) {
				br.close();
				createConfigFile(fileType);
			}
			// 正常ならそのままクローズ
			else {
				br.close();
			}
		} catch(FileNotFoundException e) {
			createConfigFile(fileType);
		} catch(IOException e) {
			e.printStackTrace();
			new DialogManager().showErrorDialog(2, fileURI, true);
		}
	}
	
	/**
	 * 設定ファイルが存在しない場合にデフォルト構成の設定ファイルを生成します。
	 * 
	 * @param fileType　設定ファイルの種別を定数で指定
	 */
	private static void createConfigFile(int fileType) {
		switch(fileType) {
		
		case PROPERTY_LIST:
			createConfigFile(fileType, DEFAULT_PROPERTY_LIST);
			break;
			
		case MESSAGE_FILE:
			createConfigFile(fileType, DEFAULT_MESSAGE_FILE);
			break;
		}
	}
	
	/**
	 * 設定ファイルが存在しない場合に指定された場所へデフォルト構成の設定ファイルを生成します。
	 * 
	 * @param fileType　設定ファイルの種別を定数で指定
	 * @param fileURI 設定ファイルのパス
	 */
	private static void createConfigFile(int fileType, String fileURI) {
		String ls = System.getProperty("line.separator");
		
		switch(fileType) {
		
		case PROPERTY_LIST:
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(fileURI));
				bw.write("message.file=" + DEFAULT_MESSAGE_FILE + ls);
				bw.write("twitter.active=0" + ls);
				bw.write("twitter.user=" + ls);
				bw.write("twitter.password=" + ls);		// for PBE
				bw.write("twitter.oauth=1" + ls);
				bw.write("twitter.search=" + ls);
				bw.write("twitter.search.hide=0");		// Since 0.2.0
				bw.write("twitter.accesstoken=" + ls);	// for OAuth
				bw.write("twitter.secrettoken=" + ls);	// for OAuth
				bw.write("twitter.requesturi=" + ls);	// for OAuth
				bw.write("proxy.active=0" + ls);
				bw.write("proxy.server=" + ls);
				bw.write("proxy.port=8080" + ls);
				bw.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
			break;
			
		case MESSAGE_FILE:
			try {
				BufferedWriter bw = new BufferedWriter(new FileWriter(fileURI));
				bw.write("家なう" + ls);
				bw.write("研究室なう" + ls);
				bw.write("授業なう" + ls);
				bw.write("ご飯なう" + ls);
				bw.close();
			} catch(IOException e) {
				e.printStackTrace();
				new DialogManager().showErrorDialog(3, fileURI, true);
			}
			break;
		}
	}
}
