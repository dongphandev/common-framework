package com.tmoncorp.crawler.exception;

public class CommonException extends RuntimeException {
    private static final long serialVersionUID = -3919285568067466685L;

    private ExceptionCode code;
    
    private String errorMessage;

    public ExceptionCode getCode() {
        return code;
    }

    public void setCode(ExceptionCode code) {
        this.code = code;
    }

    public CommonException(ExceptionCode code, String errorMessage) {
        super();
        this.code = code;
        this.setErrorMessage(errorMessage);
    }

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

}
