package com.example.timetabling.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.timetabling.dto.response.Response;
import com.example.timetabling.model.TimeTable;
import com.example.timetabling.model.TimetableItem;
import com.example.timetabling.repo.TimeTableRepository;

@Service
public class TimeTableService {

    @Autowired
    private TimeTableRepository tableRepository;

    public Response<TimeTable> createTimeTable(TimeTable timeTable) {
        try {
            List<TimetableItem> items = tableRepository.saveAll(timeTable.getItems());
            TimeTable savedTimeTable = TimeTable.builder()
                    .items(items)
                    .term(items.get(0).getTerm())
                    .build();
            return Response.<TimeTable>builder()
                    .data(savedTimeTable)
                    .success(true)
                    .message("Created new timetable for " + savedTimeTable.getTerm() + " term.")
                    .build();
        } catch (Exception e) {
            return Response.<TimeTable>builder()
                    .data(null)
                    .success(false)
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }

    public Response<TimeTable> getTimeTableOf(String teacherCode, int term) {
        try {
            List<TimetableItem> items = tableRepository.findByTeacherCodeAndTerm(teacherCode, term);
            return Response.<TimeTable>builder()
                    .data(TimeTable.builder().items(items).term(term).build())
                    .success(true)
                    .message("Found " + items.size() + "class" + (items.size() > 1 ? "es." : "."))
                    .build();

        } catch (Exception e) {
            return Response.<TimeTable>builder()
                    .data(null)
                    .success(false)
                    .message("Error: " + e.getMessage())
                    .build();
        }
    }
}
