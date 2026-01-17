package com.example.common.repository;

import com.example.common.model.User;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);
}
