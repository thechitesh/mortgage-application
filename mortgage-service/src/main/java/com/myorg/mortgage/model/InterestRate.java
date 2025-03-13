package com.myorg.mortgage.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
@EqualsAndHashCode
public class InterestRate implements Comparable<InterestRate> {

    private Integer maturityPeriod;
    private BigDecimal interestRate;
    private OffsetDateTime lastUpdate;

    @Override
    public int compareTo(InterestRate other) {
        return Integer.compare(this.maturityPeriod, other.maturityPeriod);
    }
}
