package org.aomikan.SomewhereNow;

import java.io.*;
import java.util.Properties;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * PropertyManager クラスは、プロパティファイルの入出力を担当します。<br />
 * デフォルトのプロパティファイル URI は config.properties です。
 */
class PropertyManager {

	// 定数
	private static final String CRYPT_MODE = "Blowfish";	// 暗号化モード
	
	// 変数
	private String propertyFile = null;	// プロパティファイル URI
	private Properties prop = null;		// プロパティファイル入出力クラスのオブジェクト
	private String digits = null;		// 暗号化キーワード
	
	/**
	 * PropertyManager のコンストラクタです。<br />
	 * 引数で指定されたプロパティファイルをロードして初期化します。
	 * 
	 * @param propertyFile プロパティファイルのパス
	 */
	public PropertyManager() {
		propertyFile = SomewhereNow.propertyFileURI;
		try {
			prop = new Properties();
			prop.load(new FileInputStream(propertyFile));
		} catch (FileNotFoundException e) {
			new DialogManager().showErrorDialog(21, propertyFile, true);
			e.printStackTrace();
		} catch (IOException e) {
			new DialogManager().showErrorDialog(20, propertyFile, true);
			e.printStackTrace();
		}
	}
	
	/**
	 * アカウントが設定済か否かを判別します。
	 * @return 設定積みなら true, 未設定なら false
	 */
	public boolean isTwitterActive() {
		if(prop.getProperty("twitter.active").equals("1")) {
			return true;
		} else {
			return false;
		}
	}
	
	public void enableTwitter(boolean state) {
		if(state) {
			prop.setProperty("twitter.active", "1");
		} else {
			prop.setProperty("twitter.active", "0");
		}
		storeProperty();
	}
		
