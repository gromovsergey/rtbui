package app.programmatic.ui.yandexmediastatus.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationDto {
    @JsonProperty("requestid")
    private String requestId;

    @JsonProperty("impressionid")
    private String impressionId;

    private Integer status;

    private String payload;
}
