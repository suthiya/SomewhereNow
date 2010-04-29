package org.aomikan.SomewhereNow;

import javax.swing.JOptionPane;

/**
 * DialogManager クラスは、各クラスで発生する様々なエラーを統一的に扱い表示します。
 */
public class DialogManager {
	
	/**
	 * エラーダイアログを生成・表示します。
	 * 
	 * @param number 追跡番号
	 * @param message 付随メッセージ, なければ null
	 * @param postClose 表示後アプリを終了するならば true, そのまま続行するならば false
	 */
	void showErrorDialog(int number, String message, boolean postClose) {
		switch(number) {
		
		// at SomewhereNow
		
		case 1:
			System.err.println("Error #01: Illigal argments.");
			JOptionPane.showMessageDialog(null, "エラー #01: " +
					"引数が多すぎます。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 2:
			System.err.println("Error #02: Configuration file: " +
					message + " check error.");
			JOptionPane.showMessageDialog(null, "エラー #02: " +
			"設定ファイル " + message + " をチェックする際にエラーが発生しました。",
			"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 3:
			System.err.println("Error #03: Configuration file: " +
					message + " create error.");
			JOptionPane.showMessageDialog(null, "エラー #03: " +
			"設定ファイル " + message + " を生成する際にエラーが発生しました。",
			"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		// at TwitterAccount
			
		case 11:
			System.err.println("Error #11: Login failed.");
			JOptionPane.showMessageDialog(null, "エラー #11: " +
					"不明な原因により Twitter にログインできません。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 15:
			System.err.println("Error #15: Unable to get the access token.");
			JOptionPane.showMessageDialog(null, "エラー #15: " +
					"アクセストークンを取得できません。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		// at PropertyManager
			
		case 20:
			System.err.println("Error #20: Configuration file: " +
					message + " write error.");
			JOptionPane.showMessageDialog(null, "エラー #20: " +
			"設定ファイル " + message + " を書き込む際にエラーが発生しました。",
			"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 21:
			System.err.println("Error #21: Configuration file: " +
					message + " not found.");
			JOptionPane.showMessageDialog(null, "エラー #21: " +
			"設定ファイル " + message + " がありません。",
			"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		// at MessageManager
			
		case 40:
			System.err.println("Error #40: Configuration file: " +
					message + " not found.");
			JOptionPane.showMessageDialog(null, "エラー #40: " +
					"設定ファイル " + message + " がありません。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 41:
			System.err.println("Error #41: " + message + " too small.");
			JOptionPane.showMessageDialog(null, "エラー #41: " +
					"設定ファイル " + message + " の内容が少なすぎます。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 42:
			System.err.println("Error #42: " + message + " too large.");
			JOptionPane.showMessageDialog(null, "エラー #41: " +
					"設定ファイル " + message + " の内容が多すぎます。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;

		case 43:
			System.err.println("Error #43: Configuration file: " +
					message + " write error.");
			JOptionPane.showMessageDialog(null, "エラー #43: " +
					"設定ファイル " + message + " の書込みに失敗しました。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		// at SomewhereNowUI
		
		case 100:
			System.err.println("Error #10:Proxy configuration not changed.");
			JOptionPane.showMessageDialog(null, "エラー #100: " +
					"設定内容を変更してから、プロキシ設定を適用する必要があります。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 101:
			System.err.println("Error #101: PIN is empty.");
			JOptionPane.showMessageDialog(null, "エラー #101: " +
					"PIN が入力されていません。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 102:
			System.err.println("Error #102: Some text box is empty.");
			JOptionPane.showMessageDialog(null, "エラー #102: " +
					"空欄があります。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 103:
			System.err.println("Error #103: Available A to Z, a to z and" +
					" 0 to 9 only.");
			JOptionPane.showMessageDialog(null, "エラー #103: " +
					"入力可能な文字は、半角英数字 (0-9, a-z, A-Z) のみです。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 104:
			System.err.println("Error #104: Text box #" + message +
					" is empty.");
			JOptionPane.showMessageDialog(null, "エラー #104: " +
					"ボタン " + message + " の入力欄が記入されていません。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 110:
			System.err.println("Error #110: Tweeting failed.");
			JOptionPane.showMessageDialog(null, "エラー #110: " +
					"ツイートに失敗しました。" +
					System.getProperty("line.separator") +
					"サービスが停止しているなどが考えられます。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 111:
			System.err.println("Error #111: Tweetter login failed.");
			JOptionPane.showMessageDialog(null, "エラー #111: " +
					"ツイッターにログインできませんでした。" +
					System.getProperty("line.separator") +
					"設定画面を開き、ログイン情報を再設定してください。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		case 112:
			System.err.println("Error #112: Tweetter post failed.");
			JOptionPane.showMessageDialog(null, "エラー #112: " +
					"ツイートできませんでした。" +
					System.getProperty("line.separator") +
					"サービスが不安定になっている場合や、二重投稿などが考えられます。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
			
		// default
			
		default:
			System.err.println("Error #" + number + ": Unknown Error.");
			JOptionPane.showMessageDialog(null, "エラー #" + number +
					": " + "原因不明のエラーです。",
					"エラー" , JOptionPane.ERROR_MESSAGE);
			break;
		}
		
		if(postClose) {
			// プログラム終了
			System.exit(1);
		}
	}
	
	/**
	 * メッセージダイアログを表示します。
	 * 
	 * @param message
	 */
	void showMessageDialog(String message) {
		JOptionPane.showMessageDialog(null, message,
				"メッセージ", JOptionPane.INFORMATION_MESSAGE);
	}
}
