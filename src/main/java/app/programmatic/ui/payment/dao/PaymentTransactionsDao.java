package app.programmatic.ui.payment.dao;

import app.programmatic.ui.payment.dao.model.PaymentTransactionEntity;
import app.programmatic.ui.payment.repository.PaymentTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentTransactionsDao {

    private final PaymentTransactionRepository paymentTransactionRepository;

    public PaymentTransactionEntity createPaymentTransaction(PaymentTransactionEntity paymentTransactionEntity) {
        return paymentTransactionRepository.save(paymentTransactionEntity);
    }

    public PaymentTransactionEntity getPaymentTransactionById(String id) {
        return paymentTransactionRepository.findByPaymentId(id).orElse(null);
    }

    public PaymentTransactionEntity updatePaymentTransaction(PaymentTransactionEntity paymentTransactionEntity) {
        return paymentTransactionRepository.save(paymentTransactionEntity);
    }
}
