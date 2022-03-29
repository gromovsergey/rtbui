package app.programmatic.ui.kickcaptcha.service;

import app.programmatic.ui.common.restriction.annotation.Restrict;
import app.programmatic.ui.kickcaptcha.model.FinancialStatsEntity;
import app.programmatic.ui.kickcaptcha.repository.FinancialStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FinancialStatsServiceImpl implements FinancialStatsService {
    private final FinancialStatsRepository statsRepository;

    @Override
    @Restrict(restriction = "kickcaptcha.stat.viewFinancialStat", parameters="accountId")
    public List<FinancialStatsEntity> getFinancialStats(Integer accountId, LocalDateTime start, LocalDateTime end) {
        return statsRepository.findAllByAccountIdAndTimeBetween(accountId, start, end)
                .orElseGet(ArrayList::new);
    }
}
