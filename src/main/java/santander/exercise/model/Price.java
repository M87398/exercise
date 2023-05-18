package santander.exercise.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class Price {
    private Long id;
    private String instrumentName;
    private BigDecimal bid;
    private BigDecimal ask;
    private LocalDateTime timestamp;
}
