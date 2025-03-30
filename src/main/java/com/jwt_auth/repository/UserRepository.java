package com.jwt_auth.repository;

import com.jwt_auth.model.UserPOJO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<UserPOJO, String> {

    boolean existsByEmail(String email);

    Optional<UserPOJO> findByEmail(String email);
}
