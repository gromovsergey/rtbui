package app.programmatic.ui.yandexmediastatus.converter;


import app.programmatic.ui.yandexmediastatus.dao.model.BidLossNotificationsEntity;
import app.programmatic.ui.yandexmediastatus.dto.BidLossNotificationsDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface BidLossNotificationsMapper {
    BidLossNotificationsEntity toEntity(BidLossNotificationsDto dto);
    BidLossNotificationsDto toDto(BidLossNotificationsEntity entity);
}
