package com.kinapi.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/*
ENTITY
- KASARNYA KEA TABLE DI DATABASE
- MAKE SURE SEMUA FIELD MATCHING (NAMA, TYPE, SIFAT(NULLABLE))
- INI GABOLE DI PASSING MENTAH-MENTAH KE FRONTEND
 */

@Getter
@Setter
@Entity
@Table(name = "users")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class Users implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "dob") // VARCHAR -> MISAL VARCHAR(255) LU CUMA BOLE SAVE SAMPE 255 BYTE
    private LocalDate dob;

    @Column(name = "avatar_url", columnDefinition = "TEXT") // TEXT -> STRING YANG PANJANGNYA FLEXIBLE
    private String avatarUrl;

    @Column(name = "created_at")
    @CreationTimestamp // WAKTU BIKIN RECORD DIA BAKALAN SAVE TIMESTAMP WKTU SEBUAH RECORD DI SAVE KE DALAM DATABASE
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp // WAKTU KITA MODIFY/UPDATE DATA DIA BAKALAN UPDATE TIMESTAMP DARI RECORD TERSEBUT
    private LocalDateTime updatedAt;

    @Column(name = "is_in_group")
    private Boolean isInGroup;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TopicHistory> topicHistories;

    @OneToOne(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private FamilyGroups familyGroups;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private FamilyMembers familyMembers;
}
