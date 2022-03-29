package app.programmatic.ui.kickcaptcha.converter;

import app.programmatic.ui.kickcaptcha.dto.FinancialStatsDto;
import app.programmatic.ui.kickcaptcha.model.FinancialStatsEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface FinancialStatsMapper {

    FinancialStatsDto toDto(FinancialStatsEntity entity);

}
