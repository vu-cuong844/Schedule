package com.example.schedule.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.example.schedule.model.Class;
import com.example.schedule.model.Room;
import com.example.schedule.model.ScheduleResult;
import com.example.schedule.model.Type;

public class ExcelUtil {
    private static final String[] DAYS_GD = { "Mon", "Tue", "Wed", "Thu", "Fri" };

    public List<Class> readClassFromPathFile(String pathFile, Type type) throws IOException {
        List<Class> classes = new ArrayList<>();

        try (FileInputStream fis = new FileInputStream(pathFile);
                Workbook workbook = new XSSFWorkbook(pathFile)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;
            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false;
                    continue;
                }
                Type type_class = Type.valueOf(row.getCell(5).getStringCellValue());
                if (type == type_class) {
                    Class class_ = Class.builder()
                            .id(String.valueOf((int) row.getCell(0).getNumericCellValue()))
                            .maHP(row.getCell(2).getStringCellValue())
                            .slMax((int) row.getCell(4).getNumericCellValue())
                            .type(type)
                            .thoiLuong((int) row.getCell(6).getNumericCellValue())
                            .build();
                    classes.add(class_);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return classes;
    }

    public List<Room> readRoomFromPathFile(String pathFile, Type type) throws IOException {
        List<Room> rooms = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(pathFile);
                Workbook workbook = new XSSFWorkbook(pathFile)) {
            Sheet sheet = workbook.getSheetAt(0);
            boolean firstRow = true;
            for (Row row : sheet) {
                if (firstRow) {
                    firstRow = false;
                    continue;
                }
                Type type_class = Type.valueOf(row.getCell(3).getStringCellValue());
                if (type == type_class) {
                    String input = row.getCell(1).getStringCellValue();
                    input = input.replaceAll("[\\[\\]' ]", "");
                    List<String> code_subject = Arrays.asList(input.split(","));

                    Room room = Room.builder()
                            .name(row.getCell(0).getStringCellValue())
                            .maHPs(code_subject)
                            .slMax((int) row.getCell(2).getNumericCellValue())
                            .type(type)
                            .build();
                    rooms.add(room);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return rooms;
    }

    public void writeResultToFile(List<ScheduleResult> data, String pathFile) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Lịch học");

        // Tạo header
        Row headerRow = sheet.createRow(0);
        String[] columns = { "Mã_lớp", "Mã_HP", "Thời_lượng", "Phòng", "Tuần", "Ngày", "Tiết_bắt_đầu", "Tiết_kết_thúc",
                "Loại" };
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);
        }

        int rowNum = 1;
        for (ScheduleResult result : data) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(result.getId());
            row.createCell(1).setCellValue(result.getMaHP());
            row.createCell(2).setCellValue(result.getThoiLuong());
            row.createCell(3).setCellValue(result.getRoom());
            row.createCell(4).setCellValue(result.getWeek());
            row.createCell(5).setCellValue(DAYS_GD[result.getNgay() - 1]);
            row.createCell(6).setCellValue(result.getStart());
            row.createCell(7).setCellValue(result.getEnd());
            row.createCell(8).setCellValue(result.getType().toString());
        }

        // Tự động điều chỉnh độ rộng cột
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Ghi ra file
        try (FileOutputStream outputStream = new FileOutputStream(pathFile)) {
            workbook.write(outputStream);
            System.out.println("Đã ghi ra file: " + pathFile);
        }

        workbook.close();
    }

}
