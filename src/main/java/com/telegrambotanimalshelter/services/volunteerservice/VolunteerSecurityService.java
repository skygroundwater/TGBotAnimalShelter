package com.telegrambotanimalshelter.services.volunteerservice;

import com.telegrambotanimalshelter.models.Volunteer;
import com.telegrambotanimalshelter.security.VolunteerWrapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class VolunteerSecurityService implements UserDetailsService {

    private final VolunteersService volunteersService;

    public VolunteerSecurityService(VolunteersService volunteersService) {
        this.volunteersService = volunteersService;
    }

    public Volunteer checkByUsername(String username) {
        return volunteersService.findByUserName(username);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new VolunteerWrapper(checkByUsername(username));
    }
}
