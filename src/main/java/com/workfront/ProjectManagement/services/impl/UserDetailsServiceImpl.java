package com.workfront.ProjectManagement.services.impl;

import com.workfront.ProjectManagement.domain.ProjectUserDetails;
import com.workfront.ProjectManagement.domain.User;
import com.workfront.ProjectManagement.repositoriy.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.getUserByUsername(username);

        if(user == null) {
            throw new UsernameNotFoundException(username);
        }

        return new ProjectUserDetails(user);
    }
}
