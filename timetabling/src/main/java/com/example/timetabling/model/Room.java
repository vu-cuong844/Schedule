package com.example.timetabling.model;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    @Id
    private String name;

    @ElementCollection
    @CollectionTable(name = "room_subject", joinColumns = @JoinColumn(name = "name_room"))
    @Column(nullable = false)
    private List<String> subjectCodes;

    @Column(nullable = false)
    private int slMax;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Type type;
}
