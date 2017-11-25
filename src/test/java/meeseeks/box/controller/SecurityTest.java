package meeseeks.box.controller;

import meeseeks.box.domain.UserEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;


class SecurityTest {

    void login(final UserEntity user, final String role) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, AuthorityUtils.createAuthorityList(role));
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    void logout() {
        SecurityContextHolder.clearContext();
    }
}
