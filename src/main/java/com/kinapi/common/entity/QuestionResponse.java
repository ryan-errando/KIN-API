package com.kinapi.common.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.hibernate.type.SqlTypes;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "question_response")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamicInsert
@DynamicUpdate
public class QuestionResponse implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    @JdbcTypeCode(SqlTypes.UUID)
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "response_text", columnDefinition = "TEXT")
    private String responseText;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "mood_rating")
    private String moodRating;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "familyMember_id")
    private FamilyMembers familyMember;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="dailyQuestion_id ")
    private DailyQuestion dailyQuestion;

}
