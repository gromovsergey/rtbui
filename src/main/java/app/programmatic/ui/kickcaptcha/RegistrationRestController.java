package app.programmatic.ui.kickcaptcha;

import app.programmatic.ui.account.dao.model.AccountRole;
import app.programmatic.ui.authentication.model.AuthenticationException;
import app.programmatic.ui.authentication.model.Credentials;
import app.programmatic.ui.authentication.view.LoginData;
import app.programmatic.ui.authorization.service.KickCaptchaAuthorizationServiceImpl;
import app.programmatic.ui.kickcaptcha.dto.PasswordDto;
import app.programmatic.ui.kickcaptcha.dto.UserDto;
import app.programmatic.ui.kickcaptcha.error.InvalidOldPasswordException;
import app.programmatic.ui.kickcaptcha.model.UserEntity;
import app.programmatic.ui.kickcaptcha.model.VerificationToken;
import app.programmatic.ui.kickcaptcha.registration.OnRegistrationCompleteEvent;
import app.programmatic.ui.kickcaptcha.service.CaptchaUserService;
import app.programmatic.ui.kickcaptcha.service.SecurityUserService;
import app.programmatic.ui.kickcaptcha.util.GenericResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kickcaptcha/v1")
public class RegistrationRestController {
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    @Autowired
    private CaptchaUserService captchaUserService;

    @Autowired
    private SecurityUserService securityUserService;

    @Autowired
    private MessageSource messages;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private Environment env;

    @Autowired
    private KickCaptchaAuthorizationServiceImpl kickCaptchaAuthorizationServiceImpl;

    // Temp auth (later spring security auth will be used)
    @PostMapping(value = "/user/login", produces = "application/json")
    public Credentials login(@RequestBody LoginData loginData) {
        UserEntity userEntity = captchaUserService.findUserByEmail(loginData.getLogin());
        boolean loginPasswordValid = Optional.ofNullable(userEntity)
                .map(u -> captchaUserService.checkIfValidOldPassword(u, loginData.getPassword()))
                .orElse(false);
        if (!loginPasswordValid) {
            throw new AuthenticationException("Login and Password not matched");
        }

        VerificationToken verificationToken = captchaUserService.getVerificationToken(userEntity);
        return new Credentials(verificationToken.getToken(),
                userEntity.getApiKey(),
                AccountRole.INTERNAL,
                Long.valueOf(userEntity.getAccountId()),
                null);
    }

    @PostMapping(value = "/user/logout", produces = "application/json")
    public void logout() {
        UserEntity userEntity = kickCaptchaAuthorizationServiceImpl.getAuthUser();
        String token = UUID.randomUUID().toString();
        captchaUserService.createVerificationTokenForUser(userEntity, token);
    }

    // Registration
    @PostMapping(value = "/user/registration", produces = "application/json")
    public GenericResponse registerUserAccount(@RequestBody @Valid UserDto accountDto, final HttpServletRequest request) {
        LOGGER.debug("Registering user account with information: {}", accountDto);

        accountDto.setUid(UUID.randomUUID().toString());

        final UserEntity registered = captchaUserService.registerNewUserAccount(accountDto);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), getAppUrl(request)));
        return new GenericResponse("success");
    }

    // User activation - verification
    @GetMapping("/user/resendRegistrationToken")
    public GenericResponse resendRegistrationToken(final HttpServletRequest request, @RequestParam("token") final String existingToken) {
        final VerificationToken newToken = captchaUserService.generateNewVerificationToken(existingToken);
        final UserEntity userEntity = captchaUserService.getUser(newToken.getToken());
        mailSender.send(constructResendVerificationTokenEmail(getAppUrl(request), request.getLocale(), newToken, userEntity));
        return new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale()));
    }

    // Reset password
    @PostMapping("/user/resetPassword")
    public GenericResponse resetPassword(final HttpServletRequest request, @RequestParam("email") final String userEmail) {
        final UserEntity userEntity = captchaUserService.findUserByEmail(userEmail);
        if (userEntity != null) {
            final String token = UUID.randomUUID().toString();
            captchaUserService.createPasswordResetTokenForUser(userEntity, token);
            mailSender.send(constructResetTokenEmail(getAppUrl(request), request.getLocale(), token, userEntity));
        }
        return new GenericResponse(messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
    }

    // Save password
    @PostMapping("/user/savePassword")
    public GenericResponse savePassword(final Locale locale, @Valid PasswordDto passwordDto) {

        final String result = securityUserService.validatePasswordResetToken(passwordDto.getToken());

        if(result != null) {
            return new GenericResponse(messages.getMessage("auth.message." + result, null, locale));
        }

        Optional<UserEntity> user = captchaUserService.getUserByPasswordResetToken(passwordDto.getToken());
        if(user.isPresent()) {
            captchaUserService.changeUserPassword(user.get(), passwordDto.getNewPassword());
            return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
        } else {
            return new GenericResponse(messages.getMessage("auth.message.invalid", null, locale));
        }
    }

    // Change user password
    @PostMapping("/user/updatePassword")
    public GenericResponse changeUserPassword(final Locale locale, @Valid PasswordDto passwordDto) {
        final UserEntity userEntity = captchaUserService.findUserByEmail(((UserEntity) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getEmail());
        if (!captchaUserService.checkIfValidOldPassword(userEntity, passwordDto.getOldPassword())) {
            throw new InvalidOldPasswordException();
        }
        captchaUserService.changeUserPassword(userEntity, passwordDto.getNewPassword());
        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }

    // ============== NON-API ============

    private SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale, final VerificationToken newToken, final UserEntity userEntity) {
        final String confirmationUrl = contextPath + "/registrationConfirm.html?token=" + newToken.getToken();
        final String message = messages.getMessage("message.resendToken", null, locale);
        return constructEmail("Resend Registration Token", message + " \r\n" + confirmationUrl, userEntity);
    }

    private SimpleMailMessage constructResetTokenEmail(final String contextPath, final Locale locale, final String token, final UserEntity userEntity) {
        final String url = contextPath + "/user/changePassword?token=" + token;
        final String message = messages.getMessage("message.resetPassword", null, locale);
        return constructEmail("Reset Password", message + " \r\n" + url, userEntity);
    }

    private SimpleMailMessage constructEmail(String subject, String body, UserEntity userEntity) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject(subject);
        email.setText(body);
        email.setTo(userEntity.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }

    private String getAppUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }

    private String getClientIP(HttpServletRequest request) {
        final String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
