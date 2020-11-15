package com.assessgo.backend.security;

import com.assessgo.backend.common.Role;
import com.assessgo.backend.entity.Account;
import com.assessgo.backend.service.AccountService;

import java.util.Optional;

public class AuthorizationUtils {

	public static boolean isUserAuthorizedToViewAndUpdateAccount(AccountService accountService, Long accId) {
		boolean isAccessGranted = false;
		if (SecurityUtils.isUserHasRole(Role.SUPER_ADMIN)) {
			isAccessGranted = true;
		} else if (SecurityUtils.isUserHasRole(Role.ADMIN)) {
			Optional<Account> optional = accountService.getRepository().findById(accId);
			if (optional.isPresent()) {
				Account account = optional.get();
				String currentUser = SecurityUtils.getLoggedInUsername();
				isAccessGranted = account.getUsers().stream()
						.filter(user -> user.getEmail().equalsIgnoreCase(currentUser)).findAny().isPresent();
			}
		}

		return isAccessGranted;
	}
}

