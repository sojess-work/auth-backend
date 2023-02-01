package com.dmarkdown.auth.services;

import com.dmarkdown.auth.Exceptions.OperationFailedException;
import com.dmarkdown.auth.Exceptions.VerificationTokenNotFoundException;
import com.dmarkdown.auth.models.UserInfo;
import com.dmarkdown.auth.models.VerificationToken;
import com.dmarkdown.auth.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationTokenService {

    private final VerificationTokenRepository verificationTokenRepository;

    public void createVerificationToken(UserInfo user, String token) {
        VerificationToken verificationToken = VerificationToken.builder()
                        .token(token)
                                .user(user).build();
         verificationTokenRepository.save(verificationToken);
    }

    public VerificationToken getVerificationTokenDetails(String token) throws VerificationTokenNotFoundException {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if(verificationToken == null){
            throw new VerificationTokenNotFoundException();
        }
        return  verificationToken;
    }
}
