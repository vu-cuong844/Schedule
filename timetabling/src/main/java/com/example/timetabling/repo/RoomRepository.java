package com.example.timetabling.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.timetabling.model.Room;

@Repository
public interface RoomRepository extends JpaRepository<Room, String>{
    Optional<Room> findById(String id);

    @Modifying
    @Transactional
    @Query("DELETE FROM Room r WHERE r.name = :name")
    void deleteByName(@Param("name") String name);

    @Query("SELECT r FROM Room r WHERE LOWER(r.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<Room> findByName(@Param("name") String name);
}
