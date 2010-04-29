/**
 * Somewhere Now - ���ǂ��ɂ���̂� 1 �N���b�N�łԂ₭ Twitter �N���C�A���g
 * 
 * 
 * ���C�Z���X:
 * 		BSD License
 * 
 * �E�F�u�T�C�g:
 * 		http://apps.aomikan.org/somewherenow
 * 
 * @author Y.K., 2010
 * @version 0.2.0
 */
package org.aomikan.SomewhereNow;

import java.io.*;

/**
 * SomewhereNow �N���X�́A main() ���܂ލŏ��̃N���X�ŁA�����̎����K�v�ȃN���X�̏��������s��
 * �܂��B
 *
 */
public class SomewhereNow {

	// �萔
	public static final String VERSION = "0.2.0 beta1";
	public static final String APPLICATION_NAME = "SomewhereNow!";
	public static final String URL = "http://apps.aomikan.org/somewherenow";
	public static final int ONETOUCH_BUTTONS = 4;
	public static final int PROPERTY_LIST = 1;
	public static final int MESSAGE_FILE = 2;
	public static final String DEFAULT_PROPERTY_LIST = "config.properties";
	public static final String DEFAULT_MESSAGE_FILE = "buttons.conf";
	
	// �����o�[�ϐ�
	public static String propertyFileURI = null;
	private static TwitterAccount user = null;
	
	/**
	 * @param args �v���p�e�B�t�@�C�� URI
	 */
	public static void main(String[] args) {
		
		// �����`�F�b�N
		switch(args.length) {
		
		case 0:	// �����Ȃ�
			propertyFileURI = DEFAULT_PROPERTY_LIST;
			checkFile(MESSAGE_FILE, DEFAULT_MESSAGE_FILE);	// buttons.conf �m�F
			break;
			
		case 1:	// �v���p�e�B�t�@�C���w��
			propertyFileURI = args[0];
			break;
			
		default:
			new DialogManager().showErrorDialog(1, null, false);
			return;
		}
		
		// �t�@�C���`�F�b�N
		checkFile(PROPERTY_LIST, propertyFileURI);
				
		// �A�J�E���g�C���X�^���X����
		user = new TwitterAccount();
		
		// GUI �p�l���N��
		new SomewhereNowUI(user);
	}
	
	/**
	 * �f�t�H���g�̐ݒ�t�@�C���p�X�ŋN������ꍇ�A���̑��݂��m�F���܂��B
	 * @param fileType �t�@�C���̎�ʂ�萔�Ŏw��
	 * @param fileURI �t�@�C���̃p�X���w��
	 */
	private static void checkFile(int fileType, String fileURI) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileURI));
			
			// �����ǂ݂ŋ���ۂȂ璆�g�𐶐����ăN���[�Y
			if(br.readLine() == null) {
				br.close();
				createConfigFile(fileType);
			}
			// ����Ȃ炻�̂܂܃N���[�Y
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
	 * �ݒ�t�@�C�������݂��Ȃ��ꍇ�Ƀf�t�H���g�\���̐ݒ�t�@�C���𐶐����܂��B
	 * 
	 * @param fileType�@�ݒ�t�@�C���̎�ʂ�萔�Ŏw��
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
	 * �ݒ�t�@�C�������݂��Ȃ��ꍇ�Ɏw�肳�ꂽ�ꏊ�փf�t�H���g�\���̐ݒ�t�@�C���𐶐����܂��B
	 * 
	 * @param fileType�@�ݒ�t�@�C���̎�ʂ�萔�Ŏw��
	 * @param fileURI �ݒ�t�@�C���̃p�X
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
				bw.write("�ƂȂ�" + ls);
				bw.write("�������Ȃ�" + ls);
				bw.write("���ƂȂ�" + ls);
				bw.write("���тȂ�" + ls);
				bw.close();
			} catch(IOException e) {
				e.printStackTrace();
				new DialogManager().showErrorDialog(3, fileURI, true);
			}
			break;
		}
	}
}
