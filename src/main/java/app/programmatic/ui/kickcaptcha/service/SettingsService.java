package app.programmatic.ui.kickcaptcha.service;

import app.programmatic.ui.kickcaptcha.model.Settings;

public interface SettingsService {

    Settings findSettings(Long userId);
}
