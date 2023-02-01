package com.dmarkdown.auth.services;

import com.dmarkdown.auth.authentication.AuthenticationResponse;
import com.dmarkdown.auth.config.ApplicationProperties;
import com.dmarkdown.auth.config.AuthAppServiceConfiguration;
import com.dmarkdown.auth.dto.VerificationRequest;
import com.dmarkdown.auth.models.UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final VerificationTokenService tokenService;
    private final ApplicationProperties applicationProperties;
    private final AuthAppServiceConfiguration serviceConfiguration;
    private  final RestTemplate restTemplate;

    public ResponseEntity<AuthenticationResponse> sendVerificationMail(UserInfo user) {
        String token = UUID.randomUUID().toString();
        tokenService.createVerificationToken(user, token);

        VerificationRequest request = new VerificationRequest();
        request.setFrom("noreply@authapp.com");
        request.setTo(user.getEmail());
        request.setToken(token);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type","application/json");
        final HttpEntity<VerificationRequest> httpEntity = new HttpEntity<>(request,headers);
        final StringBuilder url =  new StringBuilder();
        url.append(applicationProperties.getEmailServiceUrl()).append(serviceConfiguration.getEmailServiceSendVerificationEmailEndpoint());
        try{
//            ParameterizedTypeReference<?> responseObj =
//                    new ParameterizedTypeReference<>() {
//                    };
            ResponseEntity<AuthenticationResponse> response =  restTemplate.exchange(url.toString()
                    , HttpMethod.POST,httpEntity,AuthenticationResponse.class);
            return response;
        }catch (Exception e){
            log.error("Failed to send email to: " +user.getEmail());
            return ResponseEntity.ok(AuthenticationResponse.builder().message("Failed to send email").build());
        }
    }
}
