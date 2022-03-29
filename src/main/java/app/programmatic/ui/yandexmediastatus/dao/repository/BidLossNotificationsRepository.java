package app.programmatic.ui.yandexmediastatus.dao.repository;

import app.programmatic.ui.yandexmediastatus.dao.model.BidLossNotificationsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidLossNotificationsRepository extends JpaRepository<BidLossNotificationsEntity, Long> {
}
