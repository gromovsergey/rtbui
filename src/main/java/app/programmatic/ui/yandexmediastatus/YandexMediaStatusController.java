package app.programmatic.ui.yandexmediastatus;

import app.programmatic.ui.yandexmediastatus.converter.BidLossNotificationsMapper;
import app.programmatic.ui.yandexmediastatus.dao.model.BidLossNotificationsEntity;
import app.programmatic.ui.yandexmediastatus.dto.BidLossNotificationsDto;
import app.programmatic.ui.yandexmediastatus.service.BidLossNotificationsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/notifications/yandex")
public class YandexMediaStatusController {
    private static final Logger logger = Logger.getLogger(YandexMediaStatusController.class.getName());

    private final BidLossNotificationsMapper notificationsMapper;
    private final BidLossNotificationsService notificationsService;

    @Transactional
    @RequestMapping(value = "v2/list", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<BidLossNotificationsDto>> getNotifications() {
        List<BidLossNotificationsEntity> entities = notificationsService.list();
        List<BidLossNotificationsDto> result = entities.stream().map(notificationsMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @Transactional
    @RequestMapping(value = "v2/add", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<BidLossNotificationsDto> add(@RequestBody BidLossNotificationsDto requestDto) {
        BidLossNotificationsEntity entity = notificationsService.add(notificationsMapper.toEntity(requestDto));
        BidLossNotificationsDto resultDto = notificationsMapper.toDto(entity);
        return ResponseEntity.ok(resultDto);
    }

    @RequestMapping(value = "v1/list", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<List<String>> getNotificationsAsPlainJson() {
        return null;
    }

    @RequestMapping(value = "v1/add", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity addAsPlainJson(HttpEntity<String> httpEntity) {
        try {
            String body = httpEntity.getBody();
            logger.info("Yandex notification: " + body);

            notificationsService.addAsPlainJson(body);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Can't process yandex notification: " + e.getMessage(), e);
        }

        return ResponseEntity.unprocessableEntity().build();
    }
}
