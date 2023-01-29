package com.dmarkdown.auth.event;

import com.dmarkdown.auth.models.UserInfo;
import lombok.*;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private String appUrl;
    private Locale locale;
    private UserInfo user;

    public OnRegistrationCompleteEvent(UserInfo user, Locale locale, String appUrl){
        super(user);
        this.appUrl=appUrl;
        this.locale=locale;
        this.user=user;

    }

}
