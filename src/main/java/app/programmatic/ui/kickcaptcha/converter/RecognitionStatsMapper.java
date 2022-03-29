package app.programmatic.ui.kickcaptcha.converter;

import app.programmatic.ui.kickcaptcha.dto.RecognitionStatsDto;
import app.programmatic.ui.kickcaptcha.model.RecognitionStatsEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface RecognitionStatsMapper {

    RecognitionStatsDto toDto(RecognitionStatsEntity entity);
    RecognitionStatsEntity toEntity(RecognitionStatsDto dto);

}
