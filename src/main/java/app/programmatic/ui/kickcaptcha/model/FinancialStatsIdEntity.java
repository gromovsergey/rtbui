package app.programmatic.ui.kickcaptcha.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FinancialStatsIdEntity implements Serializable {

    Integer accountId;
    String txId;
}
