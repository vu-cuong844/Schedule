package com.example.timetabling.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.example.timetabling.dto.algorithm.ClassRoomAssignment;
import com.example.timetabling.dto.algorithm.ClassTeacherAssignmentInput;
import com.example.timetabling.dto.algorithm.ClassTeacherAssignmentOutput;
import com.example.timetabling.model.Room;
import com.example.timetabling.model.Subject;
import com.example.timetabling.model.Teacher;
import com.example.timetabling.model.TimeTable;
import com.example.timetabling.model.TimetableItem;
import com.example.timetabling.model.Type;

public class Concat {

    private final Map<Integer, Integer> timestart;
    {
        Map<Integer, Integer> tempMap = new HashMap<>();
        tempMap.put(1, 645);
        tempMap.put(2, 730);
        tempMap.put(3, 825);
        tempMap.put(4, 920);
        tempMap.put(5, 1015);
        tempMap.put(6, 1100);
        tempMap.put(7, 1230);
        tempMap.put(8, 1315);
        tempMap.put(9, 1410);
        tempMap.put(10, 1505);
        tempMap.put(11, 1600);
        tempMap.put(12, 1645);
        timestart = java.util.Collections.unmodifiableMap(tempMap);
    }
    private final Map<Integer, Integer> timeEnd;
    {
        Map<Integer, Integer> tempMap = new HashMap<>();
        tempMap.put(1, 730);
        tempMap.put(2, 815);
        tempMap.put(3, 910);
        tempMap.put(4, 1005);
        tempMap.put(5, 1100);
        tempMap.put(6, 1145);
        tempMap.put(7, 1315);
        tempMap.put(8, 1400);
        tempMap.put(9, 1455);
        tempMap.put(10, 1550);
        tempMap.put(11, 1645);
        tempMap.put(12, 1730);
        timeEnd = java.util.Collections.unmodifiableMap(tempMap);
    }

    public List<ClassTeacherAssignmentInput> concatCLassTeacherInput(List<ClassRoomAssignment> classes) {
        Map<String, List<ClassRoomAssignment>> groupById = new HashMap<>();

        for (ClassRoomAssignment classRoomAssignment : classes) {
            String key = classRoomAssignment.getId();

            groupById.computeIfAbsent(key, k -> new ArrayList<>()).add(classRoomAssignment);
        }

        List<ClassTeacherAssignmentInput> classTeacherAssignmentInputs = new ArrayList<>();

        for (List<ClassRoomAssignment> classRoomAssignments : groupById.values()) {
            ClassRoomAssignment item = classRoomAssignments.get(0);

            List<Integer> weeks = classRoomAssignments.stream().map(ClassRoomAssignment::getWeek).sorted()
                    .collect(Collectors.toList());

            ClassTeacherAssignmentInput classTeacherAssignmentInput = new ClassTeacherAssignmentInput();
            classTeacherAssignmentInput.setId(item.getId());
            classTeacherAssignmentInput.setMaHP(item.getMaHP());
            classTeacherAssignmentInput.setThoiLuong(item.getThoiLuong());
            classTeacherAssignmentInput.setRoom(item.getRoom());
            classTeacherAssignmentInput.setWeeks(weeks);
            classTeacherAssignmentInput.setNgay(item.getNgay());
            classTeacherAssignmentInput.setStart(item.getStart());
            classTeacherAssignmentInput.setEnd(item.getEnd());
            classTeacherAssignmentInput.setType(item.getType());
            classTeacherAssignmentInput.setTime_teaching(item.getTime_teaching());
            classTeacherAssignmentInput.setSlMax(item.getSlMax());

            classTeacherAssignmentInputs.add(classTeacherAssignmentInput);
        }
        return classTeacherAssignmentInputs;
    }

