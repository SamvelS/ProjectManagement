package com.workfront.ProjectManagement.security;

import com.workfront.ProjectManagement.services.impl.UserDetailsServiceImpl;
import com.workfront.ProjectManagement.utilities.Beans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Beans beans;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()

                .authorizeRequests()
                .antMatchers("/users/changePassword")
                    .access("permitAll")
                .antMatchers("/users/**")
                    .hasAnyAuthority("manage_users")
                .antMatchers("/tasks/**")
                    .hasAnyAuthority("manage_tasks")
                .antMatchers("/projects/**")
                    .hasAnyAuthority("manage_projects")
                .antMatchers("/")
                    .access("permitAll")

                .and()
                    .formLogin()
                        .loginPage("/login")

                .and()
                    .logout()
                        .logoutSuccessUrl("/");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(this.userDetailsService());
        authProvider.setPasswordEncoder(this.beans.passwordEncoder());
        return authProvider;
    }
}

