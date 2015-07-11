package com.sentilabs.royaltyplanttask.dao.interfaces;

import com.sentilabs.royaltyplanttask.entity.UserEntity;

/**
 * Created by sentipy on 04/07/15.
 */
public interface UserDAO {

    UserEntity getUserByName(String userName);
    UserEntity getUserByName(String userName, boolean setPassword);
    UserEntity getUserById(Long userId);
    UserEntity createUser(UserEntity userEntity);
}