    public TimeTable concatClassTeacherAssignmentOutput(List<ClassTeacherAssignmentOutput> classes,
            List<Teacher> teachers, List<Room> rooms, List<Subject> subjects, int term) {

        Map<String, Teacher> teacherMap = teachers.stream()
                .collect(Collectors.toMap(
                        Teacher::getTeacherCode,
                        teacher -> teacher));

        Map<String, Subject> subjectMap = subjects.stream()
                .collect(Collectors.toMap(
                        Subject::getSubjectCode,
                        subject -> subject));

        List<ClassTeacherAssignmentOutput> classTN = classes.stream()
                .filter(c -> c.getType() == Type.TN)
                .collect(Collectors.toList());
        List<ClassTeacherAssignmentOutput> classGD = classes.stream()
                .filter(c -> c.getType() == Type.GD)
                .collect(Collectors.toList());

        List<TimetableItem> timetableItems = new ArrayList<>();
        List<Integer> weeks = new ArrayList<>();
        for (int i = 1; i <= 16; i++) {
            weeks.add(i);
        }
        // xử lý lớp gd
        for (ClassTeacherAssignmentOutput c : classGD) {

            TimetableItem timetableItem = TimetableItem.builder()
                    .idClass(c.getId())
                    .idSubject(c.getMaHP())
                    .nameSubject(subjectMap.get(c.getMaHP()).getNameSubject())
                    .nameEnglishSubject(subjectMap.get(c.getMaHP()).getNameEnglishSubject())
                    .day(c.getDay())
                    .timeStart(timestart.get(c.getStart()))
                    .timeENd(timeEnd.get(c.getEnd()))
                    .start(c.getStart())
                    .end(c.getEnd())
                    .session(c.getEnd() <= 6 ? "Sáng" : "Chiều")
                    .weeks(weeks)
                    .room(c.getRoom())
                    .slMax(c.getSlMax())
                    .type(c.getType())
                    .managementCode("sis")
                    .teachingType("trực tiếp")
                    .teacherName(teacherMap.get(c.getTeacherCode()).getName())
                    .teacherCode(c.getTeacherCode())
                    .term(term)
                    .requetTN(subjectMap.get(c.getMaHP()).isRequestTN())
                    .weight(String.valueOf(subjectMap.get(c.getMaHP()).getCount()))
                    .build();

            timetableItems.add(timetableItem);
        }

        // xử lý lớp TN
        Map<String, List<Integer>> class_week = new HashMap<>();
        Map<String, Boolean> class_assign = new HashMap<>();

        for (ClassTeacherAssignmentOutput c : classTN) {
            String id = c.getId(); // Lấy id
            Integer week = c.getWeek(); // Lấy week

            // Lấy danh sách tuần hiện tại cho id, nếu chưa có thì tạo mới
            List<Integer> wks = class_week.getOrDefault(id, new ArrayList<>());
            wks.add(week); // Thêm tuần vào danh sách
            class_week.put(id, wks); // Cập nhật lại map

            class_assign.putIfAbsent(id, false);
        }

        for (ClassTeacherAssignmentOutput c : classTN) {
            if (class_assign.get(c.getId())) {
                continue;
            }

            TimetableItem timetableItem = TimetableItem.builder()
                    .idClass(c.getId())
                    .idSubject(c.getMaHP())
                    .nameSubject(subjectMap.get(c.getMaHP()).getNameSubject())
                    .nameEnglishSubject(subjectMap.get(c.getMaHP()).getNameEnglishSubject())
                    .day(c.getDay())
                    .timeStart(timestart.get(c.getStart()))
                    .timeENd(timeEnd.get(c.getEnd()))
                    .start(c.getStart())
                    .end(c.getEnd())
                    .session(c.getEnd() <= 6 ? "Sáng" : "Chiều")
                    .weeks(class_week.get(c.getId()))
                    .room(c.getRoom())
                    .slMax(c.getSlMax())
                    .type(c.getType())
                    .managementCode("sis")
                    .teachingType("Trực tiếp")
                    .teacherName(teacherMap.get(c.getTeacherCode()).getName())
                    .teacherCode(c.getTeacherCode())
                    .term(term)
                    .requetTN(subjectMap.get(c.getMaHP()).isRequestTN())
                    .weight(String.valueOf(subjectMap.get(c.getMaHP()).getCount()))
                    .build();

            timetableItems.add(timetableItem);
            class_assign.put(c.getId(), true);
        }

        return TimeTable.builder()
                .term(term)
                .items(timetableItems)
                .build();
    }

}