	/**
	 * Twitter アカウントで OAuth が有効か否かを判別します。
	 * 
	 * @return 有効なら true, 無効なら false
	 */
	public boolean isOAuth() {
		if(prop.getProperty("twitter.oauth").equals("1")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * OAuth の有効/無効を切り替えます。
	 * 
	 * @param state 有効にする場合 true, 無効にする場合 true
	 * @return 成功すれば true, 失敗すれば false
	 */
	public boolean setOAuth(boolean state) {
		if(state) {
			prop.setProperty("twitter.oauth", "1");
		} else {
			prop.setProperty("twitter.oauth", "0");
		}
		storeProperty();
		return true;
	}
	
	public String getTwitterUserID() {
		String userID = prop.getProperty("twitter.user");
		createDigits(userID);
		return userID;
	}
	
	public boolean setTwitterUserID(String userID) {
		prop.setProperty("twitter.user", userID);
		storeProperty();
		createDigits(userID);
		return true;
	}
	
	public String getTwitterPassword() {
		String plain = prop.getProperty("twitter.password");
		return getDecryptedPassword(plain, this.digits);
	}
	
	public boolean setTwitterPassword(String password) {
		String enc = getEncryptedPassword(password, this.digits);
		prop.setProperty("twitter.password", enc);
		storeProperty();
		return true;
	}
	
	public String getTwitterGroup() {
		String search = prop.getProperty("twitter.search");
		return search;
	}
	
	public boolean setTwitterGroup(String groupTag) {
		prop.setProperty("twitter.search", groupTag);
		storeProperty();
		return true;
	}
	
	public String getButtonsConfigURI() {
		String buttonsConf = prop.getProperty("message.file");
		if(buttonsConf == null) {
			buttonsConf = "buttons.conf";
		}
		return buttonsConf;
	}
	
	public String[] getAccessToken() {
		String[] token = new String[2];
		token[0] = prop.getProperty("twitter.accesstoken");
		token[1] = prop.getProperty("twitter.secrettoken");
		return token;
	}
	
	public boolean setAccessToken(String[] token) {
		prop.setProperty("twitter.accesstoken", token[0]);
		prop.setProperty("twitter.secrettoken", token[1]);
		storeProperty();
		return true;
	}
	
	public String getRequestURL() {
		String url = prop.getProperty("twitter.requesturl");
		return url;
	}
	
	public boolean setRequestURL(String url) {
		prop.setProperty("twitter.requesturl", url);
		storeProperty();		
		return true;
	}
	
	public boolean isProxyEnabled() {
		if(prop.getProperty("proxy.active").equals("1")) {
			return true;
		} else {
			return false;
		}
	}
	
	public void enableProxy(boolean state) {
		if(state) {
			prop.setProperty("proxy.active", "1");
		} else {
			prop.setProperty("proxy.active", "0");
		}
		storeProperty();
	}
	
	/**
	 * プロキシ設定 (サーバ, ポート番号) を取得します。
	 * 
	 * @return [0]: プロキシサーバ, [1]: ポート番号
	 */
	public String[] getProxySettings() {
		String[] target = new String[2];
		target[0] = prop.getProperty("proxy.server");
		target[1] = prop.getProperty("proxy.port");
		return target;
	}
	
	/**
	 * プロキシ設定を永続化します。
	 * 
	 * @param target プロキシ設定を格納した String[2], [0] にサーバ, [1] にポート番号
	 * @return
	 */
	public boolean setProxySettings(String[] target) {
		prop.setProperty("proxy.server", target[0]);
		prop.setProperty("proxy.port", target[1]);
		storeProperty();
		return true;
	}
	
	/**
	 * プロパティリストファイルの変更を反映します。
	 */
	private void storeProperty() {
		try {
			prop.store(new FileOutputStream(propertyFile), null);
		} catch (FileNotFoundException e) {
			new DialogManager().showErrorDialog(21, propertyFile, true);
			e.printStackTrace();
		} catch (IOException e) {
			new DialogManager().showErrorDialog(20, propertyFile, true);
			e.printStackTrace();
		}
	}
		
	/**
	 * ベースワードを元に 8 バイトの暗号化ディジットを生成します。<br />
	 * 生成されたディジットは digits メンバー変数に格納されます。
	 * @param baseWord ベースワード
	 */
	private void createDigits(String baseWord) {
		if(baseWord.length() < 1 || baseWord == null) {
			digits = "xxxxxxxx";
			return;
		}
		String encoded = new BASE64Encoder().encode(baseWord.getBytes());
		if(encoded.length() < 8) {
			encoded = encoded + "xxxxxxxx";
		}
		if(encoded.length() > 8) {
			encoded = encoded.substring(0, 8);
		}
		digits = encoded;
	}
	
	/**
	 * getEncryptedPassword メソッドは、平文のパスワードを暗号化します
	 * @param plainPassword 平文のパスワード
	 * @param digits 暗号化用ディジット (8バイト)
	 * @return 暗号化されたパスワード
	 */
	private String getEncryptedPassword(String plainPassword, String digits) {
		
		System.out.println("[DEBUG] getEP: " + plainPassword + "/" + digits);
		
		try {
			SecretKeySpec keySpec = new SecretKeySpec(digits.getBytes(), CRYPT_MODE);
			Cipher cipher = Cipher.getInstance(CRYPT_MODE);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec);
			byte[] encrypted = cipher.doFinal(plainPassword.getBytes());
			String encoded = new BASE64Encoder().encode(encrypted);

			System.out.println("[DEBUG] getEP: " + encoded);
			
			return new String(encoded);
		} catch(GeneralSecurityException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * getDecryptedPassword メソッドは、復号されたパスワードを取得します
	 * @param encryptedPassword 暗号化されたパスワード
	 * @param digits 暗号化用ディジット (8バイト)
	 * @return 復号されたパスワード
	 */
	private String getDecryptedPassword(String encryptedPassword, String digits) {

		System.out.println("[DEBUG] getDP: " + encryptedPassword + "/" + digits);
		
		try {
			SecretKeySpec keySpec = new SecretKeySpec(digits.getBytes(),CRYPT_MODE);
			Cipher cipher = Cipher.getInstance(CRYPT_MODE);
			cipher.init(Cipher.DECRYPT_MODE, keySpec);
			byte[] decoded = new BASE64Decoder().decodeBuffer(encryptedPassword);
			byte[] decrypted = cipher.doFinal(decoded);

			System.out.println("[DEBUG] getEP: " + decrypted);
			
			return new String(decrypted);
		} catch(GeneralSecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}

