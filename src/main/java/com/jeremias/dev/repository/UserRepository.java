package com.jeremias.dev.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.jeremias.dev.model.User;

public interface UserRepository extends MongoRepository<User, String> {
	Optional<User> findByEmailAddress(String email);
    Optional<User> findBySub(String sub);
}
