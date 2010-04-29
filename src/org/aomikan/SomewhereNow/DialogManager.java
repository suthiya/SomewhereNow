package org.aomikan.SomewhereNow;

import javax.swing.JOptionPane;

/**
 * DialogManager �N���X�́A�e�N���X�Ŕ�������l�X�ȃG���[�𓝈�I�Ɉ����\�����܂��B
 */
public class DialogManager {
	
	/**
	 * �G���[�_�C�A���O�𐶐��E�\�����܂��B
	 * 
	 * @param number �ǐՔԍ�
	 * @param message �t�����b�Z�[�W, �Ȃ���� null
	 * @param postClose �\����A�v�����I������Ȃ�� true, ���̂܂ܑ��s����Ȃ�� false
	 */
	void showErrorDialog(int number, String message, boolean postClose) {
		switch(number) {
		
		// at SomewhereNow
		
		case 1:
			System.err.println("Error #01: Illigal argments.");
			JOptionPane.showMessageDialog(null, "�G���[ #01: " +
					"�������������܂��B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 2:
			System.err.println("Error #02: Configuration file: " +
					message + " check error.");
			JOptionPane.showMessageDialog(null, "�G���[ #02: " +
			"�ݒ�t�@�C�� " + message + " ���`�F�b�N����ۂɃG���[���������܂����B",
			"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 3:
			System.err.println("Error #03: Configuration file: " +
					message + " create error.");
			JOptionPane.showMessageDialog(null, "�G���[ #03: " +
			"�ݒ�t�@�C�� " + message + " �𐶐�����ۂɃG���[���������܂����B",
			"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		// at TwitterAccount
			
		case 11:
			System.err.println("Error #11: Login failed.");
			JOptionPane.showMessageDialog(null, "�G���[ #11: " +
					"�s���Ȍ����ɂ�� Twitter �Ƀ��O�C���ł��܂���B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 15:
			System.err.println("Error #15: Unable to get the access token.");
			JOptionPane.showMessageDialog(null, "�G���[ #15: " +
					"�A�N�Z�X�g�[�N�����擾�ł��܂���B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		// at PropertyManager
			
		case 20:
			System.err.println("Error #20: Configuration file: " +
					message + " write error.");
			JOptionPane.showMessageDialog(null, "�G���[ #20: " +
			"�ݒ�t�@�C�� " + message + " ���������ލۂɃG���[���������܂����B",
			"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 21:
			System.err.println("Error #21: Configuration file: " +
					message + " not found.");
			JOptionPane.showMessageDialog(null, "�G���[ #21: " +
			"�ݒ�t�@�C�� " + message + " ������܂���B",
			"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		// at MessageManager
			
		case 40:
			System.err.println("Error #40: Configuration file: " +
					message + " not found.");
			JOptionPane.showMessageDialog(null, "�G���[ #40: " +
					"�ݒ�t�@�C�� " + message + " ������܂���B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 41:
			System.err.println("Error #41: " + message + " too small.");
			JOptionPane.showMessageDialog(null, "�G���[ #41: " +
					"�ݒ�t�@�C�� " + message + " �̓��e�����Ȃ����܂��B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 42:
			System.err.println("Error #42: " + message + " too large.");
			JOptionPane.showMessageDialog(null, "�G���[ #41: " +
					"�ݒ�t�@�C�� " + message + " �̓��e���������܂��B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;

		case 43:
			System.err.println("Error #43: Configuration file: " +
					message + " write error.");
			JOptionPane.showMessageDialog(null, "�G���[ #43: " +
					"�ݒ�t�@�C�� " + message + " �̏����݂Ɏ��s���܂����B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		// at SomewhereNowUI
		
		case 100:
			System.err.println("Error #10:Proxy configuration not changed.");
			JOptionPane.showMessageDialog(null, "�G���[ #100: " +
					"�ݒ���e��ύX���Ă���A�v���L�V�ݒ��K�p����K�v������܂��B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 101:
			System.err.println("Error #101: PIN is empty.");
			JOptionPane.showMessageDialog(null, "�G���[ #101: " +
					"PIN �����͂���Ă��܂���B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 102:
			System.err.println("Error #102: Some text box is empty.");
			JOptionPane.showMessageDialog(null, "�G���[ #102: " +
					"�󗓂�����܂��B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 103:
			System.err.println("Error #103: Available A to Z, a to z and" +
					" 0 to 9 only.");
			JOptionPane.showMessageDialog(null, "�G���[ #103: " +
					"���͉\�ȕ����́A���p�p���� (0-9, a-z, A-Z) �݂̂ł��B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 104:
			System.err.println("Error #104: Text box #" + message +
					" is empty.");
			JOptionPane.showMessageDialog(null, "�G���[ #104: " +
					"�{�^�� " + message + " �̓��͗����L������Ă��܂���B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 110:
			System.err.println("Error #110: Tweeting failed.");
			JOptionPane.showMessageDialog(null, "�G���[ #110: " +
					"�c�C�[�g�Ɏ��s���܂����B" +
					System.getProperty("line.separator") +
					"�T�[�r�X����~���Ă���Ȃǂ��l�����܂��B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 111:
			System.err.println("Error #111: Tweetter login failed.");
			JOptionPane.showMessageDialog(null, "�G���[ #111: " +
					"�c�C�b�^�[�Ƀ��O�C���ł��܂���ł����B" +
					System.getProperty("line.separator") +
					"�ݒ��ʂ��J���A���O�C�������Đݒ肵�Ă��������B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 112:
			System.err.println("Error #112: Tweetter post failed.");
			JOptionPane.showMessageDialog(null, "�G���[ #112: " +
					"�c�C�[�g�ł��܂���ł����B" +
					System.getProperty("line.separator") +
					"�T�[�r�X���s����ɂȂ��Ă���ꍇ��A��d���e�Ȃǂ��l�����܂��B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
			
		// default
			
		default:
			System.err.println("Error #" + number + ": Unknown Error.");
			JOptionPane.showMessageDialog(null, "�G���[ #" + number +
					": " + "�����s���̃G���[�ł��B",
					"�G���[" , JOptionPane.ERROR_MESSAGE);
			break;
		}
		
		if(postClose) {
			// �v���O�����I��
			System.exit(1);
		}
	}
	
	/**
	 * ���b�Z�[�W�_�C�A���O��\�����܂��B
	 * 
	 * @param message
	 */
	void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(null, message,
				"���b�Z�[�W", JOptionPane.INFORMATION_MESSAGE);
	}
}
