package com.tmoncorp.crawler.exception;

public enum ExceptionCode {
	SYSTEM_ERROR("0001", "System error"), 
	INTERNAL_SERVER_ERROR("0007", "Internal server error"),
	EXTERNAL_SERVER_ERROR("0017", "Internal server error"),
	PARAMETER_ERROR("0008","Parameter error"),

	FILE_NOT_FOUND("0009","File not found"),
	
	CAN_NOT_SAVE_IMAGE("0010","File not found"),
	CAN_NOT_SAVE_CONTENT("0011","File not found");
	


	private String code;
	private String message;

	public String getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	private ExceptionCode(String code, String message) {
		this.code = code;
		this.message = message;
	}

}
