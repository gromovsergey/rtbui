package app.programmatic.ui.kickcaptcha.service;

import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.kickcaptcha.model.Payments;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentsServiceImpl implements PaymentsService {

    @Override
    @Restrict(restriction = "kickcaptcha.payment.viewPayments", parameters="userId")
    public Payments findPayments(Long userId) {
        Payments result = new Payments();

        result.setPaidAmount(BigDecimal.TEN);
        result.setSpentAmount(BigDecimal.valueOf(3));
        result.setBalance(result.getPaidAmount().subtract(result.getSpentAmount()));

        return result;
    }
}
