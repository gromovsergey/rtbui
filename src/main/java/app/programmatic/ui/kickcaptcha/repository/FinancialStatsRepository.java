package app.programmatic.ui.kickcaptcha.repository;

import app.programmatic.ui.kickcaptcha.model.FinancialStatsEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FinancialStatsRepository extends CrudRepository<FinancialStatsEntity, Long> {

    Optional<List<FinancialStatsEntity>> findAllByAccountIdAndTimeBetween(Integer accountId,
                                                                       LocalDateTime start,
                                                                       LocalDateTime end);
}
