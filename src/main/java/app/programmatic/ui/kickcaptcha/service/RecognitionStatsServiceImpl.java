package app.programmatic.ui.kickcaptcha.service;

import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.kickcaptcha.model.RecognitionStatsEntity;
import app.programmatic.ui.kickcaptcha.repository.RecognitionStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecognitionStatsServiceImpl implements RecognitionStatsService {
    private final RecognitionStatsRepository statsRepository;

    @Override
    @Restrict(restriction = "kickcaptcha.stat.viewMainStat", parameters="accountId")
    public List<RecognitionStatsEntity> getRecaptchaStats(int accountId, LocalDate start, LocalDate end) {

        return statsRepository.findAllByDateBetweenAndAccountId(start, end, accountId)
                .orElseGet(ArrayList::new);
}
}
