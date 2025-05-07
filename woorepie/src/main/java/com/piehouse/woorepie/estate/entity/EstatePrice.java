package com.piehouse.woorepie.estate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "estate_price")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@ToString(exclude = "estate")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EstatePrice {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long estatePriceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estate_id", nullable = false)
    private Estate estate;

    @Column(nullable = false)
    private Integer estatePrice;

    @Column(nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime estatePriceDate;

}
