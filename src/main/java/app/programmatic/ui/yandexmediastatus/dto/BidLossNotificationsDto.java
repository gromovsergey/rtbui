package app.programmatic.ui.yandexmediastatus.dto;

import lombok.Data;

import java.util.List;

@Data
public class BidLossNotificationsDto {
    private Long id;
    private List<NotificationDto> notifications;
}
