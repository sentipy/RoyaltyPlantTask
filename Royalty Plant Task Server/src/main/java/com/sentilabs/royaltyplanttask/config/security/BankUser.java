package com.sentilabs.royaltyplanttask.config.security;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * Created by sentipy on 05/07/15.
 */
public class BankUser extends User {

    private final long clientId;

    public BankUser(long clientId, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
        this.clientId = clientId;
    }

    public long getClientId() {
        return clientId;
    }
}
