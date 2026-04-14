package me.ronygomes.userManagement.common.repository;

import me.ronygomes.userManagement.common.model.User;
import java.util.Optional;

public interface UserRepository extends BaseRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);
}
