package app.programmatic.ui.authorization.service;

import app.programmatic.ui.kickcaptcha.model.UserEntity;
import app.programmatic.ui.kickcaptcha.service.CaptchaUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class KickCaptchaAuthorizationServiceImpl {
    private static final ThreadLocal<UserEntity> currentUserThreadLocal = new ThreadLocal<>();

    @Autowired
    private CaptchaUserService captchaUserService;

    public UserEntity configure(String userToken, String ip, long sessionTimeoutInMinutes) {
        UserEntity userEntity = captchaUserService.getUser(userToken);
        return userEntity == null ? null : configureImpl(userEntity);
    }

    public UserEntity configureAnonymous(String ip) {
        return null;
    }

    private UserEntity configureImpl(UserEntity userEntity) {
        currentUserThreadLocal.set(userEntity);
        return userEntity;
    }

    public UserEntity getAuthUser() {
        return currentUserThreadLocal.get();
    }

    public void cleanUp() {
        currentUserThreadLocal.remove();
    }
}
