package app.programmatic.ui.kickcaptcha.service;

import app.programmatic.ui.kickcaptcha.model.RecognitionStatsEntity;

import java.time.LocalDate;
import java.util.List;

public interface RecognitionStatsService {

    List<RecognitionStatsEntity> getRecaptchaStats(int accountId, LocalDate start, LocalDate end);
}
