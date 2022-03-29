package app.programmatic.ui.yandexmediastatus.dao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "bid_loss_notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BidLossNotificationsEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, updatable = false)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "notification_id")
    private List<NotificationEntity> notifications;
}
