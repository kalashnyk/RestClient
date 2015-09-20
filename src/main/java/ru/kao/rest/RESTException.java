package ru.kao.rest;

@SuppressWarnings("serial")
public class RESTException extends Exception {

	public RESTException() {
		super();
	}

	public RESTException(String message) {
		super(message);
	}

	public RESTException(String message, Throwable cause) {
		super(message, cause);
	}
}
