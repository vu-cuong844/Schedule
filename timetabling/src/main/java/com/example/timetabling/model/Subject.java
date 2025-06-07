package com.example.timetabling.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subjects")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subject {

   @Id
   private String subjectCode;

   @Column(nullable = false)
   private String nameSubject;

   @Column(nullable = false)
   private String nameEnglishSubject;

   @Column(nullable = false)
   private int count;

   @Column(nullable = false)
   private boolean requestTN;

   @Column(nullable = false)
   private int lt;

   @Column(nullable = false)
   private int bt;

   @Column(nullable = false)
   private int tn;

   @Column(nullable = false)
   private int tuhoc; 

   
}
