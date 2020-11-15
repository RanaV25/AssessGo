package com.assessgo.backend.service;

import org.springframework.dao.DataIntegrityViolationException;

/**
 * A entity integrity violation exception containing a message intended to be
 * shown to the end user.
 */
public class UserFriendlyDataException extends DataIntegrityViolationException {

	public UserFriendlyDataException(String message) {
		super(message);
	}

}
