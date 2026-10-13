package com.panut.centralbanking.appsecuritycore.usersecurity.context;

import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class UserPrincipal implements UserDetails {
    private String username;
    private List<SimpleGrantedAuthority> grantedUserAuthorities;


    private void toGrantedAuthority(List<String> list) {
        this.grantedUserAuthorities = list.stream()
                .map(authority -> new SimpleGrantedAuthority(authority))
                .toList();
    }

    public UserPrincipal(List<String> userAuthorities,
                         String username) {
        Objects.requireNonNull(userAuthorities);
        Objects.requireNonNull(username);
        toGrantedAuthority(userAuthorities);
        this.username = username;
    }

    public UserPrincipal(List<String> userAuthorities) {
        Objects.requireNonNull(userAuthorities);
        toGrantedAuthority(userAuthorities);

    }

    public UserPrincipal(String username) {
        Objects.requireNonNull(username);
        this.username = username;
    }





    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedUserAuthorities;
    }

    @Override
    public @Nullable String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setGrantedUserAuthorities(List<String> userAuthorities) {
        toGrantedAuthority(userAuthorities);
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
