package app.programmatic.ui.kickcaptcha.model;

public class Settings {
    private String kickCaptchaKey;

    public String getKickCaptchaKey() {
        return kickCaptchaKey;
    }

    public Settings setKickCaptchaKey(String kickCaptchaKey) {
        this.kickCaptchaKey = kickCaptchaKey;
        return this;
    }
}
