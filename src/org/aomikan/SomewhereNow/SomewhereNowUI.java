package org.aomikan.SomewhereNow;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import twitter4j.*;

/**
 * SomewhereNowUI クラスは、グラフィカルユーザインタフェースを提供します。
 */
public class SomewhereNowUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	// Twitter 変数
	Twitter twitter = null;
	TwitterAccount user = null;

	
	// フレーム部品
	GridBagLayout grid = new GridBagLayout();
	JPanel rightPanel = new JPanel();
	int selectedMenu = 0;
	
	// 左列部品
	JLabel labelUserID = new JLabel();
	String[] buttonText = new String[SomewhereNow.ONETOUCH_BUTTONS];
	JButton[] buttonTweet = new JButton[SomewhereNow.ONETOUCH_BUTTONS];
	JLabel labelLastUpdate = new JLabel("TL最終更新: (なし)");
	JLabel labelStatus = new JLabel("投稿準備完了。");
	JButton buttonCustomTweet = new JButton("記入する...");
	JButton buttonChange = new JButton("設定...");
	
	// 右列部品
	Vector<String> searchItems = new Vector<String>();
	JLabel labelGroup = new JLabel();
	JComboBox comboSearch = null;
	JList statusJList = null;
	JScrollPane scrollTimeLine = null;
	JTextField textTimeLine = new JTextField();
	JPanel logPanel = new JPanel();
	
	/**
	 * SomewhereNouUI のコンストラクタです。
	 * @param user TwitterAccount のオブジェクト
	 */
	public SomewhereNowUI(TwitterAccount user) {
		
		this.user = user;
		
	
		// フレーム設定
		setTitle("SomewhereNow!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(480, 300);
		setLayout(grid);
		
		
		// ボタン初期化
		for(int i = 0; i < SomewhereNow.ONETOUCH_BUTTONS; i++) {
			buttonTweet[i] = new JButton("");
		}
		initButtonText();

		// グループタグ初期化
		setGroupTag();
		comboSearch = new JComboBox(searchItems);
		comboSearch.setEnabled(false);

		// スクロールペイン初期化
		String[] def = new String[2];
		def[0] = "リスト未更新。";
		def[1] = "ログインしてください。";
		statusJList = new JList(def);
		statusJList.setFixedCellHeight(20);
		scrollTimeLine = new JScrollPane(statusJList, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		// ログイン
		if(user.isEnabled()) {
			twitter = user.login();
			setLoginStatus();
			updateTimeLine();										// UPDATE
		} else {
			setLoginStatus();
		}
		
		// グリッド追加
		addGrid(labelUserID,		0, 0, 1, 1);
		addGrid(buttonTweet[0],		0, 1, 1, 1);
		addGrid(buttonTweet[1],		0, 2, 1, 1);
		addGrid(buttonTweet[2],		0, 3, 1, 1);
		addGrid(buttonTweet[3],		0, 4, 1, 1);
		addGrid(labelStatus,		0, 5, 1, 1);
		addGrid(labelLastUpdate,	0, 6, 1, 1);
		addGrid(buttonCustomTweet,	0, 7, 1, 1);
		addGrid(labelGroup,			1, 0, 1, 1);
		addGrid(comboSearch,		1, 1, 1, 1);
		addGrid(scrollTimeLine,		1, 2, 1, 5);
		addGrid(buttonChange,		1, 7, 1, 1);
		
		// リスナ登録
		for(int i = 0; i < SomewhereNow.ONETOUCH_BUTTONS; i++) {
			buttonTweet[i].addActionListener(this);
		}
		buttonCustomTweet.addActionListener(this);
		buttonChange.addActionListener(this);
		comboSearch.addActionListener(this);
		
		// OS が Windows ならば Windows ルックに変更
		if(System.getProperty("os.name").startsWith("Windows")) {
			try {
				UIManager.setLookAndFeel(
						"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
			} catch(Exception e) {
				// do nothing
			}
		}
		
		// 表示
		setVisible(true);
		
		// ログインされていなければ、設定パネル表示
		if(!user.isEnabled()) {
			new ConfigPanelUI();
		}
	}
	
	private void addGrid(Component com, int x, int y, int width, int height) {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = width;
		gbc.gridheight = height;
		gbc.weightx = 1;
		gbc.weighty = 1;
		grid.setConstraints(com, gbc);
		add(com);
	}

	private void setLoginStatus() {
		if(user.isEnabled()) {
			labelUserID.setText("Twitter: " + user.getUserID());
			labelUserID.setForeground(Color.BLUE);
			labelUserID.repaint();
			enableButtons(true);
		} else {
			labelUserID.setText("Twitter: (ログインしていません)");
			labelUserID.setForeground(Color.RED);
			labelUserID.repaint();
			enableButtons(false);
		}
	}
	
	/**
	 *グループ検索タグを設定・パネルへ表示します。
	 * 
	 * @return 追加されたコンボボックスのインデックス番号, なければ -1
	 */
	private int setGroupTag() {
		
		// コンボボックスにアイテム追加
		searchItems.add("Twitter");
		searchItems.add("#SomewhereNow タイムライン");
		String groupTag = user.getGroupTag();
		if(groupTag == null || groupTag.length() == 0) {
			labelGroup.setText("(グループ未設定)");
			return -1;
		} else {
			labelGroup.setText("グループ: " + user.getGroupTag());
			searchItems.add("#" + user.getGroupTag() + " タイムライン");
			return (searchItems.size() - 1);
		}
	}
	
	private void enableButtons(boolean isEnable) {
		for(int i = 0; i < SomewhereNow.ONETOUCH_BUTTONS; i++) {
			buttonTweet[i].setEnabled(isEnable);
		}
		buttonCustomTweet.setEnabled(isEnable);
	}
	
	private void initButtonText() {
		buttonText = 
			new MessageManager(user.getButtonsConfigURI()).getButtonsText();
		for(int i = 0; i < SomewhereNow.ONETOUCH_BUTTONS; i++) {
			buttonTweet[i].setText(buttonText[i]);
		}
	}
	private void setButtonText(String[] messages) {
		for(int i = 0; i < SomewhereNow.ONETOUCH_BUTTONS; i++) {
			buttonTweet[i].setText(messages[i]);
		}
		// ボタン設定を保存
		new MessageManager(user.getButtonsConfigURI()).setButtonsText(messages);
	}
	
	/**
	 * ボタン番号に割り当てられたメッセージをツイートします。
	 * 
	 * @param buttonNumber ボタン番号
	 */
	private void tweet(int buttonNumber) {
		String groupTag = user.getGroupTag();
		if(groupTag == null || groupTag.length() == 0) {
			groupTag = "(グループ未設定) #SomewhereNow";
		} else {
			groupTag = "(グループ: #" + groupTag + ") #SomewhereNow";
		}
		
		try {
			twitter.updateStatus(buttonText[buttonNumber] + 
					" " + groupTag);
			labelStatus.setText("投稿完了: " + buttonText[buttonNumber]);		// temp
			updateTimeLine();										// UPDATE
		} catch(TwitterException e) {
			if(e.getStatusCode() == 401) {
				e.printStackTrace();
				new DialogManager().showErrorDialog(111, null, false);
			} else if(e.getStatusCode() == 403) {
				e.printStackTrace();
				new DialogManager().showErrorDialog(112, null, false);
			} else {
				e.printStackTrace();
				new DialogManager().showErrorDialog(110, null, false);
			}
		}
	}
	private void tweet(String message) {
		try {
			twitter.updateStatus(message);
			labelStatus.setText("投稿完了: " + message);		// temp
			updateTimeLine();
		} catch(TwitterException e) {
			if(e.getStatusCode() == 401) {
				e.printStackTrace();
				new DialogManager().showErrorDialog(111, null, false);
			} else {
				e.printStackTrace();
				new DialogManager().showErrorDialog(110, null, false);
			}
		}
	}
		
	public void updateTimeLine() {
		
		ResponseList<Status> statusList;
		String[] statusArr;
		SimpleDateFormat formatter = new SimpleDateFormat("yy/MM/dd HH:mm");
		
		switch(selectedMenu) {
		
		// Friend タイムライン更新
		case 0:
			try {
				statusList = twitter.getFriendsTimeline();
				statusArr = new String[statusList.size()];
				for (int i = 0; i < statusList.size(); i++) {
					statusArr[i] = formatter.format(
							statusList.get(i).getCreatedAt()) +
							" (" + statusList.get(i).getUser().getScreenName()
							+ ") " + statusList.get(i).getText();
		        }
				statusJList = new JList(statusArr);
				statusJList.setFixedCellHeight(20);
				scrollTimeLine = new JScrollPane(statusJList, 
						JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
						JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			} catch (TwitterException e) {
				//
				e.printStackTrace();
			}
			break;
			
		// #somewhereNow 検索更新
		case 1:
			
			break;
		
		// グループ検索更新
		case 2:
			break;
			
		// バグ
		default:
			return;
		}
		labelLastUpdate.setText("TL最終更新: " + formatter.format(new Date()));
	}
	
	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		
		// 設定ボタン
		if(source.equals(buttonChange)) {
			new ConfigPanelUI();
		}
		
		// ワンタッチツイートボタン
		else if(source.equals(buttonTweet[0])) {
			tweet(0);
		} else if(source.equals(buttonTweet[1])) {
			tweet(1);
		} else if(source.equals(buttonTweet[2])) {
			tweet(2);
		} else if(source.equals(buttonTweet[3])) {
			tweet(3);
		}
		
		// カスタムツイートボタン
		else if(source.equals(buttonCustomTweet)) {
			updateTimeLine();
			String message = JOptionPane.showInputDialog(
					this, "本文を 140 文字以内で入力してください。");
			if(message == null) {
				//
			} else {
				tweet(message);
			}
		}
		
		// コンボボックス
		else if(source.equals(comboSearch)) {
			selectedMenu = comboSearch.getSelectedIndex();
		}
	}
	
	/**
	 * MessageInputUI 内部クラスは、カスタムツイート記入画面を生成・表示します。
	 */
	class MessageInputUI extends JFrame implements ActionListener {
		private static final long serialVersionUID = 1L;

		public MessageInputUI() {
			
			// フレーム設定
			setTitle("ツイート入力画面");
			setSize(400, 300);
			
			
			// リスナ登録
			
			// OS が Windows ならば Windows ルックに変更
			if(System.getProperty("os.name").startsWith("Windows")) {
				try {
					UIManager.setLookAndFeel(
							"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					SwingUtilities.updateComponentTreeUI(this);
					//pack();
				} catch(Exception e) {
					// do nothing
				}
			}
			
			// 表示
			setVisible(true);
		}
		
		public void actionPerformed(ActionEvent arg0) {
			
			
		}
	}
	
	/**
	 * ConfigPanelUI 内部クラスは、設定画面を生成・表示します。
	 *
	 */
	class ConfigPanelUI extends JFrame implements ActionListener {
		private static final long serialVersionUID = 1L;
		PropertyManager prop = null;
		
		// フレーム設定
		JTabbedPane tabPane = new JTabbedPane();
		JPanel panelLogin 	= new JPanel();
		JPanel panelOAuth 	= new JPanel();
		JPanel panelButtons = new JPanel();
		JPanel panelGroup 	= new JPanel();
		JPanel panelProxy 	= new JPanel();
		JPanel panelAbout 	= new JPanel();
		
		// "ログイン" タブ部品
		JButton buttonDone          = new JButton("OK");
		JTextField textUserID       = new JTextField(20);
		JPasswordField textPassword = new JPasswordField(20);
		JButton buttonLogin         = new JButton("ログイン");
		JButton buttonSwitchPBE     = new JButton("OAuth 認証に切り替える...");
		JButton buttonSwitchOAuth   = new JButton("パスワード認証に切り替える...");
		JButton buttonOpenURL       = new JButton("ブラウザを起動し PIN 取得...");
		JTextField textPin          = new JTextField(20);
		JButton buttonLoginOAuth    = new JButton("ログイン");
		
		// "ボタン設定" タブ部品
		JTextField[] textButton		= new JTextField[SomewhereNow.ONETOUCH_BUTTONS];
		JButton buttonButtonsDone	= new JButton("適用");
		
		// "グループ設定" タブ部品
		JTextField textGroup		= new JTextField(20);
		JButton buttonGroupDone		= new JButton("適用");
		JButton buttonGroupClear	= new JButton("未設定に戻す");
		
		// "プロキシ設定" タブ部品
		JRadioButton radioProxyOn   = new JRadioButton("ON");
		JRadioButton radioProxyOff  = new JRadioButton("OFF");
		JTextField textProxyServer	= new JTextField(20);
		JTextField textProxyPort	= new JTextField(5);
		JButton buttonProxyDone		= new JButton("適用");
		boolean isProxyChanged		= false;
		
		// "このアプリについて" タブ部品
		JLabel labelAbout			= null;
		JLabel labelAuthor			= null;
		JMenuItem labelURL			= null;
		
		/**
		 * ConfigPanelUI を初期化して表示します。
		 */
		public ConfigPanelUI() {
			
			// フレーム設定
			setTitle("SomewhereNow! 設定");
			setSize(400, 300);
			add(tabPane);
			
			// タブペイン設定
			if(user.isOAuth()) {
				tabPane.add("ログイン", panelOAuth);
			} else {
				tabPane.add("ログイン", panelLogin);
			}
			tabPane.add("ボタン設定", panelButtons);
			tabPane.add("グループ設定", panelGroup);
			tabPane.add("プロキシ設定", panelProxy);
			tabPane.add("このアプリについて", panelAbout);
			tabPane.setSize(400, 200);
			getContentPane().add(tabPane);
			
			// "ログイン" タブ (PBE)
			panelLogin.setLayout(new BoxLayout(panelLogin, BoxLayout.Y_AXIS));
			spacer(panelLogin);
			if(user.isEnabled()) {
				panelLogin.add(new JLabel("ユーザを " + user.getUserID() +
						" から変更する場合は、以下を入力してください。"));
			} else {
				panelLogin.add(new JLabel("Twitter のユーザ ID とパスワードを" +
						"入力してください。"));
			}
			spacer(panelLogin);
			panelLogin.add(new JLabel("ID: "));
			panelLogin.add(textUserID);
			spacer(panelLogin);
			panelLogin.add(new JLabel("PW: "));
			panelLogin.add(textPassword);
			spacer(panelLogin);
			panelLogin.add(buttonLogin);
			spacer(panelLogin);
			panelLogin.add(buttonSwitchPBE);
			
			for(int i = 0; i < 4; i++) {
				spacer(panelLogin);
			}
			
			// "ログイン" タブ (OAuth)
			panelOAuth.setLayout(new BoxLayout(panelOAuth, BoxLayout.Y_AXIS));
			spacer(panelOAuth);
			if(user.isEnabled()) {
				panelOAuth.add(new JLabel("ユーザを " + user.getUserID() +
						" から変更する場合は、以下を設定し直してください。"));
			} else {
				panelOAuth.add(new JLabel("下のボタンから PIN (番号) を取得して" +
						"入力し、ログインしてください。"));
			}
			spacer(panelOAuth);
			panelOAuth.add(buttonOpenURL);
			spacer(panelOAuth);
			panelOAuth.add(new JLabel("PIN: "));
			textPin.setFont(new Font(Font.SERIF, Font.BOLD, 14));
			panelOAuth.add(textPin);
			spacer(panelOAuth);
			panelOAuth.add(buttonLoginOAuth);
			spacer(panelOAuth);
			panelOAuth.add(buttonSwitchOAuth);
			
			for(int i = 0; i < 4; i++) {
				spacer(panelOAuth);
			}
			
			// "ボタン設定" タブ
			panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.Y_AXIS));
			spacer(panelButtons);
			String[] messages = new MessageManager().getButtonsText();
			for(int i = 0; i < SomewhereNow.ONETOUCH_BUTTONS; i++) {
				textButton[i] = new JTextField(20);
				textButton[i].setText(messages[i]);
				panelButtons.add(new JLabel("ボタン" + (i + 1) + ": "));
				panelButtons.add(textButton[i]);
			}
			spacer(panelButtons);
			panelButtons.add(buttonButtonsDone);
			
			for(int i = 0; i < 2; i++) {
				spacer(panelButtons);
			}
			
			// "グループ設定" タブ
			panelGroup.setLayout(new BoxLayout(panelGroup, BoxLayout.Y_AXIS));
			spacer(panelGroup);
			panelGroup.add(new JLabel("グループを設定により、同グループの検索を容易に" +
					"します。"));
			spacer(panelGroup);
			String group = user.getGroupTag();
			textGroup.setText(group);
			panelGroup.add(new JLabel("グループ名 (英数字のみ): "));
			panelGroup.add(textGroup);
			panelGroup.add(new JLabel("(入力例: AomikanNetwork)"));
			spacer(panelGroup);
			panelGroup.add(buttonGroupDone);
			spacer(panelGroup);
			panelGroup.add(buttonGroupClear);
			
			for(int i = 0; i < 6; i++) {
				spacer(panelGroup);
			}
			
			
			// "プロキシ設定" タブ
			panelProxy.setLayout(new BoxLayout(panelProxy, BoxLayout.Y_AXIS));
			spacer(panelProxy);
			ButtonGroup bg = new ButtonGroup();
			bg.add(radioProxyOn);
			bg.add(radioProxyOff);
			panelProxy.add(new JLabel("プロキシを使用する"));
			panelProxy.add(radioProxyOn);
			panelProxy.add(radioProxyOff);
			spacer(panelProxy);
			panelProxy.add(new JLabel("サーバ: "));
			panelProxy.add(textProxyServer);
			spacer(panelProxy);
			panelProxy.add(new JLabel("ポート番号: "));
			panelProxy.add(textProxyPort);
			spacer(panelProxy);
			panelProxy.add(buttonProxyDone);
			
			String[] proxyConfig = user.getProxySettings();
			textProxyServer.setText(proxyConfig[0]);
			textProxyPort.setText(proxyConfig[1]);
			if(user.isProxyEnabled()) {
				radioProxyOn.setSelected(true);
			}else {
				radioProxyOff.setSelected(true);
				textProxyServer.setEnabled(false);
				textProxyPort.setEnabled(false);
			}
			for(int i = 0; i < 3; i++) {
				spacer(panelProxy);
			}
			
			// "このアプリについて" タブ
			panelAbout.setLayout(new BoxLayout(panelAbout, BoxLayout.Y_AXIS));
			spacer(panelAbout);
			labelAbout = new JLabel("   " + SomewhereNow.APPLICATION_NAME +
					"  Version: " + SomewhereNow.VERSION);
			labelAbout.setFont(new Font(Font.SERIF, Font.BOLD, 24));
			panelAbout.add(labelAbout);
			labelAuthor = new JLabel("Copyright(c) 2010 Flying Mikan @" +
					" Aomikan Network");
			spacer(panelAbout);
			labelAuthor.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
			panelAbout.add(labelAuthor);
			spacer(panelAbout);
			labelURL = new JMenuItem(SomewhereNow.URL);
			labelURL.setForeground(Color.BLUE);
			panelAbout.add(labelURL);
			for(int i = 0; i < 3; i++) {
				spacer(panelAbout);
			}
			
			// リスナ登録
			buttonLogin.addActionListener(this);
			buttonSwitchPBE.addActionListener(this);
			buttonSwitchOAuth.addActionListener(this);
			buttonButtonsDone.addActionListener(this);
			buttonGroupDone.addActionListener(this);
			buttonGroupClear.addActionListener(this);
			buttonLoginOAuth.addActionListener(this);
			radioProxyOn.addActionListener(this);
			radioProxyOff.addActionListener(this);
			buttonProxyDone.addActionListener(this);
			labelURL.addActionListener(this);
			buttonOpenURL.addActionListener(this);
			
			// OS が Windows ならば Windows ルックに変更
			if(System.getProperty("os.name").startsWith("Windows")) {
				try {
					UIManager.setLookAndFeel(
							"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
					SwingUtilities.updateComponentTreeUI(this);
					//pack();
				} catch(Exception e) {
					// do nothing
				}
			}
			
			// 表示
			setVisible(true);
		}
		
		/**
		 * ConfigPanelUI クラスのアクションリスナです。
		 */
		public void actionPerformed(ActionEvent ae) {
			Object source = ae.getSource();
			
			// PBE ログイン
			if(source.equals(buttonLogin)) {
				if(textUserID.getText().length() == 0 ||
						textPassword.getPassword().length == 0) {
					new DialogManager().showErrorDialog(102, null, false);
				}else if(textUserID.getText().length() < 3) {
					JOptionPane.showMessageDialog(null, "ユーザ ID が短すぎます。");
				} else {
					twitter = user.login(
							textUserID.getText(),
							new String(textPassword.getPassword()));
					try {
						if(twitter.test()) {
							user.setEnable(true);
							setLoginStatus();
							close();
						} else {
							JOptionPane.showMessageDialog(null, 
									"Twitter に接続できません。");
						}
					} catch (TwitterException e) {
						JOptionPane.showMessageDialog(null, 
								"Twitter にログインできません。");
					}
				}
			} else if(source.equals(buttonSwitchPBE) ||
					ae.getSource().equals(buttonSwitchOAuth)) {

				int answer;
				
				// OAuth 有効の場合、PBE に変更
				if(user.isOAuth()) {
					answer = JOptionPane.showConfirmDialog(null,
						"OAuth を無効にし、パスワード認証に切り替えます。" +
                        System.getProperty("line.separator") +
                        "OAuth の認証情報は削除されます。" +
                        "再び有効化するには、再登録してください。",
                        "PBE/OAuth 切り替え確認",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
					if(answer == JOptionPane.YES_OPTION) {
						
						// OAuth 無効化
						user.setOAuth(false);
						new ConfigPanelUI();
						close();
						
					} else {
						// Do nothing
					}
				}
				
				// PBE 有効の場合、OAuth に変更
				else {
					answer = JOptionPane.showConfirmDialog(null,
						"パスワード認証を無効にし、 OAuth に切り替えます。" +
                        System.getProperty("line.separator") +
                        "保存されている暗号化パスワードは削除されます。" +
                        "再び有効化するには、再登録してください。",
                        "PBE/OAuth 切り替え確認",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
					if(answer == JOptionPane.YES_OPTION) {
						
						// OAuth 有効化
						user.setOAuth(true);
						user.setEnable(true);
						new ConfigPanelUI();
						close();
						
					} else {
						// Do nothing
					}
				}
			} else if(source.equals(buttonOpenURL)) {
				twitter = user.login(null, null);
				
				// ブラウザを開いてリクエストトークン取得ページへ誘導
				openDefaultBrowser(user.getRequestURL());
			}
			
			// OAuth ログイン
			else if(source.equals(buttonLoginOAuth)) {
				if(textPin.getText().length() == 0) {
					new DialogManager().showErrorDialog(101, null, false);
				} else {
					twitter = user.initOAuth(twitter, textPin.getText());
					user.setEnable(true);
					setLoginStatus();
					close();
				}
			}
			
			// ボタン設定適用
			else if(source.equals(buttonButtonsDone)) {
				String[] settings = new String[SomewhereNow.ONETOUCH_BUTTONS];
				for(int i = 0; i < SomewhereNow.ONETOUCH_BUTTONS; i++) {
					String text = textButton[i].getText();
					if(text.length() == 0) {
						new DialogManager().showErrorDialog(
								104, Integer.toString(i + 1), false);
						return;
					} else {
						settings[i] = text;
					}
				}
				setButtonText(settings);
				close();
			}
			
			// グループ設定適用
			else if(source.equals(buttonGroupDone)) {
				String group = textGroup.getText();
				
				// 空 (無効化) 確認
				if(group == null || group.length() == 0) {
					user.setGroupTag("");
					setGroupTag();
					close();
				}
				
				// 半角英数チェック
				else if(!group.matches("[0-9a-zA-Z]+")) {
					textGroup.setText("");
					new DialogManager().showErrorDialog(103, null, false);
				}
				// 続行
				else {
					user.setGroupTag(group);
					setGroupTag();
					close();
				}
			}

			// グループ設定クリア
			else if(source.equals(buttonGroupClear)) {
				textGroup.setText("");
				user.setGroupTag("");
				setGroupTag();
				close();
			}
			
			// プロキシ有効化
			else if(source.equals(radioProxyOn)) {
				isProxyChanged = true;
				textProxyServer.setEnabled(true);
				textProxyPort.setEnabled(true);
			}
			
			// プロキシ無効化
			else if(source.equals(radioProxyOff)) {
				isProxyChanged = true;
				textProxyServer.setEnabled(false);
				textProxyPort.setEnabled(false);
			}
			
			// プロキシ適用
			else if(source.equals(buttonProxyDone)) {
				
				// 現在の設定を取得
				String[] prevProxy = new String[2];
				prevProxy = user.getProxySettings();
				if(!prevProxy[0].equals(textProxyServer.getText()) ||
						!prevProxy[1].equals(textProxyPort.getText())) {
					isProxyChanged = true;
				}
				
				// プロキシサーバ情報を取得
				String[] target = new String[2];
				target[0] = textProxyServer.getText();
				target[1] = textProxyPort.getText();

				// プロキシサーバ情報を保存
				user.setProxySettings(target);
				
				// 有効/無効状態を保存
				user.enableProxy(radioProxyOn.isSelected());
				
				// メッセージダイアログ表示
				if(isProxyChanged) {
					JOptionPane.showMessageDialog(null, "プロキシ設定を保存しました。" +
							System.getProperty("line.separator") +
							"適用するには、本プログラムを起動しなおす必要があります。" +
							System.getProperty("line.separator") +
							"OK をクリックすると終了します。",
							"プロキシ設定" , JOptionPane.INFORMATION_MESSAGE);
					System.exit(0);
				} else {
					isProxyChanged = false;
					new DialogManager().showErrorDialog(100, null, false);
				}
			}
			
			// URL
			else if(source.equals(labelURL)) {
				openDefaultBrowser(SomewhereNow.URL);
			}
		}
		
		public void close() {
			super.dispose();
		}
		
		private void spacer(JPanel panel) {
			panel.add(new JLabel(" "));
		}
		
		private void openDefaultBrowser(String url) {
			if(url == null) {
				url = "about:blank";
			}
			
			Desktop desktop = Desktop.getDesktop();
			try {
				desktop.browse(new URI(url));
			} catch (IOException e) {
				
				e.printStackTrace();
			} catch (URISyntaxException e) {
				
				e.printStackTrace();
			}
		}
	}
	
}
