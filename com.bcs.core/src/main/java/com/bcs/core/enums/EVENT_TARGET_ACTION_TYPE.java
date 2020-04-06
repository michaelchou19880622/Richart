package com.bcs.core.enums;

public enum EVENT_TARGET_ACTION_TYPE {
			
	EVENT_SHARE("EventShare"),
		ACTION_ShareWinning("ShareWinning"),

	EVENT_SEND_GROUP("SendGroup"),
		ACTION_UPLOAD_MID("UploadMid"),

	EVENT_SERIAL_SETTING("SerialSetting"),
		ACTION_UPLOAD_MID_SERIAL("UploadMidSerial"),
		
	EVENT_LINE_POINT_SEND("LinePointSend"),
	
	// For Hpi Richmenu 
	TARGET_RICHMENU_SEND_GROUP("RichmenuSendGroup"),
		ACTION_UPLOAD_RICHMENU_MID("UploadRichmenuMid")
	;

    private final String str;
    
    EVENT_TARGET_ACTION_TYPE(String str) {
        this.str = str;
    }
	/**
	 * @return the str
	 */
	public String toString() {
		return str;
	}
}
