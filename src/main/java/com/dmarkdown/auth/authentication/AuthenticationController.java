package com.dmarkdown.auth.authentication;

import com.dmarkdown.auth.models.UserInfo;
import com.dmarkdown.auth.services.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest user, HttpServletRequest request){

        return authenticationService.register(user,request);
    }
    @GetMapping("/confirmUser")
    public ResponseEntity<AuthenticationResponse> confirmUser(@RequestParam String token){
       return  authenticationService.confirmUser(token);
    }
    @PostMapping("/resendVerificationEmail")
    public ResponseEntity<AuthenticationResponse> resendVerificationEmail(@RequestBody RegisterRequest user){
        return authenticationService.resendVerificationEmail( user);
    }
    @GetMapping("/resendVerificationEmail")
    public ResponseEntity<AuthenticationResponse> resendVerificationEmail(@RequestParam String token){
        return authenticationService.resendVerificationEmail( token);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
        return  ResponseEntity.ok(authenticationService.authenticate(request));
    }
    @GetMapping("/checkUserExists")
    public ResponseEntity<Boolean> isUserExists(@RequestParam String email){
        return ResponseEntity.ok(authenticationService.isUserExists(email));
    }
}
