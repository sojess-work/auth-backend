package com.dmarkdown.auth.services;

import com.dmarkdown.auth.Exceptions.UserAlreadyExistsException;
import com.dmarkdown.auth.Exceptions.VerificationTokenNotFoundException;
import com.dmarkdown.auth.authentication.AuthenticationRequest;
import com.dmarkdown.auth.authentication.AuthenticationResponse;
import com.dmarkdown.auth.authentication.RegisterRequest;
import com.dmarkdown.auth.config.JwtService;
import com.dmarkdown.auth.enums.Role;
import com.dmarkdown.auth.models.UserInfo;
import com.dmarkdown.auth.models.VerificationToken;
import com.dmarkdown.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.UUID;


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

    private final VerificationTokenService tokenService;
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
             final String token = tokenService.createVerificationToken(user);
             return emailService.sendVerificationMail(user,token);
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

    public ResponseEntity<AuthenticationResponse> confirmUser(String token) {
        try{
            VerificationToken verificationToken = tokenService.getVerificationTokenDetails(token);
            UserInfo user = verificationToken.getUser();
            Calendar cal = Calendar.getInstance();
            if((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {

                return ResponseEntity.badRequest().body(AuthenticationResponse.builder().message("Url Expired").build());
            }
            user.setEnabled(true);
            userRepository.save(user);
            return ResponseEntity.ok(AuthenticationResponse.builder().message("User Confirmed").build());
        } catch (VerificationTokenNotFoundException e) {
           return ResponseEntity.badRequest().body(AuthenticationResponse.builder().message("Invalid Url").build());
        }
    }

    public ResponseEntity<AuthenticationResponse> resendVerificationEmail(RegisterRequest user) {
        UserInfo userInfo = userRepository.findByEmail(user.getEmail()).get();
        ResponseEntity<AuthenticationResponse> response ;
        try {
            VerificationToken token = tokenService.getVerificationTokenDetails(userInfo);
            if (isTokenExpired(token)) {
                final String newToken = tokenService.updateVerificationToken(token);
                response  =  emailService.sendVerificationMail(userInfo,newToken);
            }else{
                response = emailService.sendVerificationMail(userInfo,token.getToken());
            }
        } catch (VerificationTokenNotFoundException e) {
            log.error("Invalid user with username: "+user.getEmail());
            return ResponseEntity.badRequest().body(AuthenticationResponse.builder().message("Invalid User").build());
        }
        return response;
    }

    private boolean isTokenExpired(VerificationToken token){
        Calendar cal = Calendar.getInstance();
        return (token.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0;
    }
}
