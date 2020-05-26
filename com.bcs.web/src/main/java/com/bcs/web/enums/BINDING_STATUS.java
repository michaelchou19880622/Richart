package com.bcs.web.enums;

import java.util.Arrays;
import java.util.List;

public enum BINDING_STATUS {
	
	ALL(0, "BINDED,UNBIND", "綁定 + 未綁定"),
	BINDED(1, "BINDED", "綁定"),
	UNBIND(2, "UNBIND", "未綁定"),
	;

	private final int index;
    private final String value;
    private final String value_ch;
    
    BINDING_STATUS(int index, String value, String value_ch) {
        this.index = index;
        this.value = value;
        this.value_ch = value_ch;
    }
    
	/**
	 * @return the value
	 */
	public String toString() {
		return value;
	}
	
	/**
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * @return the value_ch
	 */
	public String getValueCh() {
		return value_ch;
	}
	

	/**
	 * @return the name
	 */
	public static String getValueChByIndex(final int index) {
		for (BINDING_STATUS enum_bindStatus : BINDING_STATUS.values()) {
			if (enum_bindStatus.index == index) {
				String value = enum_bindStatus.getValueCh();
				
				return value;
			}
		}
		return null;
	}
	
	/**
	 * @return the name
	 */
	public static List<String> getListValueByIndex(final int index) {
		for (BINDING_STATUS enum_bindStatus : BINDING_STATUS.values()) {
			if (enum_bindStatus.index == index) {
				String value = enum_bindStatus.toString();
				
				List<String> listValue = Arrays.asList(value.split(","));
				
				return listValue;
			}
		}
		return null;
	}
}
