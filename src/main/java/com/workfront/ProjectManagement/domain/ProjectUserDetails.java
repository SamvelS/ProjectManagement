package com.workfront.ProjectManagement.domain;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

public class ProjectUserDetails implements UserDetails {
    private User user;

    public ProjectUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.user.getPermissions().stream().map(permission -> new SimpleGrantedAuthority(permission.getName())).collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired()  {
        return true;
    }

    public boolean forceToChangePassword() {
        return this.user.getStatusId() == UserStatus.CHANGE_PASSWORD.getValue();
    }

    public Integer getUserId() {
        return this.user.getId();
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
