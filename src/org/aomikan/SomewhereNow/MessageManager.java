package org.aomikan.SomewhereNow;

import java.io.*;

/**
 * MessageManager �N���X�́A�e�{�^���̃��x���Ⓤ�e���e�̐ݒ��ۑ��E�񋟂��܂��B
 */
public class MessageManager {
	private String buttonsConfigURI = null;
	
	public MessageManager() {
		buttonsConfigURI = SomewhereNow.DEFAULT_MESSAGE_FILE;
	}
	public MessageManager(String buttonsConfigURI) {
		this.buttonsConfigURI = buttonsConfigURI;
	}
	
	/**
	 * �e�{�^���ɐݒ肳��Ă��镶���̈ꗗ���擾���܂��B
	 * 
	 * @return �ݒ肳��Ă��镶���̈ꗗ
	 */
	public String[] getButtonsText() {
		
		String line;
		String lines[] = new String[SomewhereNow.ONETOUCH_BUTTONS];
		int count = 0;
				
		try {
			BufferedReader br = new BufferedReader(
					new FileReader(buttonsConfigURI));
			while((line = br.readLine()) != null) {
                lines[count] = line;
                count++;
            }
			br.close();
			if(lines.length < SomewhereNow.ONETOUCH_BUTTONS) {
				new DialogManager().showErrorDialog(41, buttonsConfigURI, true);
			} else if (lines.length > SomewhereNow.ONETOUCH_BUTTONS) {
				new DialogManager().showErrorDialog(42, buttonsConfigURI, true);
			} else {
				return lines;
			}
		} catch(FileNotFoundException e) {
			e.printStackTrace();
			new DialogManager().showErrorDialog(40, buttonsConfigURI, true);
		} catch(Exception e) {
			e.printStackTrace();
			new DialogManager().showErrorDialog(49, null, true);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param messages
	 * @return ��������� true, ���s����� false
	 */
	public boolean setButtonsText(String messages[]) {
		
		try {
			BufferedWriter bw = new BufferedWriter(
					new FileWriter(buttonsConfigURI, false));	// �V�K�������[�h
			for(int i = 0; i < SomewhereNow.ONETOUCH_BUTTONS; i++) {
				bw.write(messages[i]);
				bw.newLine();
			}
			bw.close();
		} catch(FileNotFoundException e) {
			new DialogManager().showErrorDialog(40, buttonsConfigURI, false);
			return false;
		} catch(IOException e) {
			new DialogManager().showErrorDialog(43, buttonsConfigURI, false);
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
