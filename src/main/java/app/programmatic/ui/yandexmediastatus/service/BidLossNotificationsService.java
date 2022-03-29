package app.programmatic.ui.yandexmediastatus.service;

import app.programmatic.ui.yandexmediastatus.dao.model.BidLossNotificationsEntity;

import java.util.List;

public interface BidLossNotificationsService {
    List<BidLossNotificationsEntity> list();
    BidLossNotificationsEntity add(BidLossNotificationsEntity entity);
    void addAsPlainJson(String json);
}
