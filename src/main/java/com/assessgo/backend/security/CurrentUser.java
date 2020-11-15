package com.assessgo.backend.security;


import com.assessgo.backend.entity.User;

@FunctionalInterface
public interface CurrentUser {

	User getUser();
}
