package com.sentilabs.royaltyplanttask.service.impl;

import com.sentilabs.royaltyplanttask.config.security.BankUser;
import com.sentilabs.royaltyplanttask.dao.interfaces.UserDAO;
import com.sentilabs.royaltyplanttask.entity.UserEntity;
import com.sentilabs.royaltyplanttask.repository.UserRepository;
import com.sentilabs.royaltyplanttask.service.interfaces.BankUserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Created by sentipy on 04/07/15.
 */

@Component
public class BankUserServiceImpl implements BankUserService, UserDetailsService {

    private static Logger logger = LogManager.getLogger(BankUserServiceImpl.class);

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Override
    @Transactional
    public UserEntity registerUser(UserEntity userEntity) {
        logger.info("registering user = " + userEntity.getUsername());
        userEntity.setPassword(bCryptPasswordEncoder.encode(userEntity.getPassword()));
        final UserEntity savedUserEntity = userRepository.save(userEntity);
        return savedUserEntity;
        /*final UserEntity user = userDAO.createUser(userEntity);
        return user;*/
    }

    @Override
    @Transactional
    public UserEntity getUserByName(String userName) {
        return this.getUserByName(userName, false);
    }

    @Override
    @Transactional
    public UserEntity getUserByName(String userName, boolean setPassword) {
        return userDAO.getUserByName(userName, setPassword);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        final UserEntity userEntity = userDAO.getUserByName(userName, true);
        UserDetails userDetails = new BankUser(userEntity.getId(), userEntity.getUsername()
                , userEntity.getPassword()
                , Collections.<GrantedAuthority>emptyList());
        return userDetails;
    }
}
