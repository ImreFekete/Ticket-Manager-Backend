package com.imrefekete.ticket_manager.repository;

import com.imrefekete.ticket_manager.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findUserByUsername(String username);
    Optional<Object> findUserByEmail(String email);
}
