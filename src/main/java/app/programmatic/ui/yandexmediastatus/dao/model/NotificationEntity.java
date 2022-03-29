package app.programmatic.ui.yandexmediastatus.dao.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false, updatable = false)
    private Long id;

    @NotNull
    @Column(name = "request_id")
    private String requestId;

    @Column(name = "impression_id")
    private String impressionId;

    @NotNull
    private Integer status;

    private String payload;

}