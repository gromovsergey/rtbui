package app.programmatic.ui.kickcaptcha.service;

import app.programmatic.ui.kickcaptcha.model.FinancialStatsEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface FinancialStatsService {

    List<FinancialStatsEntity> getFinancialStats(Integer accountId, LocalDateTime start, LocalDateTime end);
}
