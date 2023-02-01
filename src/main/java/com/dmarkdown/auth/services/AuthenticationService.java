package com.dmarkdown.auth.services;

import com.dmarkdown.auth.Exceptions.UserAlreadyExistsException;
import com.dmarkdown.auth.authentication.AuthenticationRequest;
import com.dmarkdown.auth.authentication.AuthenticationResponse;
import com.dmarkdown.auth.authentication.RegisterRequest;
import com.dmarkdown.auth.config.JwtService;
import com.dmarkdown.auth.enums.Role;
import com.dmarkdown.auth.models.UserInfo;
import com.dmarkdown.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    private final EmailService emailService;

    private final UserService userService;
    public ResponseEntity<AuthenticationResponse> register(RegisterRequest userDetails, HttpServletRequest request) {

        var user = UserInfo.builder()
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .email(userDetails.getEmail())
                .password(passwordEncoder.encode(userDetails.getPassword()))
                .role(Role.USER)
                .build();
         try {
             userService.registerNewUser(user);
             ResponseEntity<AuthenticationResponse> response = emailService.sendVerificationMail(user);
             return response;
         }catch (UserAlreadyExistsException e){
             log.error("User "+ user.getEmail()+" already exists");
             return  ResponseEntity.badRequest().body(AuthenticationResponse.builder()
                     .message("User with the email "+ user.getEmail()+" already exists").build());
         }
         catch (Exception e){
             log.error("Error occured while saving user");
             return  ResponseEntity.ok().body(AuthenticationResponse.builder()
                     .message("Error occured while saving user").build());
         }
//        var jwtToken = jwtService.generateToken(user);
//        return ResponseEntity.ok(AuthenticationResponse.builder()
//                .token(jwtToken).build());
    }



    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken).build();
    }
}
