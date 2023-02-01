package com.dmarkdown.auth.repository;

import com.dmarkdown.auth.models.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface UserRepository extends JpaRepository<UserInfo,Integer> {

    public Optional<UserInfo> findByEmail(String email);
}
