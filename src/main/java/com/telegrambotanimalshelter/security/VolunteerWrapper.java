package com.telegrambotanimalshelter.security;

import com.telegrambotanimalshelter.models.Volunteer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public record VolunteerWrapper(Volunteer volunteer) implements UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(
                new SimpleGrantedAuthority(volunteer.getRole().toString())
        );
    }

    @Override
    public String getPassword() {
        return this.volunteer.getPassword();
    }

    @Override
    public String getUsername() {
        return this.volunteer.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return volunteer.isNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return volunteer.isNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return volunteer.isNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return volunteer.isEnabled();
    }
}
