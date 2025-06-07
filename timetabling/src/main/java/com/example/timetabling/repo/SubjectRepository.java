package com.example.timetabling.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.timetabling.model.Subject;
import java.util.Optional;
import java.util.List;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, String> {
    Optional<Subject> findById(String id);

    @Query("SELECT s FROM Subject s WHERE LOWER(s.nameSubject) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "OR LOWER(s.nameEnglishSubject) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Subject> findByNameOrEnglishName(@Param("keyword") String keyword);

    @Modifying
    @Transactional
    @Query("DELETE FROM Subject s WHERE s.subjectCode = :id")
    void deldeleteBySubjectCode(@Param("id") String id);

}
