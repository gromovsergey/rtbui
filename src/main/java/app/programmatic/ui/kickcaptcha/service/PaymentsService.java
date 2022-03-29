package app.programmatic.ui.kickcaptcha.service;

import app.programmatic.ui.kickcaptcha.model.Payments;

public interface PaymentsService {

    Payments findPayments(Long userId);
}
