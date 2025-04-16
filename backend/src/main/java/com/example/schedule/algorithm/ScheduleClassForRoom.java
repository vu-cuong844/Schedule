package com.example.schedule.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.schedule.model.Room;
import com.example.schedule.dto.ScheduleRoomResponse;
import com.example.schedule.model.Class;
import com.example.schedule.model.ScheduleResult;
import com.example.schedule.model.Type;
import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;

@Component
public class ScheduleClassForRoom {
    private static final String[] DAYS_GD = { "Mon", "Tue", "Wed", "Thu", "Fri" };
    private static final String[] DAYS_TN = { "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" };
    private static final int[] WEEKS = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16 };
    private static final int PERIODS_PER_DAY = 12;
    private static final int TIME_LIMIT_SECONDS = 20;
    private static final int NUM_WEEKS_REQUIRED = 5;

    public ScheduleRoomResponse scheduleClassToRoomGD(List<Class> classes, List<Room> rooms) {
        // Your logic here
        Loader.loadNativeLibraries();

        // Kiểm tra xem có lớp nào không thể xếp do sĩ số hoặc mã HP không khớp
        List<Class> unassignableClasses = classes.stream()
                .filter(c -> rooms.stream()
                        .noneMatch(r -> c.getSlMax() <= r.getSlMax() && r.getMaHPs().contains(c.getMaHP())))
                .collect(Collectors.toList());

        if (!unassignableClasses.isEmpty()) {
            return ScheduleRoomResponse.builder()
                    .classes(Collections.emptyList())
                    .isScheduled(false)
                    .message("Could not find a suitable schedule.")
                    .unassignableClasses(unassignableClasses)
                    .build();
        }
        // Phân tích thời lượng
        // int totalDuration = classes.stream().mapToInt(Class::getThoiLuong).sum();
        // double avgDuration =
        // classes.stream().mapToInt(Class::getThoiLuong).average().orElse(0);
        // System.out.println("Tổng số tiết mỗi tuần: " + totalDuration);
        // System.out.printf("Thời lượng trung bình mỗi lớp: %.2f tiết%n", avgDuration);

        // Tạo mô hình CP-SAT
        CpModel model = new CpModel();

        // Biến x: x[class, room, day, start_period] = 1 nếu lớp được xếp vào phòng,
        // ngày, tiết bắt đầu
        Map<String, Literal> x = new HashMap<>();

        for (Class c : classes) {
            for (Room r : rooms) {
                for (int d = 0; d < DAYS_GD.length; d++) {
                    for (int p = 1; p <= PERIODS_PER_DAY - c.getThoiLuong() + 1; p++) {
                        // Chỉ tạo biến nếu phòng phù hợp về sĩ số và mã HP
                        if (c.getSlMax() <= r.getSlMax() && r.getMaHPs().contains(c.getMaHP())) {
                            String varName = String.format("x_%s_%s_%d_%d", c.getId(), r.getName(), d, p);
                            x.put(varName, model.newBoolVar(varName));
                        }
                    }
                }
            }
        }

        // Thêm biến y[room] = 1 nếu phòng được sử dụng
        Map<String, Literal> y = new HashMap<>();
        for (Room r : rooms) {
            String varName = String.format("y_%s", r.getName());
            y.put(varName, model.newBoolVar(varName));
        }

        // Ràng buộc 1: Mỗi lớp GD được xếp đúng 1 lần
        for (Class c : classes) {
            List<Literal> classAssignments = new ArrayList<>();
            for (Room r : rooms) {
                for (int d = 0; d < DAYS_GD.length; d++) {
                    for (int p = 1; p <= PERIODS_PER_DAY - c.getThoiLuong() + 1; p++) {
                        String varName = String.format("x_%s_%s_%d_%d", c.getId(), r.getName(), d, p);
                        if (x.containsKey(varName)) {
                            classAssignments.add(x.get(varName));
                        }
                    }
                }
            }

            model.addExactlyOne(classAssignments.toArray(new Literal[0]));
        }

        // Ràng buộc 2: Không trùng phòng (kiểm tra từng tiết trong mỗi ngày)
        for (Room r : rooms) {
            for (int d = 0; d < DAYS_GD.length; d++) {
                for (int p = 1; p <= PERIODS_PER_DAY; p++) {
                    List<Literal> overlappingClasses = new ArrayList<>();

                    for (Class c : classes) {
                        int startMin = Math.max(1, p - c.getThoiLuong() + 1);
                        int startMax = Math.min(p + 1, PERIODS_PER_DAY - c.getThoiLuong() + 2);

                        for (int startPeriod = startMin; startPeriod < startMax; startPeriod++) {
                            String varName = String.format("x_%s_%s_%d_%d", c.getId(), r.getName(), d, startPeriod);
                            if (x.containsKey(varName)) {
                                overlappingClasses.add(x.get(varName));
                            }
                        }
                    }

                    model.addLessOrEqual(LinearExpr.sum(overlappingClasses.toArray(new Literal[0])), 1);
                }
            }
        }

        // Ràng buộc 3: Liên kết biến y với việc sử dụng phòng
        for (Room r : rooms) {
            List<Literal> roomUsages = new ArrayList<>();

            for (Class c : classes) {
                for (int d = 0; d < DAYS_GD.length; d++) {
                    for (int p = 1; p <= PERIODS_PER_DAY - c.getThoiLuong() + 1; p++) {
                        String varName = String.format("x_%s_%s_%d_%d", c.getId(), r.getName(), d, p);
                        if (x.containsKey(varName)) {
                            roomUsages.add(x.get(varName));
                        }
                    }
                }
            }

            // Nếu có bất kỳ lớp nào được xếp vào phòng này, thì y[room] = 1
            if (!roomUsages.isEmpty()) {
                LinearExpr sum = LinearExpr.sum(roomUsages.toArray(new Literal[0]));
                model.addGreaterOrEqual(sum, model.newConstant(0));
                model.addLessOrEqual(sum, model.newConstant(DAYS_GD.length * PERIODS_PER_DAY));

                // y[room] = 1 khi và chỉ khi phòng được sử dụng
                model.addMaxEquality(y.get(String.format("y_%s", r.getName())), roomUsages);
            }
        }

        // Mục tiêu: Tối thiểu hóa số phòng được sử dụng
        List<Literal> usedRooms = new ArrayList<>(y.values());
        model.minimize(LinearExpr.sum(usedRooms.toArray(new Literal[0])));

        // Giải mô hình với giới hạn thời gian
        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(TIME_LIMIT_SECONDS);
        CpSolverStatus status = solver.solve(model);

        // Tạo kết quả lịch học
        List<ScheduleResult> results = new ArrayList<>();

        if (status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL) {
            int usedRoomCount = 0;
            for (Room r : rooms) {
                String varName = String.format("y_%s", r.getName());
                if (solver.booleanValue(y.get(varName))) {
                    usedRoomCount++;
                }
            }

            System.out.println("Tìm thấy giải pháp " +
                    (status == CpSolverStatus.OPTIMAL ? "tối ưu" : "khả thi") +
                    " trong " + solver.wallTime() + " giây");
            System.out.println("Số phòng được sử dụng: " + usedRoomCount);

            for (Class c : classes) {
                boolean classScheduled = false;

                for (Room r : rooms) {
                    for (int d = 0; d < DAYS_GD.length; d++) {
                        for (int p = 1; p <= PERIODS_PER_DAY - c.getThoiLuong() + 1; p++) {
                            String varName = String.format("x_%s_%s_%d_%d", c.getId(), r.getName(), d, p);

                            if (x.containsKey(varName) && solver.booleanValue(x.get(varName))) {
                                ScheduleResult result = new ScheduleResult();
                                result.setId(c.getId());
                                result.setMaHP(c.getMaHP());
                                result.setThoiLuong(c.getThoiLuong());
                                result.setRoom(r.getName());
                                result.setNgay(d + 1); // Chuyển từ index 0-4 sang 1-5
                                result.setStart(p);
                                result.setEnd(p + c.getThoiLuong() - 1);
                                result.setType(Type.GD);
                                result.setWeek(1); // Mặc định là tuần 1, có thể thay đổi tùy nhu cầu

                                results.add(result);
                                classScheduled = true;
                                break;
                            }
                        }
                        if (classScheduled)
                            break;
                    }
                    if (classScheduled)
                        break;
                }

                if (!classScheduled) {
                    unassignableClasses.add(c);
                }
            }

            return ScheduleRoomResponse.builder()
                    .classes(results)
                    .isScheduled(true)
                    .message("Succcessfull!")
                    .unassignableClasses(unassignableClasses)
                    .build();
        } else {
            return ScheduleRoomResponse.builder()
                    .classes(results)
                    .isScheduled(false)
                    .message("Not found solution!")
                    .unassignableClasses(unassignableClasses)
                    .build();
        }

    }

    public ScheduleRoomResponse scheduleClassToRoomTN(List<Class> classes, List<Room> rooms) {
        Loader.loadNativeLibraries();

        List<Class> unassignableClasses = classes.stream()
                .filter(c -> rooms.stream()
                        .noneMatch(r -> c.getSlMax() <= r.getSlMax() && r.getMaHPs().contains(c.getMaHP())))
                .collect(Collectors.toList());

        if (!unassignableClasses.isEmpty()) {
            return ScheduleRoomResponse.builder()
                    .classes(Collections.emptyList())
                    .isScheduled(false)
                    .message("Could not find a suitable schedule.")
                    .unassignableClasses(unassignableClasses)
                    .build();
        }

        CpModel model = new CpModel();

        // Tạo biến x[class_id][room_id][day][period]
        Map<String, Literal> x = new HashMap<>();
        for (Class c : classes) {
            for (Room r : rooms) {
                for (int d = 0; d < DAYS_TN.length; d++) {
                    for (int p = 1; p <= PERIODS_PER_DAY - c.getThoiLuong() + 1; p++) {
                        String key = String.format("x_%s_%s_%s_%d", c.getId(), r.getName(), d, p);
                        x.put(key, model.newBoolVar(key));
                    }
                }
            }
        }

        // Tạo biến w[class_id][week] - biểu thị lớp c được học vào tuần w
        Map<String, Literal> w = new HashMap<>();
        for (Class c : classes) {
            for (int wk : WEEKS) {
                String key = String.format("w_%s_%d", c.getId(), wk);
                w.put(key, model.newBoolVar(key));
            }
        }

        // Tạo biến firstWeek[class_id][week] - biểu thị tuần wk là tuần đầu tiên của
        // lớp c
        Map<String, Literal> firstWeek = new HashMap<>();
        for (Class c : classes) {
            // Tuần đầu tiên có thể từ 1 đến (16-NUM_WEEKS_REQUIRED+1)
            for (int wk = 1; wk <= 16 - NUM_WEEKS_REQUIRED + 1; wk++) {
                String key = String.format("firstWeek_%s_%d", c.getId(), wk);
                firstWeek.put(key, model.newBoolVar(key));
            }
        }

        // 1. Mỗi lớp TN chỉ có một tuần bắt đầu
        for (Class c : classes) {
            LinearExprBuilder sumFirstWeeks = LinearExpr.newBuilder();
            for (int wk = 1; wk <= 16 - NUM_WEEKS_REQUIRED + 1; wk++) {
                sumFirstWeeks.add(firstWeek.get(String.format("firstWeek_%s_%d", c.getId(), wk)));
            }
            model.addEquality(sumFirstWeeks, 1);
        }

        // 2. Ràng buộc các tuần phải liên tiếp
        for (Class c : classes) {
            // Với mỗi tuần bắt đầu có thể
            for (int startWeek = 1; startWeek <= 16 - NUM_WEEKS_REQUIRED + 1; startWeek++) {
                String firstWeekKey = String.format("firstWeek_%s_%d", c.getId(), startWeek);

                // Nếu tuần này là tuần bắt đầu, thì NUM_WEEKS_REQUIRED tuần liên tiếp phải được
                // học
                for (int offset = 0; offset < NUM_WEEKS_REQUIRED; offset++) {
                    int currentWeek = startWeek + offset;
                    String weekKey = String.format("w_%s_%d", c.getId(), currentWeek);

                    // if firstWeek[c][startWeek] == 1 then w[c][currentWeek] == 1
                    model.addImplication(firstWeek.get(firstWeekKey), w.get(weekKey));
                }

                // Các tuần không nằm trong khoảng liên tiếp này không được học
                for (int wk : WEEKS) {
                    if (wk < startWeek || wk >= startWeek + NUM_WEEKS_REQUIRED) {
                        String weekKey = String.format("w_%s_%d", c.getId(), wk);

                        // if firstWeek[c][startWeek] == 1 then w[c][wk] == 0
                        model.addImplication(firstWeek.get(firstWeekKey), w.get(weekKey).not());
                    }
                }
            }
        }

        // 3. Mỗi lớp TN học đúng numWeeksRequired tuần
        for (Class c : classes) {
            LinearExprBuilder sumWeeks = LinearExpr.newBuilder();
            for (int wk : WEEKS) {
                String weekKey = String.format("w_%s_%d", c.getId(), wk);
                sumWeeks.add(w.get(weekKey));
            }
            model.addEquality(sumWeeks, NUM_WEEKS_REQUIRED);
        }

        // 4. Mỗi lớp chỉ được xếp vào 1 lịch cố định (room, day, start_period)
        for (Class c : classes) {
            LinearExprBuilder sumSchedules = LinearExpr.newBuilder();
            for (Room r : rooms) {
                for (int d = 0; d < DAYS_TN.length; d++) {
                    for (int p = 1; p <= PERIODS_PER_DAY - c.getThoiLuong() + 1; p++) {
                        sumSchedules.add(x.get(String.format("x_%s_%s_%s_%d", c.getId(), r.getName(), d, p)));
                    }
                }
            }
            model.addEquality(sumSchedules, 1);
        }

        // 5. Liên kết x và w: Tạo biến phụ z[class, room, day, start_period, week]
        Map<String, Literal> z = new HashMap<>();
        for (Class c : classes) {
            for (Room r : rooms) {
                for (int d = 0; d < DAYS_TN.length; d++) {
                    for (int p = 1; p <= PERIODS_PER_DAY - c.getThoiLuong() + 1; p++) {
                        for (int wk : WEEKS) {
                            String zKey = String.format("z_%s_%s_%s_%d_%d", c.getId(), r.getName(), d, p, wk);
                            z.put(zKey, model.newBoolVar(zKey));

                            String xKey = String.format("x_%s_%s_%s_%d", c.getId(), r.getName(), d, p);
                            String wKey = String.format("w_%s_%d", c.getId(), wk);

                            // z = 1 khi cả x và w đều = 1
                            model.addBoolAnd(new Literal[] { x.get(xKey), w.get(wKey) })
                                    .onlyEnforceIf(z.get(zKey));

                            // Nếu x = 0 hoặc w = 0 thì z = 0
                            model.addBoolOr(new Literal[] { x.get(xKey).not(), w.get(wKey).not() })
                                    .onlyEnforceIf(z.get(zKey).not());
                        }
                    }
                }
            }
        }

        // 6. Không trùng phòng (kiểm tra từng tiết trong mỗi tuần)
        for (Room r : rooms) {
            for (int d = 0; d < DAYS_TN.length; d++) {
                for (int wk : WEEKS) {
                    for (int p = 1; p <= PERIODS_PER_DAY; p++) {
                        List<Literal> overlappingClasses = new ArrayList<>();
                        for (Class c : classes) {
                            for (int start_p = Math.max(1, p - c.getThoiLuong() + 1); start_p <= Math.min(p,
                                    PERIODS_PER_DAY - c.getThoiLuong() + 1); start_p++) {
                                String zKey = String.format("z_%s_%s_%s_%d_%d",
                                        c.getId(), r.getName(), d, start_p, wk);
                                if (z.containsKey(zKey)) {
                                    overlappingClasses.add(z.get(zKey));
                                }
                            }
                        }
                        if (!overlappingClasses.isEmpty()) {
                            LinearExprBuilder sumExpr = LinearExpr.newBuilder();
                            for (Literal lit : overlappingClasses) {
                                sumExpr.add(lit);
                            }
                            model.addLessOrEqual(sumExpr, 1);
                        }
                    }
                }
            }
        }

        // 7. Sĩ số và mã HP phải khớp
        for (Class c : classes) {
            for (Room r : rooms) {
                if (c.getSlMax() > r.getSlMax() || !r.getMaHPs().contains(c.getMaHP())) {
                    for (int d = 0; d < DAYS_TN.length; d++) {
                        for (int p = 1; p <= PERIODS_PER_DAY - c.getThoiLuong() + 1; p++) {
                            String xKey = String.format("x_%s_%s_%s_%d", c.getId(), r.getName(), d, p);
                            model.addEquality(x.get(xKey), 0);
                        }
                    }
                }
            }
        }

        // 8. TN phải nằm trong 1 buổi (sáng: 1-6, chiều: 7-12)
        for (Class c : classes) {
            for (Room r : rooms) {
                for (int d = 0; d < DAYS_TN.length; d++) {
                    for (int p = 1; p <= PERIODS_PER_DAY - c.getThoiLuong() + 1; p++) {
                        int end_p = p + c.getThoiLuong() - 1;
                        if ((p <= 6 && end_p > 6) || (p > 6 && end_p > 12)) {
                            String xKey = String.format("x_%s_%s_%s_%d", c.getId(), r.getName(), d, p);
                            model.addEquality(x.get(xKey), 0);
                        }
                    }
                }
            }
        }

        // Giải mô hình
        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);

        // Xử lý kết quả
        List<ScheduleResult> results = new ArrayList<>();
        if (status == CpSolverStatus.FEASIBLE || status == CpSolverStatus.OPTIMAL) {
            for (Class c : classes) {
                for (Room r : rooms) {
                    for (int d = 0; d < DAYS_TN.length; d++) {
                        for (int p = 1; p <= PERIODS_PER_DAY - c.getThoiLuong() + 1; p++) {
                            String xKey = String.format("x_%s_%s_%s_%d", c.getId(), r.getName(), d, p);
                            if (solver.booleanValue(x.get(xKey))) {
                                int end_p = p + c.getThoiLuong() - 1;

                                // Thêm lịch cho các tuần được chọn
                                for (int wk : WEEKS) {
                                    String wKey = String.format("w_%s_%d", c.getId(), wk);
                                    if (solver.booleanValue(w.get(wKey))) {
                                        ScheduleResult result = new ScheduleResult();
                                        result.setId(c.getId());
                                        result.setMaHP(c.getMaHP());
                                        result.setThoiLuong(c.getThoiLuong());
                                        result.setRoom(r.getName());
                                        result.setNgay(d + 1);
                                        result.setWeek(wk);
                                        result.setStart(p);
                                        result.setEnd(end_p);
                                        result.setType(Type.TN);

                                        results.add(result);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return ScheduleRoomResponse.builder()
                    .classes(results)
                    .isScheduled(true)
                    .message("Successfull!")
                    .unassignableClasses(Collections.emptyList())
                    .build();
        } else {
            return ScheduleRoomResponse.builder()
                    .classes(Collections.emptyList())
                    .isScheduled(false)
                    .message("A feasible solution could not be identified.")
                    .unassignableClasses(Collections.emptyList())
                    .build();
        }
    }

}