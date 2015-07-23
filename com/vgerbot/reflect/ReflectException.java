package com.vgerbot.reflect; 
public class ReflectException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ReflectException() {
		super();
	}

	public ReflectException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public ReflectException(String detailMessage) {
		super(detailMessage);
	}

	public ReflectException(Throwable throwable) {
		super(throwable);
	}
	
}
