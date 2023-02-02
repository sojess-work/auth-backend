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

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class VerificationTokenService {
    private static final int EXPIRATION = 60;

    private final VerificationTokenRepository verificationTokenRepository;

    public String createVerificationToken(UserInfo user) {
        final String token = UUID.randomUUID().toString();
        VerificationToken verificationToken = VerificationToken.builder()
                        .token(token)
                                .user(user).expiryDate(calculateExpiryDate(EXPIRATION)).build();
         verificationTokenRepository.save(verificationToken);
         return token;
    }

    public String updateVerificationToken(VerificationToken verificationToken){
        final String token = UUID.randomUUID().toString();
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(calculateExpiryDate(EXPIRATION));
        verificationTokenRepository.save(verificationToken);
        return token;
    }

    public VerificationToken getVerificationTokenDetails(String token) throws VerificationTokenNotFoundException {

        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if(verificationToken == null){
            throw new VerificationTokenNotFoundException();
        }
        return  verificationToken;
    }
    public VerificationToken getVerificationTokenDetails(UserInfo user) throws VerificationTokenNotFoundException {
        VerificationToken verificationToken = verificationTokenRepository.findByUser(user);
        if(verificationToken == null){
            throw new VerificationTokenNotFoundException();
        }
        return  verificationToken;
    }
    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}
