package org.example.gdgpage.repository;

import org.example.gdgpage.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    Optional<User> findByEmail(String email);
    List<User> findByIsApprovedFalseAndRejectionReasonIsNull();
}
