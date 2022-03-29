package app.programmatic.ui.yandexmediastatus.service.impl;

import app.programmatic.ui.yandexmediastatus.dao.model.BidLossNotificationsEntity;
import app.programmatic.ui.yandexmediastatus.dao.repository.BidLossNotificationsRepository;
import app.programmatic.ui.yandexmediastatus.service.BidLossNotificationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BidLossNotificationsServiceImpl implements BidLossNotificationsService {
    private static final String BIG_DATA_INSERT_QUERY = "INSERT INTO yandex_notification (*) VALUES (now(), generateUUIDv4(), ?)";

    private final BidLossNotificationsRepository notificationsRepository;
    private final JdbcOperations bigDataJdbcOperations;

    public BidLossNotificationsServiceImpl(@Autowired BidLossNotificationsRepository notificationsRepository,
                                           @Autowired @Qualifier("bigDataOperations") JdbcOperations bigDataJdbcOperations) {
        this.notificationsRepository = notificationsRepository;
        this.bigDataJdbcOperations = bigDataJdbcOperations;
    }

    @Override
    public List<BidLossNotificationsEntity> list() {
        return notificationsRepository.findAll();
    }

    @Override
    public BidLossNotificationsEntity add(BidLossNotificationsEntity entity) {
        return notificationsRepository.save(entity);
    }

    @Override
    public void addAsPlainJson(String json) {
        bigDataJdbcOperations.update(BIG_DATA_INSERT_QUERY, json);
    }
}
