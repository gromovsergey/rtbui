package app.programmatic.ui.kickcaptcha.repository;

import app.programmatic.ui.kickcaptcha.model.RecognitionStatsEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface RecognitionStatsRepository extends CrudRepository<RecognitionStatsEntity, Long> {

    Optional<List<RecognitionStatsEntity>> findAllByDateBetweenAndAccountId(LocalDate start, LocalDate end, Integer accountId);

}
