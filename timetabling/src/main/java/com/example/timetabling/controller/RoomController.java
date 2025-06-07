package com.example.timetabling.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.service.annotation.DeleteExchange;

import com.example.timetabling.config.UserContext;
import com.example.timetabling.config.UserContextHolder;
import com.example.timetabling.dto.response.Response;
import com.example.timetabling.model.Room;
import com.example.timetabling.service.RoomService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/schedule/room")
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/")
    public ResponseEntity<Response<List<Room>>> createRooms(@RequestBody List<Room> rooms) {
        UserContext userContext = UserContextHolder.getContext();
        if (!hasAdminRole(userContext)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.<List<Room>>builder()
                    .success(false)
                    .message("Không có quyền thực hiện thao tác này")
                    .build());
        }

        Response<List<Room>> response = roomService.createRooms(rooms);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/")
    public ResponseEntity<Response<Room>> updateRoom(@RequestBody Room room) {
        try {
            UserContext userContext = UserContextHolder.getContext();
            if (!hasAdminRole(userContext)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Response.<Room>builder()
                        .success(false)
                        .message("Không có quyền thực hiện thao tác này")
                        .build());
            }

            Response<Room> response = roomService.updateRoom(room);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.ok(Response.<Room>builder()
                    .success(false)
                    .message(e.getMessage())
                    .build());
        }
    }

    @DeleteExchange("/{name}")
    public ResponseEntity<Response<String>> deleteRoom(@PathVariable String name) {
        UserContext userContext = UserContextHolder.getContext();
        if (!hasAdminRole(userContext)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Response.<String>builder()
                    .success(false)
                    .message("Không có quyền thực hiện thao tác này")
                    .build());
        }

        Response<String> response = roomService.deleteRoom(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{name}")
    public ResponseEntity<Response<List<Room>>> searchRoom(@PathVariable String name) {
        // Không cần kiểm tra quyền với thao tác đọc
        Response<List<Room>> response = roomService.searchRoom(name);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/")
    public ResponseEntity<List<Room>> getAllRooms() {
        // Không cần kiểm tra quyền với thao tác đọc
        List<Room> response = roomService.getAll();
        return ResponseEntity.ok(response);
    }

    private boolean hasAdminRole(UserContext userContext) {
        return userContext != null && 
               userContext.getRole() != null && 
               userContext.getRole().contains("PDT");
    }
}