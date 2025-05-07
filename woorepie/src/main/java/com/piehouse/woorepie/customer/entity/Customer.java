package com.piehouse.woorepie.customer.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name="customer")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(toBuilder = true)
@ToString(exclude = "customerPassword")
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Customer {

    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long customerId;

    @Column(length = 10, nullable = false)
    private String customerName;

    @Column(length = 320, nullable = false, unique = true)
    private String customerEmail;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(length = 255, nullable = false)
    private String customerPassword;

    @Column(length = 20, nullable = false, unique = true)
    private String customerPhoneNumber;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime customerJoinDate;

    private LocalDate customerDateOfBirth;

    @Column(length = 100, nullable = false)
    private String customerAddress;

    @Column(length = 20, nullable = false, unique = true)
    private String accountNumber;

    @Builder.Default
    private Integer accountBalance = 0;

    @Column(length = 100, nullable = false, unique = true)
    private String customerKyc;

    @Column(length = 1000, nullable = false, unique = true)
    private String customerIdentificationUrl;

}
