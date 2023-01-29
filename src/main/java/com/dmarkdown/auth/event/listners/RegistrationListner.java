package com.dmarkdown.auth.event.listners;

import com.dmarkdown.auth.event.OnRegistrationCompleteEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RegistrationListner implements ApplicationListener<OnRegistrationCompleteEvent> {

    private final MessageSource messageSource;
    private final JavaMail
    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {

    }
}
