package com.example.timetabling.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.timetabling.algorithm.ScheduleRoom;
import com.example.timetabling.algorithm.ScheduleTeacher;
import com.example.timetabling.dto.algorithm.ClassRoomAssignment;
import com.example.timetabling.dto.algorithm.ClassTeacherAssignmentInput;
import com.example.timetabling.dto.algorithm.ClassTeacherAssignmentOutput;
import com.example.timetabling.dto.request.ScheduleRequest;
import com.example.timetabling.dto.response.Response;
import com.example.timetabling.model.Class;
import com.example.timetabling.model.Room;
import com.example.timetabling.model.Subject;
import com.example.timetabling.model.Teacher;
import com.example.timetabling.model.TimeTable;
import com.example.timetabling.model.Type;
import com.example.timetabling.utils.Concat;

@Service
public class ScheduleService {
    @Autowired
    private ScheduleRoom scheduleRoom;

    @Autowired
    private ScheduleTeacher scheduleTeacher;

    @Autowired
    private RoomService roomService;

    @Autowired
    private TeacherService teacherService;

    @Autowired
    private SubjectSevice subjectSevice;

    @Autowired
    private TimeTableService timeTableService;


    public Response<TimeTable> schedule(ScheduleRequest request) {
        if (request.getRooms() == null || request.getRooms().isEmpty()) {
            request.setRooms(roomService.getAll());
        }

        if (request.getTeachers() == null || request.getTeachers().isEmpty()) {
            request.setTeachers(teacherService.getAll());
        }

        // xếp lớp tn
        List<Class> classes = request.getClasses();
        List<Class> classesTN = classes.stream().filter(c -> c.getType() == Type.TN).collect(Collectors.toList());
        List<Class> classesGD = classes.stream().filter(c -> c.getType() == Type.GD).collect(Collectors.toList());

        List<Room> rooms = request.getRooms();
        List<Room> roomTNs = rooms.stream().filter(r -> r.getType() == Type.TN).collect(Collectors.toList());
        List<Room> roomGDs = rooms.stream().filter(r -> r.getType() == Type.GD).collect(Collectors.toList());

        List<ClassRoomAssignment> classRoomAssignmentTNs = scheduleRoom.scheduleClassToRoomTN(classesTN, roomTNs);
        List<ClassRoomAssignment> classRoomAssignmentGDs = scheduleRoom.scheduleClassToRoomGD(classesGD, roomGDs);

        List<ClassRoomAssignment> classRoomAssignments = new ArrayList<>();
        classRoomAssignmentGDs.addAll(classRoomAssignmentTNs);
        classRoomAssignments.addAll(classRoomAssignmentGDs);

        if (classRoomAssignments.isEmpty() || classRoomAssignments.size() == 0) {
            return Response.<TimeTable>builder()
                .data(null)
                .success(false)
                .message("Không xếp được lớp")
                .build();
        }

        Concat concat = new Concat();

        // phân công giảng viên
        List<ClassTeacherAssignmentInput> inputs = concat.concatCLassTeacherInput(classRoomAssignments);
        List<Teacher> teachers = request.getTeachers();
        List<ClassTeacherAssignmentOutput> outputs = scheduleTeacher.scheduleTeacherToClass(inputs, teachers);

        // tạo thời khóa biểu
        List<Subject> subjects = subjectSevice.getAllSubject();
        TimeTable timeTable = concat.concatClassTeacherAssignmentOutput(outputs, teachers, rooms, subjects, request.getTerm());

        return Response.<TimeTable>builder()
                .data(timeTable)
                .success(true)
                .message("Successfully")
                .build();
    }

    public Response<TimeTable> confirm(TimeTable timeTable) {
        return timeTableService.createTimeTable(timeTable);

    }
}
