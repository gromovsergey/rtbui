package app.programmatic.ui.kickcaptcha;

import app.programmatic.ui.kickcaptcha.converter.FinancialStatsMapper;
import app.programmatic.ui.kickcaptcha.converter.RecognitionStatsMapper;
import app.programmatic.ui.kickcaptcha.dto.FinancialStatsDto;
import app.programmatic.ui.kickcaptcha.dto.RecognitionStatsDto;
import app.programmatic.ui.kickcaptcha.model.FinancialStatsEntity;
import app.programmatic.ui.kickcaptcha.model.Payments;
import app.programmatic.ui.kickcaptcha.model.RecognitionStatsEntity;
import app.programmatic.ui.kickcaptcha.model.Settings;
import app.programmatic.ui.kickcaptcha.service.FinancialStatsService;
import app.programmatic.ui.kickcaptcha.service.PaymentsService;
import app.programmatic.ui.kickcaptcha.service.RecognitionStatsService;
import app.programmatic.ui.kickcaptcha.service.SettingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kickcaptcha/v1")
public class KickCaptchaController {

    private final SettingsService settingsService;
    private final PaymentsService paymentsService;
    private final RecognitionStatsService recognitionStatsService;
    private final RecognitionStatsMapper recognitionStatsMapper;
    private final FinancialStatsService financialStatsService;
    private final FinancialStatsMapper financialStatsMapper;

    @RequestMapping(method = RequestMethod.GET, value = "/settings", produces = "application/json")
    public Settings getUserSettings(@RequestParam(value = "userId", required = false) Long userId) {
        return settingsService.findSettings(userId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/payments", produces = "application/json")
    public Payments getUserPayments(@RequestParam(value = "userId", required = false) Long userId) {
        return paymentsService.findPayments(userId);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/payments/topupbalance", produces = "application/json")
    public Payments topUpBalance(@RequestParam(value = "userId", required = false) Long userId) {
        return paymentsService.findPayments(userId);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/stat/recognition/recaptcha", produces = "application/json")
    public ResponseEntity<List<RecognitionStatsDto>> getRecaptchaRecognitionStats(
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<RecognitionStatsEntity> statsEntities = recognitionStatsService
                    .getRecaptchaStats(accountId, startDate,
                             endDate);
            List<RecognitionStatsDto> statsDtos = statsEntities.stream()
                    .map(recognitionStatsMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(statsDtos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/stat/financial/transactions", produces = "application/json")
    public ResponseEntity<List<FinancialStatsDto>> getFinancialStats(
            @RequestParam(required = false) Integer accountId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            List<FinancialStatsEntity> statsEntities = financialStatsService
                    .getFinancialStats(accountId,
                            startDate.atStartOfDay(),
                            endDate.atTime(23, 59, 59, 999999999));
            List<FinancialStatsDto> statsDtos = statsEntities.stream()
                    .map(financialStatsMapper::toDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(statsDtos);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}
