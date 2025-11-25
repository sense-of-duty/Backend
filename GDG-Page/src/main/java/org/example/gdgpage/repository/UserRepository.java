package org.example.gdgpage.repository;

import org.example.gdgpage.domain.auth.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
