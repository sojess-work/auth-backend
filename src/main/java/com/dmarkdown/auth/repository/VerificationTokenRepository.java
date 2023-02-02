package com.dmarkdown.auth.repository;

import com.dmarkdown.auth.models.UserInfo;
import com.dmarkdown.auth.models.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken,Long> {

    VerificationToken findByToken(String token);
    VerificationToken findByUser(UserInfo user);

}
