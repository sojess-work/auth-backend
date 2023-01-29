package com.dmarkdown.auth.services;

import com.dmarkdown.auth.authentication.AuthenticationRequest;
import com.dmarkdown.auth.authentication.AuthenticationResponse;
import com.dmarkdown.auth.authentication.RegisterRequest;
import com.dmarkdown.auth.config.JwtService;
import com.dmarkdown.auth.enums.Role;
import com.dmarkdown.auth.event.OnRegistrationCompleteEvent;
import com.dmarkdown.auth.models.UserInfo;
import com.dmarkdown.auth.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher eventPublisher;
    public ResponseEntity<AuthenticationResponse> register(RegisterRequest userDetails, HttpServletRequest request) {
        if(emailExist(userDetails.getEmail())){
            //Todo
        }

        var user = UserInfo.builder()
                .firstName(userDetails.getFirstName())
                .lastName(userDetails.getLastName())
                .email(userDetails.getEmail())
                .password(passwordEncoder.encode(userDetails.getPassword()))
                .role(Role.USER)
                .build();
         final String appUrl = request.getContextPath();
         try {
             userRepository.save(user);
             eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user,request.getLocale(),appUrl));
         }catch (Exception e){
             log.error("Error occured while saving user");
             return  ResponseEntity.badRequest().body(AuthenticationResponse.builder()
                     .token("Error occured while saving user").build());
         }
        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .token(jwtToken).build());
    }

    private boolean emailExist(String email) {

        Optional<UserInfo> user = userRepository.findByEmail(email);
        return !user.isEmpty();
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
