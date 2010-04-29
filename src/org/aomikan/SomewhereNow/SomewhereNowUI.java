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
 * SomewhereNowUI �N���X�́A�O���t�B�J�����[�U�C���^�t�F�[�X��񋟂��܂��B
 */
public class SomewhereNowUI extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	
	// Twitter �ϐ�
	Twitter twitter = null;
	TwitterAccount user = null;

	
	// �t���[�����i
	GridBagLayout grid = new GridBagLayout();
	JPanel rightPanel = new JPanel();
	int selectedMenu = 0;
	
	// ���񕔕i
	JLabel labelUserID = new JLabel();
	String[] buttonText = new String[SomewhereNow.ONETOUCH_BUTTONS];
	JButton[] buttonTweet = new JButton[SomewhereNow.ONETOUCH_BUTTONS];
	JLabel labelLastUpdate = new JLabel("TL�ŏI�X�V: (�Ȃ�)");
	JLabel labelStatus = new JLabel("���e���������B");
	JButton buttonCustomTweet = new JButton("�L������...");
	JButton buttonChange = new JButton("�ݒ�...");
	
	// �E�񕔕i
	Vector<String> searchItems = new Vector<String>();
	JLabel labelGroup = new JLabel();
	JComboBox comboSearch = null;
	JList statusJList = null;
	JScrollPane scrollTimeLine = null;
	JTextField textTimeLine = new JTextField();
	JPanel logPanel = new JPanel();
	
	/**
	 * SomewhereNouUI �̃R���X�g���N�^�ł��B
	 * @param user TwitterAccount �̃I�u�W�F�N�g
	 */
	public SomewhereNowUI(TwitterAccount user) {
		
		this.user = user;
		
	
		// �t���[���ݒ�
		setTitle("SomewhereNow!");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(480, 300);
		setLayout(grid);
		
		
		// �{�^��������
		for(int i = 0; i < SomewhereNow.ONETOUCH_BUTTONS; i++) {
			buttonTweet[i] = new JButton("");
		}
		initButtonText();

		// �O���[�v�^�O������
		setGroupTag();
		comboSearch = new JComboBox(searchItems);
		comboSearch.setEnabled(false);

		// �X�N���[���y�C��������
		String[] def = new String[2];
		def[0] = "���X�g���X�V�B";
		def[1] = "���O�C�����Ă��������B";
		statusJList = new JList(def);
		statusJList.setFixedCellHeight(20);
		scrollTimeLine = new JScrollPane(statusJList, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
				JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		
		// ���O�C��
		if(user.isEnabled()) {
			twitter = user.login();
			setLoginStatus();
			updateTimeLine();										// UPDATE
		} else {
			setLoginStatus();
		}
		
		// �O���b�h�ǉ�
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
		
		// ���X�i�o�^
		for(int i = 0; i < SomewhereNow.ONETOUCH_BUTTONS; i++) {
			buttonTweet[i].addActionListener(this);
		}
		buttonCustomTweet.addActionListener(this);
		buttonChange.addActionListener(this);
		comboSearch.addActionListener(this);
		
		// OS �� Windows �Ȃ�� Windows ���b�N�ɕύX
		if(System.getProperty("os.name").startsWith("Windows")) {
			try {
				UIManager.setLookAndFeel(
						"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				SwingUtilities.updateComponentTreeUI(this);
			} catch(Exception e) {
				// do nothing
			}
		}
		
		// �\��
		setVisible(true);
		
		// ���O�C������Ă��Ȃ���΁A�ݒ�p�l���\��
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
			labelUserID.setText("Twitter: (���O�C�����Ă��܂���)");
			labelUserID.setForeground(Color.RED);
			labelUserID.repaint();
			enableButtons(false);
		}
	}
	
	/**
	 *�O���[�v�����^�O��ݒ�E�p�l���֕\�����܂��B
	 * 
	 * @return �ǉ����ꂽ�R���{�{�b�N�X�̃C���f�b�N�X�ԍ�, �Ȃ���� -1
	 */
	private int setGroupTag() {
		
		// �R���{�{�b�N�X�ɃA�C�e���ǉ�
		searchItems.add("Twitter");
		searchItems.add("#SomewhereNow �^�C�����C��");
		String groupTag = user.getGroupTag();
		if(groupTag == null || groupTag.length() == 0) {
			labelGroup.setText("(�O���[�v���ݒ�)");
			return -1;
		} else {
			labelGroup.setText("�O���[�v: " + user.getGroupTag());
			searchItems.add("#" + user.getGroupTag() + " �^�C�����C��");
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
		// �{�^���ݒ��ۑ�
		new MessageManager(user.getButtonsConfigURI()).setButtonsText(messages);
	}
	
	/**
	 * �{�^���ԍ��Ɋ��蓖�Ă�ꂽ���b�Z�[�W���c�C�[�g���܂��B
	 * 
	 * @param buttonNumber �{�^���ԍ�
	 */
	private void tweet(int buttonNumber) {
		String groupTag = user.getGroupTag();
		if(groupTag == null || groupTag.length() == 0) {
			groupTag = "(�O���[�v���ݒ�) #SomewhereNow";
		} else {
			groupTag = "(�O���[�v: #" + groupTag + ") #SomewhereNow";
		}
		
		try {
			twitter.updateStatus(buttonText[buttonNumber] + 
					" " + groupTag);
			labelStatus.setText("���e����: " + buttonText[buttonNumber]);		// temp
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
			labelStatus.setText("���e����: " + message);		// temp
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
		
		// Friend �^�C�����C���X�V
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
			
		// #somewhereNow �����X�V
		case 1:
			
			break;
		
		// �O���[�v�����X�V
		case 2:
			break;
			
		// �o�O
		default:
			return;
		}
		labelLastUpdate.setText("TL�ŏI�X�V: " + formatter.format(new Date()));
	}
	
	public void actionPerformed(ActionEvent ae) {
		Object source = ae.getSource();
		
		// �ݒ�{�^��
		if(source.equals(buttonChange)) {
			new ConfigPanelUI();
		}
		
		// �����^�b�`�c�C�[�g�{�^��
		else if(source.equals(buttonTweet[0])) {
			tweet(0);
		} else if(source.equals(buttonTweet[1])) {
			tweet(1);
		} else if(source.equals(buttonTweet[2])) {
			tweet(2);
		} else if(source.equals(buttonTweet[3])) {
			tweet(3);
		}
		
		// �J�X�^���c�C�[�g�{�^��
		else if(source.equals(buttonCustomTweet)) {
			updateTimeLine();
			String message = JOptionPane.showInputDialog(
					this, "�{���� 140 �����ȓ��œ��͂��Ă��������B");
			if(message == null) {
				//
			} else {
				tweet(message);
			}
		}
		
		// �R���{�{�b�N�X
		else if(source.equals(comboSearch)) {
			selectedMenu = comboSearch.getSelectedIndex();
		}
	}
	
	/**
	 * MessageInputUI �����N���X�́A�J�X�^���c�C�[�g�L����ʂ𐶐��E�\�����܂��B
	 */
	class MessageInputUI extends JFrame implements ActionListener {
		private static final long serialVersionUID = 1L;

		public MessageInputUI() {
			
			// �t���[���ݒ�
			setTitle("�c�C�[�g���͉��");
			setSize(400, 300);
			
			
			// ���X�i�o�^
			
			// OS �� Windows �Ȃ�� Windows ���b�N�ɕύX
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
			
			// �\��
			setVisible(true);
		}
		
		public void actionPerformed(ActionEvent arg0) {
			
			
		}
	}
	
	/**
	 * ConfigPanelUI �����N���X�́A�ݒ��ʂ𐶐��E�\�����܂��B
	 *
	 */
	class ConfigPanelUI extends JFrame implements ActionListener {
		private static final long serialVersionUID = 1L;
		PropertyManager prop = null;
		
		// �t���[���ݒ�
		JTabbedPane tabPane = new JTabbedPane();
		JPanel panelLogin 	= new JPanel();
		JPanel panelOAuth 	= new JPanel();
		JPanel panelButtons = new JPanel();
		JPanel panelGroup 	= new JPanel();
		JPanel panelProxy 	= new JPanel();
		JPanel panelAbout 	= new JPanel();
		
		// "���O�C��" �^�u���i
		JButton buttonDone          = new JButton("OK");
		JTextField textUserID       = new JTextField(20);
		JPasswordField textPassword = new JPasswordField(20);
		JButton buttonLogin         = new JButton("���O�C��");
		JButton buttonSwitchPBE     = new JButton("OAuth �F�؂ɐ؂�ւ���...");
		JButton buttonSwitchOAuth   = new JButton("�p�X���[�h�F�؂ɐ؂�ւ���...");
		JButton buttonOpenURL       = new JButton("�u���E�U���N���� PIN �擾...");
		JTextField textPin          = new JTextField(20);
		JButton buttonLoginOAuth    = new JButton("���O�C��");
		
		// "�{�^���ݒ�" �^�u���i
		JTextField[] textButton		= new JTextField[SomewhereNow.ONETOUCH_BUTTONS];
		JButton buttonButtonsDone	= new JButton("�K�p");
		
		// "�O���[�v�ݒ�" �^�u���i
		JTextField textGroup		= new JTextField(20);
		JButton buttonGroupDone		= new JButton("�K�p");
		JButton buttonGroupClear	= new JButton("���ݒ�ɖ߂�");
		
		// "�v���L�V�ݒ�" �^�u���i
		JRadioButton radioProxyOn   = new JRadioButton("ON");
		JRadioButton radioProxyOff  = new JRadioButton("OFF");
		JTextField textProxyServer	= new JTextField(20);
		JTextField textProxyPort	= new JTextField(5);
		JButton buttonProxyDone		= new JButton("�K�p");
		boolean isProxyChanged		= false;
		
		// "���̃A�v���ɂ���" �^�u���i
		JLabel labelAbout			= null;
		JLabel labelAuthor			= null;
		JMenuItem labelURL			= null;
		
		/**
		 * ConfigPanelUI �����������ĕ\�����܂��B
		 */
		public ConfigPanelUI() {
			
			// �t���[���ݒ�
			setTitle("SomewhereNow! �ݒ�");
			setSize(400, 300);
			add(tabPane);
			
			// �^�u�y�C���ݒ�
			if(user.isOAuth()) {
				tabPane.add("���O�C��", panelOAuth);
			} else {
				tabPane.add("���O�C��", panelLogin);
			}
			tabPane.add("�{�^���ݒ�", panelButtons);
			tabPane.add("�O���[�v�ݒ�", panelGroup);
			tabPane.add("�v���L�V�ݒ�", panelProxy);
			tabPane.add("���̃A�v���ɂ���", panelAbout);
			tabPane.setSize(400, 200);
			getContentPane().add(tabPane);
			
			// "���O�C��" �^�u (PBE)
			panelLogin.setLayout(new BoxLayout(panelLogin, BoxLayout.Y_AXIS));
			spacer(panelLogin);
			if(user.isEnabled()) {
				panelLogin.add(new JLabel("���[�U�� " + user.getUserID() +
						" ����ύX����ꍇ�́A�ȉ�����͂��Ă��������B"));
			} else {
				panelLogin.add(new JLabel("Twitter �̃��[�U ID �ƃp�X���[�h��" +
						"���͂��Ă��������B"));
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
			
			// "���O�C��" �^�u (OAuth)
			panelOAuth.setLayout(new BoxLayout(panelOAuth, BoxLayout.Y_AXIS));
			spacer(panelOAuth);
			if(user.isEnabled()) {
				panelOAuth.add(new JLabel("���[�U�� " + user.getUserID() +
						" ����ύX����ꍇ�́A�ȉ���ݒ肵�����Ă��������B"));
			} else {
				panelOAuth.add(new JLabel("���̃{�^������ PIN (�ԍ�) ���擾����" +
						"���͂��A���O�C�����Ă��������B"));
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
			
			// "�{�^���ݒ�" �^�u
			panelButtons.setLayout(new BoxLayout(panelButtons, BoxLayout.Y_AXIS));
			spacer(panelButtons);
			String[] messages = new MessageManager().getButtonsText();
			for(int i = 0; i < SomewhereNow.ONETOUCH_BUTTONS; i++) {
				textButton[i] = new JTextField(20);
				textButton[i].setText(messages[i]);
				panelButtons.add(new JLabel("�{�^��" + (i + 1) + ": "));
				panelButtons.add(textButton[i]);
			}
			spacer(panelButtons);
			panelButtons.add(buttonButtonsDone);
			
			for(int i = 0; i < 2; i++) {
				spacer(panelButtons);
			}
			
			// "�O���[�v�ݒ�" �^�u
			panelGroup.setLayout(new BoxLayout(panelGroup, BoxLayout.Y_AXIS));
			spacer(panelGroup);
			panelGroup.add(new JLabel("�O���[�v��ݒ�ɂ��A���O���[�v�̌�����e�Ղ�" +
					"���܂��B"));
			spacer(panelGroup);
			String group = user.getGroupTag();
			textGroup.setText(group);
			panelGroup.add(new JLabel("�O���[�v�� (�p�����̂�): "));
			panelGroup.add(textGroup);
			panelGroup.add(new JLabel("(���͗�: AomikanNetwork)"));
			spacer(panelGroup);
			panelGroup.add(buttonGroupDone);
			spacer(panelGroup);
			panelGroup.add(buttonGroupClear);
			
			for(int i = 0; i < 6; i++) {
				spacer(panelGroup);
			}
			
			
			// "�v���L�V�ݒ�" �^�u
			panelProxy.setLayout(new BoxLayout(panelProxy, BoxLayout.Y_AXIS));
			spacer(panelProxy);
			ButtonGroup bg = new ButtonGroup();
			bg.add(radioProxyOn);
			bg.add(radioProxyOff);
			panelProxy.add(new JLabel("�v���L�V���g�p����"));
			panelProxy.add(radioProxyOn);
			panelProxy.add(radioProxyOff);
			spacer(panelProxy);
			panelProxy.add(new JLabel("�T�[�o: "));
			panelProxy.add(textProxyServer);
			spacer(panelProxy);
			panelProxy.add(new JLabel("�|�[�g�ԍ�: "));
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
			
			// "���̃A�v���ɂ���" �^�u
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
			
			// ���X�i�o�^
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
			
			// OS �� Windows �Ȃ�� Windows ���b�N�ɕύX
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
			
			// �\��
			setVisible(true);
		}
		
		/**
		 * ConfigPanelUI �N���X�̃A�N�V�������X�i�ł��B
		 */
		public void actionPerformed(ActionEvent ae) {
			Object source = ae.getSource();
			
			// PBE ���O�C��
			if(source.equals(buttonLogin)) {
				if(textUserID.getText().length() == 0 ||
						textPassword.getPassword().length == 0) {
					new DialogManager().showErrorDialog(102, null, false);
				}else if(textUserID.getText().length() < 3) {
					JOptionPane.showMessageDialog(null, "���[�U ID ���Z�����܂��B");
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
									"Twitter �ɐڑ��ł��܂���B");
						}
					} catch (TwitterException e) {
						JOptionPane.showMessageDialog(null, 
								"Twitter �Ƀ��O�C���ł��܂���B");
					}
				}
			} else if(source.equals(buttonSwitchPBE) ||
					ae.getSource().equals(buttonSwitchOAuth)) {

				int answer;
				
				// OAuth �L���̏ꍇ�APBE �ɕύX
				if(user.isOAuth()) {
					answer = JOptionPane.showConfirmDialog(null,
						"OAuth �𖳌��ɂ��A�p�X���[�h�F�؂ɐ؂�ւ��܂��B" +
                        System.getProperty("line.separator") +
                        "OAuth �̔F�؏��͍폜����܂��B" +
                        "�ĂїL��������ɂ́A�ēo�^���Ă��������B",
                        "PBE/OAuth �؂�ւ��m�F",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
					if(answer == JOptionPane.YES_OPTION) {
						
						// OAuth ������
						user.setOAuth(false);
						new ConfigPanelUI();
						close();
						
					} else {
						// Do nothing
					}
				}
				
				// PBE �L���̏ꍇ�AOAuth �ɕύX
				else {
					answer = JOptionPane.showConfirmDialog(null,
						"�p�X���[�h�F�؂𖳌��ɂ��A OAuth �ɐ؂�ւ��܂��B" +
                        System.getProperty("line.separator") +
                        "�ۑ�����Ă���Í����p�X���[�h�͍폜����܂��B" +
                        "�ĂїL��������ɂ́A�ēo�^���Ă��������B",
                        "PBE/OAuth �؂�ւ��m�F",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE);
					if(answer == JOptionPane.YES_OPTION) {
						
						// OAuth �L����
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
				
				// �u���E�U���J���ă��N�G�X�g�g�[�N���擾�y�[�W�֗U��
				openDefaultBrowser(user.getRequestURL());
			}
			
			// OAuth ���O�C��
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
			
			// �{�^���ݒ�K�p
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
			
			// �O���[�v�ݒ�K�p
			else if(source.equals(buttonGroupDone)) {
				String group = textGroup.getText();
				
				// �� (������) �m�F
				if(group == null || group.length() == 0) {
					user.setGroupTag("");
					setGroupTag();
					close();
				}
				
				// ���p�p���`�F�b�N
				else if(!group.matches("[0-9a-zA-Z]+")) {
					textGroup.setText("");
					new DialogManager().showErrorDialog(103, null, false);
				}
				// ���s
				else {
					user.setGroupTag(group);
					setGroupTag();
					close();
				}
			}

			// �O���[�v�ݒ�N���A
			else if(source.equals(buttonGroupClear)) {
				textGroup.setText("");
				user.setGroupTag("");
				setGroupTag();
				close();
			}
			
			// �v���L�V�L����
			else if(source.equals(radioProxyOn)) {
				isProxyChanged = true;
				textProxyServer.setEnabled(true);
				textProxyPort.setEnabled(true);
			}
			
			// �v���L�V������
			else if(source.equals(radioProxyOff)) {
				isProxyChanged = true;
				textProxyServer.setEnabled(false);
				textProxyPort.setEnabled(false);
			}
			
			// �v���L�V�K�p
			else if(source.equals(buttonProxyDone)) {
				
				// ���݂̐ݒ���擾
				String[] prevProxy = new String[2];
				prevProxy = user.getProxySettings();
				if(!prevProxy[0].equals(textProxyServer.getText()) ||
						!prevProxy[1].equals(textProxyPort.getText())) {
					isProxyChanged = true;
				}
				
				// �v���L�V�T�[�o�����擾
				String[] target = new String[2];
				target[0] = textProxyServer.getText();
				target[1] = textProxyPort.getText();

				// �v���L�V�T�[�o����ۑ�
				user.setProxySettings(target);
				
				// �L��/������Ԃ�ۑ�
				user.enableProxy(radioProxyOn.isSelected());
				
				// ���b�Z�[�W�_�C�A���O�\��
				if(isProxyChanged) {
					JOptionPane.showMessageDialog(null, "�v���L�V�ݒ��ۑ����܂����B" +
							System.getProperty("line.separator") +
							"�K�p����ɂ́A�{�v���O�������N�����Ȃ����K�v������܂��B" +
							System.getProperty("line.separator") +
							"OK ���N���b�N����ƏI�����܂��B",
							"�v���L�V�ݒ�" , JOptionPane.INFORMATION_MESSAGE);
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
