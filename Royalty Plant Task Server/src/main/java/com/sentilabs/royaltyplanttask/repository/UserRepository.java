package com.sentilabs.royaltyplanttask.repository;

import com.sentilabs.royaltyplanttask.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by sentipy on 09/07/15.
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {
}
