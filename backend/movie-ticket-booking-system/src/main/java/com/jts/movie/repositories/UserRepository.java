package com.jts.movie.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jts.movie.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByEmailId(String emailId);;
    Optional<User> findByConfirmationToken(String token);
    Optional<User> findByResetToken(String resetToken);
}