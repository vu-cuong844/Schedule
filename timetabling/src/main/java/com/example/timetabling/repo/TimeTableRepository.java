package com.example.timetabling.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.timetabling.model.TimetableItem;

@Repository
public interface TimeTableRepository extends JpaRepository<TimetableItem, String>{
    @Query("SELECT t FROM TimetableItem t WHERE t.teacherCode = :teacherCode AND t.term = :term")
    List<TimetableItem> findByTeacherCodeAndTerm(@Param("teacherCode") String teacherCode, @Param("term") int term);
}
