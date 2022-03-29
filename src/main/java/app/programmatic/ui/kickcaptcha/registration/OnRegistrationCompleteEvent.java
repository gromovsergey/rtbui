package app.programmatic.ui.kickcaptcha.registration;

import app.programmatic.ui.kickcaptcha.model.UserEntity;
import org.springframework.context.ApplicationEvent;

import java.util.Locale;

@SuppressWarnings("serial")
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private final String appUrl;
    private final Locale locale;
    private final UserEntity userEntity;

    public OnRegistrationCompleteEvent(final UserEntity userEntity, final Locale locale, final String appUrl) {
        super(userEntity);
        this.userEntity = userEntity;
        this.locale = locale;
        this.appUrl = appUrl;
    }

    //

    public String getAppUrl() {
        return appUrl;
    }

    public Locale getLocale() {
        return locale;
    }

    public UserEntity getUser() {
        return userEntity;
    }

}
