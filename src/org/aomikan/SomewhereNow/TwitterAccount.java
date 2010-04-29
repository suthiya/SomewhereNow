package org.aomikan.SomewhereNow;

import twitter4j.*;
import twitter4j.http.*;

/**
 * TwitterAccount クラスは、PropertyManager をラップし、Twitter サービスを利用するのに必要な
 * 各種情報の操作を提供します。
 */
public class TwitterAccount {

	// メンバー属性
	public String userID = null;
	public boolean isActive = false;
	private boolean isOAuth = false;
	private static final String KEY = "M42qfIHl4uEyIMMpvaPSzw";
	private static final String KEY_SECRET = "l7jRBDCTVDNvg07SKwidX3QviQOb91x0txQbUmW1dDs";

	// 関係するクラスのオブジェクト
	private PropertyManager propMan = null;
	private RequestToken requestToken = null;
	
	/**
	 * TwitterAccount クラスのコンストラクタです。
	 */
	public TwitterAccount() {
		
		propMan = new PropertyManager();
		
		// プロキシ設定の適用
		if(propMan.isProxyEnabled()) {
			String[] config = propMan.getProxySettings();
			
			// プロキシをセット
	        System.setProperty("proxySet", "true");  
	        System.setProperty("proxyHost", config[0]); 
	        System.setProperty("proxyPort", config[1]); 
		}
		
		// メンバー属性の初期化
		userID = propMan.getTwitterUserID();
		isActive = propMan.isTwitterActive();
		isOAuth = propMan.isOAuth();		
	}
	
	public boolean isEnabled() {
		return propMan.isTwitterActive();
	}
	
	public void setEnable(boolean isActive) {
		this.isActive = isActive;
		if(isActive) {
			propMan.enableTwitter(true);
		} else {
			propMan.enableTwitter(false);
		}
	}
	
	public boolean isOAuth() {
		return propMan.isOAuth();
	}
	
	public void setOAuth(boolean state) {
		isOAuth = state;
		propMan.setOAuth(isOAuth);
	}
	
	public String getUserID() {
		userID = propMan.getTwitterUserID();
		return userID;
	}
	
	public boolean setUserID(String userID) {
		this.userID = userID;
		propMan.setTwitterUserID(userID);
		return true;
	}
	
	public boolean setPassword(String planePassword) {
		propMan.setTwitterPassword(planePassword);		
		return true;
	}
	
	public String getButtonsConfigURI() {
		return propMan.getButtonsConfigURI();
	}
	
	public String getRequestURL() {
		return propMan.getRequestURL();
	}
	
	public boolean isProxyEnabled() {
		return propMan.isProxyEnabled();
	}
	
	public void enableProxy(boolean state) {
		propMan.enableProxy(state);
	}
	
	public String[] getProxySettings() {
		return propMan.getProxySettings();
	}
	
	public boolean setProxySettings(String[] target) {
		propMan.setProxySettings(target);
		return true;
	}
	public String getGroupTag() {
		String groupTag = propMan.getTwitterGroup();
		return groupTag;
	}
	
	public boolean setGroupTag(String groupTag) {
		propMan.setTwitterGroup(groupTag);
		return true;
	}
	
	/**
	 * 以前設定された ID で Twitter にログインします。
	 * @return Twitter オブジェクト
	 */
	@SuppressWarnings("deprecation")
	public Twitter login() {
		Twitter twitter = null;
		if(isOAuth) {
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(KEY, KEY_SECRET);
			String[] tokens = propMan.getAccessToken();
			twitter.setOAuthAccessToken(new AccessToken(tokens[0], tokens[1]));
			return twitter;
		} else {
			try {
				twitter = new Twitter(userID, propMan.getTwitterPassword());
			} catch(Exception e) {
				new DialogManager().showErrorDialog(11, null, false);
				e.printStackTrace();
			}
		return twitter;
		}
	}
	
	/**
	 * 新しい ID で Twitter にログインします。
	 * @param userID 新しいユーザ ID, OAuth の場合 null
	 * @param password 新しいパスワード, OAuth の場合 null
	 * @return Twitter オブジェクト
	 */
	@SuppressWarnings("deprecation")
	public Twitter login(String userID, String password) {
		Twitter twitter = null;
		if(isOAuth) {
			try {
				twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(KEY, KEY_SECRET);
				requestToken = twitter.getOAuthRequestToken();
				propMan.setRequestURL(requestToken.getAuthorizationURL());
				return twitter;
			} catch(TwitterException e) {
				
			} catch(Exception e) {
				e.printStackTrace();
				new DialogManager().showErrorDialog(11, null, false);
			}
			return twitter;
		} else {
			propMan.setTwitterUserID(userID);
			propMan.setTwitterPassword(password.toString());
			try {
				twitter = new Twitter(userID, password);
			} catch(Exception e) {
				new DialogManager().showErrorDialog(11, null, false);
				e.printStackTrace();
			}
		return twitter;
		}
	}
	
	public Twitter initOAuth(Twitter twitter, String pin) {
		AccessToken accessToken = null;
		
		try {
			if(pin.length() > 0) {
				accessToken = twitter.getOAuthAccessToken(requestToken, pin);
			} else {
				accessToken = twitter.getOAuthAccessToken();
			}
		
			// アクセストークンを保存
			String[] token = new String[2];
			token[0] = accessToken.getToken();
			token[1] = accessToken.getTokenSecret();
			propMan.setAccessToken(token);
			
			// ID を抽出・保存
			userID = twitter.getScreenName();
			System.out.println("[DEBUG] ScreenName: " + userID);
			propMan.setTwitterUserID(userID);
			
		} catch(TwitterException e) {
			if(e.getStatusCode() == 401) {
				new DialogManager().showErrorDialog(15, null, false);
			} else {
				e.printStackTrace();	
				new DialogManager().showErrorDialog(11, null, false);
			}
		} catch(Exception e) {
			e.printStackTrace();	
			new DialogManager().showErrorDialog(11, null, false);		
		}
		return twitter;
	}
}
