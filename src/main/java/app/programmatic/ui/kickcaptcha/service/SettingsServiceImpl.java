package app.programmatic.ui.kickcaptcha.service;

import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.kickcaptcha.model.Settings;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class SettingsServiceImpl implements SettingsService {

    @Override
    @Restrict(restriction = "kickcaptcha.settings.viewSettings", parameters="userId")
    public Settings findSettings(Long userId) {
        Settings result = new Settings();

        result.setKickCaptchaKey(UUID.randomUUID().toString());

        return result;
    }
}
