package com.dmarkdown.auth.services;

import com.dmarkdown.auth.Exceptions.OperationFailedException;
import com.dmarkdown.auth.Exceptions.UserAlreadyExistsException;
import com.dmarkdown.auth.authentication.AuthenticationResponse;
import com.dmarkdown.auth.models.UserInfo;
import com.dmarkdown.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public ResponseEntity<AuthenticationResponse> registerNewUser(UserInfo user) throws UserAlreadyExistsException, OperationFailedException {
        if(emailExist(user.getEmail())){
            throw new UserAlreadyExistsException();
        }
        UserInfo userInfo = userRepository.save(user);
        if(userInfo ==null){
            throw new OperationFailedException("Failed Saving User");
        }
        return ResponseEntity.ok(AuthenticationResponse.builder().message("User Saved Succesfully").build());
    }

    private boolean emailExist(String email) {

        Optional<UserInfo> user = userRepository.findByEmail(email);
        return !user.isEmpty();
    }
}
