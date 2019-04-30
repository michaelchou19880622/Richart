package com.bcs.core.utils;

import java.util.Locale;
import java.util.ResourceBundle;

import com.bcs.core.enums.LIVE_CHAT_WORDING;

public class LiveChatWordingUtil {
	public static String getString(String key) {
		Locale locale = new Locale("zh", "TW");
		ResourceBundle resourceBundle = ResourceBundle.getBundle("config.liveChatWording", locale);
		
		return resourceBundle.getString(key);
	}
	
	public static String getString(LIVE_CHAT_WORDING key) {
		Locale locale = new Locale("zh", "TW");
		ResourceBundle resourceBundle = ResourceBundle.getBundle("config.liveChatWording", locale);
		
		return resourceBundle.getString(key.toString());
	}
}