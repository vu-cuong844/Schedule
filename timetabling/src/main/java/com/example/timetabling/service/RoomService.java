package com.example.timetabling.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.timetabling.dto.response.Response;
import com.example.timetabling.model.Room;
import com.example.timetabling.repo.RoomRepository;

@Service
public class RoomService {
    @Autowired
    private RoomRepository roomRepository;

    public List<Room> getAll() {
        return roomRepository.findAll();
    }

    public Response<List<Room>> createRooms(List<Room> rooms) {
        try {
            List<Room> savedRoom = roomRepository.saveAll(rooms);
            return Response.<List<Room>>builder()
                    .data(savedRoom)
                    .success(true)
                    .message("Saved successfully " + savedRoom.size() + " record")
                    .build();
        } catch (Exception e) {
            return Response.<List<Room>>builder()
                    .data(null)
                    .success(false)
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }

    @Transactional
    public Response<Room> updateRoom(Room room) {
        try {
            Room existingRoom = roomRepository.findById(room.getName()).orElse(null);

            if (existingRoom == null) {
                return Response.<Room>builder()
                        .data(null)
                        .success(false)
                        .message("Not found " + room.getName())
                        .build();
            }

            Room updatedRoom = roomRepository.save(room);
            return Response.<Room>builder()
                    .data(updatedRoom)
                    .success(true)
                    .message("Update successfully.")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Updated room failed: " + e.getMessage());
        }
    }

    public Response<String> deleteRoom(String name) {
        try {
            roomRepository.deleteByName(name);
            return Response.<String>builder()
                    .data("")
                    .success(true)
                    .message("Deleted successfully.")
                    .build();
        } catch (Exception e) {
            return Response.<String>builder()
                    .data("")
                    .success(true)
                    .message("Erroe: " + e.getMessage())
                    .build();
        }
    }

    public Response<List<Room>> searchRoom(String name) {
        try {
            List<Room> rooms = roomRepository.findByName(name);
            return Response.<List<Room>>builder()
                    .data(rooms)
                    .success(true)
                    .message("Found " + rooms.size() + " record.")
                    .build();
        } catch (Exception e) {
            return Response.<List<Room>>builder()
                    .data(null)
                    .success(true)
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }
}
