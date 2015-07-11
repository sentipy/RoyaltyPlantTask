package com.sentilabs.royaltyplanttask.service.interfaces;

import com.sentilabs.royaltyplanttask.entity.UserEntity;

/**
 * Created by sentipy on 04/07/15.
 */
public interface BankUserService {

    UserEntity registerUser(UserEntity userEntity);
    UserEntity getUserByName(String userName);
    UserEntity getUserByName(String userName, boolean setPassword);

}
