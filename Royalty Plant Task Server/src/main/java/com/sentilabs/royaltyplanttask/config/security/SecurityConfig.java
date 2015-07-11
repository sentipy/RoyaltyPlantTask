package com.sentilabs.royaltyplanttask.config.security;

import com.sentilabs.royaltyplanttask.entity.UserEntity;
import com.sentilabs.royaltyplanttask.service.interfaces.BankUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.RegexRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Created by sentipy on 04/07/15.
 */
@Configuration
@EnableWebMvcSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    BankUserService bankUserService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private class UserDetailsServiceImpl implements UserDetailsService {

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            final UserEntity userEntity;
            try {
                userEntity = bankUserService.getUserByName(username, true);
            } catch (Exception e) {
                throw new UsernameNotFoundException("Service returned exception", e);
            }
            UserDetails userDetails = new BankUser(userEntity.getId(), userEntity.getUsername()
                    , userEntity.getPassword()
                    , Collections.<GrantedAuthority>emptyList());
            return userDetails;
        }
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth.userDetailsService(new UserDetailsServiceImpl())
                .passwordEncoder(passwordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().requireCsrfProtectionMatcher(new RequestMatcher() {

            private RegexRequestMatcher apiMatcher = new RegexRequestMatcher("/api/.*", null);
            private Pattern allowedMethods = Pattern.compile("^(GET|HEAD|TRACE|OPTIONS)$");

            @Override
            public boolean matches(HttpServletRequest httpServletRequest) {
                if (allowedMethods.matcher(httpServletRequest.getMethod()).matches()){
                    return false;
                }
                if (apiMatcher.matches(httpServletRequest)) {
                    return false;
                }
                return true;
            }
        });

        http.authorizeRequests()
                .antMatchers("/resources/**", "/api/registerUser", "/api/logout", "/api/login"
                        /*, "/spring_security_login", "/spring_security_logout"
                        , "/j_spring_security_login", "/j_spring_security_logout"
                        , "/login", "/logout"*/)
                .permitAll()
                .anyRequest().authenticated().and().formLogin()
                .successHandler(new LoginSuccessProcessor())
                .permitAll().and().logout();
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager getAuthenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
