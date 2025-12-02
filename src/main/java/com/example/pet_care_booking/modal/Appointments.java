package com.example.pet_care_booking.modal;

import com.example.pet_care_booking.modal.enums.PetGender;
import com.example.pet_care_booking.modal.enums.PetType;
import com.example.pet_care_booking.modal.enums.AppointStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "appointments")
public class Appointments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "owner_name", nullable = false)
    private String ownerName;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "pet_name", nullable = false)
    private String petName;

    @Column(name = "pet_type")
    private PetType petType;

    @Column(name = "pet_age")
    private int age;

    @Column(name = "pet_gender")
    private PetGender petGender;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private AppointStatus appointStatus;

    @Column(name = "note")
    private String note;


    @Column(name = "start_time", nullable = true)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = true)
    private LocalDateTime endTime;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "vet_id")
    private Veterinarians veterinarian;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "user_id")
    private User user;


    @ManyToMany()
    @JoinTable(
            name = "pet_examination",
            joinColumns = @JoinColumn(name = "appointment_id"),
            inverseJoinColumns = @JoinColumn(name = "examination_id")
    )
    private List<Examination> examination;

}
