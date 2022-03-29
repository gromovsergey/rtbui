package app.programmatic.ui.payment.repository;

import app.programmatic.ui.payment.dao.model.PaymentTransactionEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends CrudRepository<PaymentTransactionEntity, Long> {

    Optional<PaymentTransactionEntity> findByPaymentId(String paymentId);
}
