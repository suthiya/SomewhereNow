package org.aomikan.SomewhereNow;

import java.io.*;
import java.util.Properties;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * PropertyManager �N���X�́A�v���p�e�B�t�@�C���̓��o�͂�S�����܂��B<br />
 * �f�t�H���g�̃v���p�e�B�t�@�C�� URI �� config.properties �ł��B
 */
class PropertyManager {

	// �萔
	private static final String CRYPT_MODE = "Blowfish";	// �Í������[�h
	
	// �ϐ�
	private String propertyFile = null;	// �v���p�e�B�t�@�C�� URI
	private Properties prop = null;		// �v���p�e�B�t�@�C�����o�̓N���X�̃I�u�W�F�N�g
	private String digits = null;		// �Í����L�[���[�h
	
	/**
	 * PropertyManager �̃R���X�g���N�^�ł��B<br />
	 * �����Ŏw�肳�ꂽ�v���p�e�B�t�@�C�������[�h���ď��������܂��B
	 * 
	 * @param propertyFile �v���p�e�B�t�@�C���̃p�X
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
	 * �A�J�E���g���ݒ�ς��ۂ��𔻕ʂ��܂��B
	 * @return �ݒ�ς݂Ȃ� true, ���ݒ�Ȃ� false
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
	 * Twitter �A�J�E���g�� OAuth ���L�����ۂ��𔻕ʂ��܂��B
	 * 
	 * @return �L���Ȃ� true, �����Ȃ� false
	 */
	public boolean isOAuth() {
		if(prop.getProperty("twitter.oauth").equals("1")) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * OAuth �̗L��/������؂�ւ��܂��B
	 * 
	 * @param state �L���ɂ���ꍇ true, �����ɂ���ꍇ true
	 * @return ��������� true, ���s����� false
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
	 * �v���L�V�ݒ� (�T�[�o, �|�[�g�ԍ�) ���擾���܂��B
	 * 
	 * @return [0]: �v���L�V�T�[�o, [1]: �|�[�g�ԍ�
	 */
	public String[] getProxySettings() {
		String[] target = new String[2];
		target[0] = prop.getProperty("proxy.server");
		target[1] = prop.getProperty("proxy.port");
		return target;
	}
	
	/**
	 * �v���L�V�ݒ���i�������܂��B
	 * 
	 * @param target �v���L�V�ݒ���i�[���� String[2], [0] �ɃT�[�o, [1] �Ƀ|�[�g�ԍ�
	 * @return
	 */
	public boolean setProxySettings(String[] target) {
		prop.setProperty("proxy.server", target[0]);
		prop.setProperty("proxy.port", target[1]);
		storeProperty();
		return true;
	}
	
	/**
	 * �v���p�e�B���X�g�t�@�C���̕ύX�𔽉f���܂��B
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
	 * �x�[�X���[�h������ 8 �o�C�g�̈Í����f�B�W�b�g�𐶐����܂��B<br />
	 * �������ꂽ�f�B�W�b�g�� digits �����o�[�ϐ��Ɋi�[����܂��B
	 * @param baseWord �x�[�X���[�h
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
	 * getEncryptedPassword ���\�b�h�́A�����̃p�X���[�h���Í������܂�
	 * @param plainPassword �����̃p�X���[�h
	 * @param digits �Í����p�f�B�W�b�g (8�o�C�g)
	 * @return �Í������ꂽ�p�X���[�h
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
	 * getDecryptedPassword ���\�b�h�́A�������ꂽ�p�X���[�h���擾���܂�
	 * @param encryptedPassword �Í������ꂽ�p�X���[�h
	 * @param digits �Í����p�f�B�W�b�g (8�o�C�g)
	 * @return �������ꂽ�p�X���[�h
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

