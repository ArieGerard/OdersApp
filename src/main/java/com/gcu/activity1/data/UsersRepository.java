package com.gcu.activity1.data;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

// Comment for commit since i already did this
@Repository
public interface UsersRepository extends CrudRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUsername(String username);
}
