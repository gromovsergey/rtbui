package app.programmatic.ui.kickcaptcha.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RecognitionStatsIdEntity implements Serializable {

    Integer accountId;
    LocalDate date;
    Integer hour;
}
