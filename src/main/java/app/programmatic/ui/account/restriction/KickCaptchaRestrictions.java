package app.programmatic.ui.account.restriction;

import app.programmatic.ui.authorization.service.KickCaptchaAuthorizationServiceImpl;
import app.programmatic.ui.common.restriction.annotation.Restriction;
import app.programmatic.ui.common.restriction.annotation.Restrictions;
import app.programmatic.ui.kickcaptcha.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Restrictions
public class KickCaptchaRestrictions {

    private KickCaptchaAuthorizationServiceImpl kickCaptchaAuthorizationServiceImpl;

    @Autowired
    public KickCaptchaRestrictions(KickCaptchaAuthorizationServiceImpl kickCaptchaAuthorizationServiceImpl) {
        this.kickCaptchaAuthorizationServiceImpl = kickCaptchaAuthorizationServiceImpl;
    }

    @Restriction("kickcaptcha.settings.viewSettings")
    public boolean canViewSettings(Long userId) {
        return userId != null
                && isLoggedInCurrentSession(userId.intValue());
    }

    @Restriction("kickcaptcha.payment.viewPayments")
    public boolean canViewPayments(Long userId) {
        return userId != null
                && isLoggedInCurrentSession(userId.intValue());
    }

    @Restriction("kickcaptcha.stat.viewMainStat")
    public boolean canViewMainStat(Integer accountId) {
        return accountId != null
                && isLoggedInCurrentSession(accountId);
    }

    @Restriction("kickcaptcha.stat.viewFinancialStat")
    public boolean canViewFinancialStat(Integer accountId) {
        return accountId != null
                && isLoggedInCurrentSession(accountId);
    }

    public boolean isLoggedInCurrentSession(int userId) {
        UserEntity loggedUser = kickCaptchaAuthorizationServiceImpl.getAuthUser();
        return loggedUser != null
                && loggedUser.getAccountId() != null
                && userId == loggedUser.getAccountId();
    }
}
