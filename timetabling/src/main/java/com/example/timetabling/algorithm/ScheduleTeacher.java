package com.example.timetabling.algorithm;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.example.timetabling.dto.algorithm.ClassTeacherAssignmentInput;
import com.example.timetabling.dto.algorithm.ClassTeacherAssignmentOutput;
import com.example.timetabling.model.Teacher;
import com.example.timetabling.model.Type;
import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverSolutionCallback;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.IntVar;
import com.google.ortools.sat.LinearExpr;
import com.google.ortools.sat.LinearExprBuilder;
import com.google.ortools.sat.Literal;

@Component
public class ScheduleTeacher {

    private final int TIME_LIMIT_SECONDS = 300;

    public List<ClassTeacherAssignmentOutput> scheduleTeacherToClass(List<ClassTeacherAssignmentInput> classes,
            List<Teacher> teachers) {

        Loader.loadNativeLibraries();
        // System.out.println("Starting teacher assignment...");
        // System.out.println("Number of teachers: " + teachers.size());
        // System.out.println("Number of classes: " + classes.size());

        // Separate GD and TN classes
        List<ClassTeacherAssignmentInput> classesGD = classes.stream()
                .filter(c -> c.getType() == Type.GD)
                .collect(Collectors.toList());
        List<ClassTeacherAssignmentInput> classesTN = classes.stream()
                .filter(c -> c.getType() == Type.TN)
                .collect(Collectors.toList());

        System.out.println("Found " + classesGD.size() + " GD classes and " + classesTN.size() + " TN classes");

        // Phase I: phân công lớp GD
        System.out.println("\nPhase 1: Assigning GD classes...");
        Map<String, TeacherAssignment> assignedGD = assignGDClasses(classesGD, teachers);
        System.out.println("Assigned " + assignedGD.size() + " GD classes out of " + classesGD.size());

        System.out.println("\nPhase 2: Assigning TN classes...");
        Map<String, TeacherAssignment> assignedTN = assignTNClasses(classesTN, teachers, assignedGD, classesGD);
        System.out.println("Assigned " + assignedTN.size() + " TN classes out of " + classesTN.size());

        List<ClassTeacherAssignmentOutput> results = new ArrayList<>();
        results.addAll(convertToClassResults(classesGD, assignedGD));
        results.addAll(convertToClassResultsTN(classesTN, assignedTN));

        System.out.println("Generated " + results.size() + " assignment results");
        return results;
    }

    private static class SolutionCallback extends CpSolverSolutionCallback {
        private final Map<String, Literal> boolVars;
        private final Map<String, IntVar> intVars;
        private final List<Map<String, Long>> solutions;
        private int solutionCount;
        private final long startTime;
        private final int timeLimit;

        public SolutionCallback(Map<String, Literal> boolVars, Map<String, IntVar> intVars, int timeLimit) {
            this.boolVars = boolVars;
            this.intVars = intVars;
            this.solutions = new ArrayList<>();
            this.solutionCount = 0;
            this.startTime = System.currentTimeMillis();
            this.timeLimit = timeLimit;
        }

        @Override
        public void onSolutionCallback() {
            solutionCount++;
            Map<String, Long> currentSolution = new HashMap<>();
            for (Map.Entry<String, Literal> entry : boolVars.entrySet()) {
                currentSolution.put(entry.getKey(), this.value(entry.getValue()));
            }
            for (Map.Entry<String, IntVar> entry : intVars.entrySet()) {
                currentSolution.put(entry.getKey(), this.value(entry.getValue()));
            }
            solutions.add(currentSolution);
            long elapsedTime = (System.currentTimeMillis() - startTime) / 1000;
            System.out.println("Found solution " + solutionCount + " after " + elapsedTime + " seconds");

            if (elapsedTime > timeLimit) {
                System.out.println("Time limit reached, stopping search");
                this.stopSearch();
            }
        }

        public List<Map<String, Long>> getSolutions() {
            return solutions;
        }

        public int getSolutionCount() {
            return solutionCount;
        }
    }

    private Map<String, TeacherAssignment> assignGDClasses(List<ClassTeacherAssignmentInput> classesGD,
            List<Teacher> teachers) {
        CpModel model = new CpModel();
        System.out.println("Preprocessing GD classes...");

        // xử lý một sô trường hợp biến không hợp lệ trước khi
        Map<String, List<Teacher>> eligibleTeachersGD = new HashMap<>();
        for (ClassTeacherAssignmentInput c : classesGD) {
            eligibleTeachersGD.put(c.getId(), new ArrayList<>());
            for (Teacher t : teachers) {
                if (t.getSubjectCodes().contains(c.getMaHP()) && t.getType().contains(c.getType())) {
                    eligibleTeachersGD.get(c.getId()).add(t);
                }
            }
        }

        // biến quyết định
        Map<String, Literal> x = new HashMap<>();
        Map<String, IntVar> intVars = new HashMap<>();
        for (ClassTeacherAssignmentInput c : classesGD) {
            for (Teacher t : eligibleTeachersGD.get(c.getId())) {
                String varName = String.format("x_%s_%s", c.getId(), t.getTeacherCode());
                x.put(varName, model.newBoolVar(varName));
            }
        }

        IntVar zGD = model.newIntVar(0, 10000, "z_gd");
        intVars.put("z_gd", zGD);

        // rang buộc chỉ phân công 1 giảng viên cho 1 lớp
        for (ClassTeacherAssignmentInput c : classesGD) {
            List<Teacher> eligible = eligibleTeachersGD.get(c.getId());
            if (!eligible.isEmpty()) {
                LinearExprBuilder sum = LinearExpr.newBuilder();
                for (Teacher t : eligible) {
                    sum.add(x.get(String.format("x_%s_%s", c.getId(), t.getTeacherCode())));
                }
                model.addEquality(sum.build(), 1);
            } else {
                System.out.println("Warning: No eligible teacher for GD class " + c.getId());
            }
        }

        // rang buộc lịch không xung đột
        System.out.println("Creating GD scheduling constraints...");
        Map<String, List<ClassTeacherAssignmentInput>> timeSlots = new HashMap<>();
        for (ClassTeacherAssignmentInput c : classesGD) {
            String key = String.format("%d_%d_%d", c.getNgay(), c.getStart(), c.getEnd());
            timeSlots.computeIfAbsent(key, k -> new ArrayList<>()).add(c);
        }

        for (List<ClassTeacherAssignmentInput> conflictingClasses : timeSlots.values()) {
            for (int i = 0; i < conflictingClasses.size(); i++) {
                ClassTeacherAssignmentInput c1 = conflictingClasses.get(i);
                for (int j = i + 1; j < conflictingClasses.size(); j++) {
                    ClassTeacherAssignmentInput c2 = conflictingClasses.get(j);
                    List<Teacher> commonTeachers = eligibleTeachersGD.get(c1.getId()).stream()
                            .filter(t -> eligibleTeachersGD.get(c2.getId()).contains(t))
                            .collect(Collectors.toList());
                    for (Teacher t : commonTeachers) {
                        Literal var1 = x.get(String.format("x_%s_%s", c1.getId(), t.getTeacherCode()));
                        Literal var2 = x.get(String.format("x_%s_%s", c2.getId(), t.getTeacherCode()));
                        if (var1 != null && var2 != null) {
                            model.addLessOrEqual(LinearExpr.sum(new Literal[] { var1, var2 }), 1);
                        }
                    }
                }
            }
        }

        // ràng buộc thời gian dạy
        System.out.println("Creating GD workload constraints...");
        for (Teacher t : teachers) {
            LinearExprBuilder totalTime = LinearExpr.newBuilder();
            for (ClassTeacherAssignmentInput c : classesGD) {
                if (eligibleTeachersGD.get(c.getId()).contains(t)) {
                    Literal var = x.get(String.format("x_%s_%s", c.getId(), t.getTeacherCode()));
                    if (var != null) {
                        totalTime.addTerm(var, (long) (c.getTime_teaching() * 100));
                    }
                }
            }
            model.addLessOrEqual(totalTime.build(), (long) (t.getTime() * 100));
            model.addLessOrEqual(totalTime.build(), zGD);
        }

        // Objective: Minimize max workload and maximize priority
        LinearExprBuilder prioritySum = LinearExpr.newBuilder();
        for (ClassTeacherAssignmentInput c : classesGD) {
            for (Teacher t : eligibleTeachersGD.get(c.getId())) {
                Literal var = x.get(String.format("x_%s_%s", c.getId(), t.getTeacherCode()));
                if (var != null) {
                    prioritySum.addTerm(var, t.getPriority_gd());
                }
            }
        }

        // Create objective expression: zGD - prioritySum
        LinearExpr objective = LinearExpr.newBuilder()
                .add(zGD)
                .add(LinearExpr.term(prioritySum.build(), -1))
                .build();
        model.minimize(objective);

        // Solve
        System.out.println("Solving GD assignment...");
        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(this.TIME_LIMIT_SECONDS);
        SolutionCallback callback = new SolutionCallback(x, intVars, this.TIME_LIMIT_SECONDS);
        CpSolverStatus status = solver.solve(model, callback);

        Map<String, TeacherAssignment> assignedGD = new HashMap<>();
        if (status == CpSolverStatus.OPTIMAL) {
            System.out.println("Found GD solution with objective value: " + solver.objectiveValue());
            for (ClassTeacherAssignmentInput c : classesGD) {
                for (Teacher t : eligibleTeachersGD.get(c.getId())) {
                    String varName = String.format("x_%s_%s", c.getId(), t.getTeacherCode());
                    Literal var = x.get(varName);
                    if (var != null && solver.booleanValue(var)) {
                        assignedGD.put(c.getId(), new TeacherAssignment(t.getTeacherCode(), t.getHoc_vi()));
                    }
                }
            }
        } else {
            System.out.println("No GD solution found, status: " + status);
            if (!callback.getSolutions().isEmpty()) {
                System.out.println("Using best GD solution from callback");
                Map<String, Long> bestSolution = callback.getSolutions().get(callback.getSolutions().size() - 1);
                long bestObjectiveValue = Long.MAX_VALUE;

                // Evaluate each solution to find the best one
                for (Map<String, Long> solution : callback.getSolutions()) {
                    long zGDValue = solution.getOrDefault("z_gd", 0L);
                    long prioritySumValue = 0;
                    for (ClassTeacherAssignmentInput c : classesGD) {
                        for (Teacher t : eligibleTeachersGD.get(c.getId())) {
                            String key = String.format("x_%s_%s", c.getId(), t.getTeacherCode());
                            if (solution.containsKey(key) && solution.get(key) == 1) {
                                prioritySumValue += t.getPriority_gd();
                            }
                        }
                    }
                    long objectiveValue = zGDValue - prioritySumValue;
                    if (objectiveValue < bestObjectiveValue) {
                        bestObjectiveValue = objectiveValue;
                        bestSolution = solution;
                    }
                }

                if (bestSolution != null) {
                    for (ClassTeacherAssignmentInput c : classesGD) {
                        for (Teacher t : eligibleTeachersGD.get(c.getId())) {
                            String key = String.format("x_%s_%s", c.getId(), t.getTeacherCode());
                            if (bestSolution.containsKey(key) && bestSolution.get(key) == 1) {
                                assignedGD.put(c.getId(), new TeacherAssignment(t.getTeacherCode(), t.getHoc_vi()));
                            }
                        }
                    }
                }
            }
        }

        return assignedGD;
    }

    private Map<String, TeacherAssignment> assignTNClasses(List<ClassTeacherAssignmentInput> classesTN,
            List<Teacher> teachers, Map<String, TeacherAssignment> assignedGD,
            List<ClassTeacherAssignmentInput> classesGD) {

        CpModel model = new CpModel();
        System.out.println("Preprocessing TN classes...");

        Map<String, List<Teacher>> eligibleTeachersTN = new HashMap<>();
        for (ClassTeacherAssignmentInput c : classesTN) {
            eligibleTeachersTN.put(c.getId(), new ArrayList<>());
            for (Teacher t : teachers) {
                if (t.getSubjectCodes().contains(c.getMaHP()) && t.getType().contains(c.getType())) {
                    eligibleTeachersTN.get(c.getId()).add(t);
                }
            }
        }

        // Map GD assignments
        Map<String, String> gdTeacherMap = new HashMap<>();
        for (ClassTeacherAssignmentInput c : classesGD) {
            if (assignedGD.containsKey(c.getId())) {
                gdTeacherMap.put(c.getId(), assignedGD.get(c.getId()).teacherCode);
            }
        }

        // Decision variables
        Map<String, Literal> y = new HashMap<>();
        Map<String, IntVar> intVars = new HashMap<>();
        for (ClassTeacherAssignmentInput c : classesTN) {
            for (Teacher t : eligibleTeachersTN.get(c.getId())) {
                String varName = String.format("y_%s_%s", c.getId(), t.getTeacherCode());
                y.put(varName, model.newBoolVar(varName));
            }
        }

        IntVar zTN = model.newIntVar(0, 10000, "z_tn");
        intVars.put("z_tn", zTN);

        // Constraint: Each TN class assigned exactly one teacher
        for (ClassTeacherAssignmentInput c : classesTN) {
            List<Teacher> eligible = eligibleTeachersTN.get(c.getId());
            if (!eligible.isEmpty()) {
                LinearExprBuilder sum = LinearExpr.newBuilder();
                for (Teacher t : eligible) {
                    sum.add(y.get(String.format("y_%s_%s", c.getId(), t.getTeacherCode())));
                }
                model.addEquality(sum.build(), 1);
            } else {
                System.out.println("Warning: No eligible teacher for TN class " + c.getId());
            }
        }

        // Constraint: No scheduling conflicts within TN classes
        System.out.println("Creating TN scheduling constraints...");
        Map<String, List<ClassTeacherAssignmentInput>> tnTimeSlots = new HashMap<>();
        for (ClassTeacherAssignmentInput c : classesTN) {
            for (int w : c.getWeeks()) {
                String key = String.format("%d_%d_%d_%d", w, c.getNgay(), c.getStart(), c.getEnd());
                tnTimeSlots.computeIfAbsent(key, k -> new ArrayList<>()).add(c);
            }
        }

        for (List<ClassTeacherAssignmentInput> conflictingClasses : tnTimeSlots.values()) {
            for (int i = 0; i < conflictingClasses.size(); i++) {
                ClassTeacherAssignmentInput c1 = conflictingClasses.get(i);
                for (int j = i + 1; j < conflictingClasses.size(); j++) {
                    ClassTeacherAssignmentInput c2 = conflictingClasses.get(j);
                    List<Teacher> commonTeachers = eligibleTeachersTN.get(c1.getId()).stream()
                            .filter(t -> eligibleTeachersTN.get(c2.getId()).contains(t))
                            .collect(Collectors.toList());
                    for (Teacher t : commonTeachers) {
                        Literal var1 = y.get(String.format("y_%s_%s", c1.getId(), t.getTeacherCode()));
                        Literal var2 = y.get(String.format("y_%s_%s", c2.getId(), t.getTeacherCode()));
                        if (var1 != null && var2 != null) {
                            model.addLessOrEqual(LinearExpr.sum(new Literal[] { var1, var2 }), 1);
                        }
                    }
                }
            }
        }

        // Constraint: No conflicts between TN and GD classes
        Map<String, List<Map.Entry<ClassTeacherAssignmentInput, String>>> gdSchedule = new HashMap<>();
        for (ClassTeacherAssignmentInput c : classesGD) {
            if (gdTeacherMap.containsKey(c.getId())) {
                for (int w = 1; w <= 16; w++) {
                    String key = String.format("%d_%d_%d_%d", w, c.getNgay(), c.getStart(), c.getEnd());
                    gdSchedule.computeIfAbsent(key, k -> new ArrayList<>())
                            .add(new AbstractMap.SimpleEntry<>(c, gdTeacherMap.get(c.getId())));
                }
            }
        }

        for (ClassTeacherAssignmentInput cTN : classesTN) {
            for (int w : cTN.getWeeks()) {
                String key = String.format("%d_%d_%d_%d", w, cTN.getNgay(), cTN.getStart(), cTN.getEnd());
                if (gdSchedule.containsKey(key)) {
                    for (Map.Entry<ClassTeacherAssignmentInput, String> entry : gdSchedule.get(key)) {
                        String gdTeacher = entry.getValue();
                        for (Teacher t : eligibleTeachersTN.get(cTN.getId())) {
                            if (t.getTeacherCode().equals(gdTeacher)) {
                                Literal var = y.get(String.format("y_%s_%s", cTN.getId(), t.getTeacherCode()));
                                if (var != null) {
                                    model.addEquality(var, 0);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Constraint: Teacher workload
        System.out.println("Creating TN workload constraints...");
        Map<String, double[]> teacherWorkload = new HashMap<>();
        for (Teacher t : teachers) {
            teacherWorkload.put(t.getTeacherCode(), new double[17]);
        }

        for (ClassTeacherAssignmentInput cGD : classesGD) {
            if (assignedGD.containsKey(cGD.getId())) {
                String teacherCode = assignedGD.get(cGD.getId()).teacherCode;
                for (int w = 1; w <= 16; w++) {
                    teacherWorkload.get(teacherCode)[w] += cGD.getTime_teaching();
                }
            }
        }

        for (Teacher t : teachers) {
            for (int w = 1; w <= 16; w++) {
                long baseWorkload = (long) (teacherWorkload.get(t.getTeacherCode())[w] * 100);
                LinearExprBuilder tnWorkload = LinearExpr.newBuilder();
                for (ClassTeacherAssignmentInput cTN : classesTN) {
                    if (cTN.getWeeks().contains(w) && eligibleTeachersTN.get(cTN.getId()).contains(t)) {
                        Literal var = y.get(String.format("y_%s_%s", cTN.getId(), t.getTeacherCode()));
                        if (var != null) {
                            tnWorkload.addTerm(var, (long) (cTN.getTime_teaching() * 100));
                        }
                    }
                }

                // First build the workload expression
                LinearExpr tnWorkloadExpr = tnWorkload.build();
                // Then create a new expression with the constant
                LinearExpr totalWorkload = LinearExpr.newBuilder()
                        .add(tnWorkloadExpr)
                        .build();

                // If base workload exists, add it to constraints separately
                // if (baseWorkload > 0) {
                // model.addLessOrEqual(
                // LinearExpr.sum(new LinearExpr[] { totalWorkload,
                // LinearExpr.constant(baseWorkload) }),
                // (long) (t.getTime() * 100));
                // model.addLessOrEqual(
                // LinearExpr.sum(new LinearExpr[] { totalWorkload,
                // LinearExpr.constant(baseWorkload) }),
                // zTN);
                // } else {
                // model.addLessOrEqual(totalWorkload, (long) (t.getTime() * 100));
                // model.addLessOrEqual(totalWorkload, zTN);
                // }
                LinearExpr finalWorkload = LinearExpr.newBuilder()
                        .add(totalWorkload)
                        .add(baseWorkload)
                        .build();
                model.addLessOrEqual(finalWorkload, (long) (t.getTime() * 100));
                model.addLessOrEqual(finalWorkload, zTN);
            }
        }

        // Objective: Minimize max workload and maximize priority
        LinearExprBuilder prioritySum = LinearExpr.newBuilder();
        boolean hasPriority = false; // Thêm biến này để theo dõi xem có term nào được thêm vào không

        for (ClassTeacherAssignmentInput c : classesTN) {
            for (Teacher t : eligibleTeachersTN.get(c.getId())) {
                Literal var = y.get(String.format("y_%s_%s", c.getId(), t.getTeacherCode()));
                if (var != null) {
                    prioritySum.addTerm(var, t.getPriority_tn());
                    hasPriority = true; // Đánh dấu đã thêm ít nhất một term
                }
            }
        }

        if (hasPriority) {
            // Create objective expression: zTN - prioritySum
            LinearExpr objective = LinearExpr.newBuilder()
                    .add(zTN)
                    .add(LinearExpr.term(prioritySum.build(), -1))
                    .build();
            model.minimize(objective);
        } else {
            model.minimize(zTN);
        }

        // Solve
        System.out.println("Solving TN assignment...");
        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(this.TIME_LIMIT_SECONDS);
        SolutionCallback callback = new SolutionCallback(y, intVars, this.TIME_LIMIT_SECONDS);
        CpSolverStatus status = solver.solve(model, callback);

        Map<String, TeacherAssignment> assignedTN = new HashMap<>();
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.println("Found TN solution with objective value: " + solver.objectiveValue());
            for (ClassTeacherAssignmentInput c : classesTN) {
                for (Teacher t : eligibleTeachersTN.get(c.getId())) {
                    String varName = String.format("y_%s_%s", c.getId(), t.getTeacherCode());
                    Literal var = y.get(varName);
                    if (var != null && solver.booleanValue(var)) {
                        assignedTN.put(c.getId(), new TeacherAssignment(t.getTeacherCode(), t.getHoc_vi()));
                    }
                }
            }
        } else {
            System.out.println("No TN solution found, status: " + status);
            if (!callback.getSolutions().isEmpty()) {
                System.out.println("Using best TN solution from callback");
                Map<String, Long> bestSolution = callback.getSolutions().get(callback.getSolutions().size() - 1);
                long bestObjectiveValue = Long.MAX_VALUE;

                // Evaluate each solution to find the best one
                for (Map<String, Long> solution : callback.getSolutions()) {
                    long zTNValue = solution.getOrDefault("z_tn", 0L);
                    long prioritySumValue = 0;
                    for (ClassTeacherAssignmentInput c : classesTN) { // Thay classesGD thành classesTN
                        for (Teacher t : eligibleTeachersTN.get(c.getId())) {
                            String key = String.format("y_%s_%s", c.getId(), t.getTeacherCode()); // Thay x thành y
                            if (solution.containsKey(key) && solution.get(key) == 1) {
                                prioritySumValue += t.getPriority_tn(); // Thay priority_gd thành priority_tn
                            }
                        }
                    }
                    long objectiveValue = zTNValue - prioritySumValue;
                    if (objectiveValue < bestObjectiveValue) {
                        bestObjectiveValue = objectiveValue;
                        bestSolution = solution;
                    }
                }

                if (bestSolution != null) {
                    for (ClassTeacherAssignmentInput c : classesTN) {
                        for (Teacher t : eligibleTeachersTN.get(c.getId())) {
                            String key = String.format("y_%s_%s", c.getId(), t.getTeacherCode());
                            if (bestSolution.containsKey(key) && bestSolution.get(key) == 1) {
                                assignedTN.put(c.getId(), new TeacherAssignment(t.getTeacherCode(), t.getHoc_vi()));
                            }
                        }
                    }
                }
            }
        }
        return assignedTN;
    }

    private List<ClassTeacherAssignmentOutput> convertToClassResults(List<ClassTeacherAssignmentInput> classesGD,
            Map<String, TeacherAssignment> assignedGD) {
        List<ClassTeacherAssignmentOutput> results = new ArrayList<>();
        for (ClassTeacherAssignmentInput c : classesGD) {
            ClassTeacherAssignmentOutput result = new ClassTeacherAssignmentOutput();
            result.setId(c.getId());
            result.setMaHP(c.getMaHP());
            result.setType(c.getType());
            result.setWeek(c.getWeeks().get(0));
            result.setDay(c.getNgay());
            result.setStart(c.getStart());
            result.setEnd(c.getEnd());
            result.setRoom(c.getRoom());
            result.setTime_teaching(c.getTime_teaching());
            TeacherAssignment assignment = assignedGD.getOrDefault(c.getId(),
                    new TeacherAssignment("Chưa phân công", ""));
            result.setTeacherCode(assignment.teacherCode);
            result.setHocvi(assignment.hocVi);
            result.setSlMax(c.getSlMax());
            results.add(result);
        }
        return results;
    }

    private List<ClassTeacherAssignmentOutput> convertToClassResultsTN(List<ClassTeacherAssignmentInput> classesTN,
            Map<String, TeacherAssignment> assignedTN) {
        List<ClassTeacherAssignmentOutput> results = new ArrayList<>();
        for (ClassTeacherAssignmentInput c : classesTN) {
            TeacherAssignment assignment = assignedTN.getOrDefault(c.getId(),
                    new TeacherAssignment("Chưa phân công", ""));
            for (int w : c.getWeeks()) {
                ClassTeacherAssignmentOutput result = new ClassTeacherAssignmentOutput();
                result.setId(c.getId());
                result.setMaHP(c.getMaHP());
                result.setType(c.getType());
                result.setWeek(w);
                result.setDay(c.getNgay());
                result.setStart(c.getStart());
                result.setEnd(c.getEnd());
                result.setRoom(c.getRoom());
                result.setTime_teaching(c.getTime_teaching());
                result.setTeacherCode(assignment.teacherCode);
                result.setHocvi(assignment.hocVi);
                result.setSlMax(c.getSlMax());
                results.add(result);
            }
        }
        return results;
    }

    // Có thể tạo một class TimeSlotKey để hiệu quả hơn thay vì dùng String
    private static class TimeSlotKey {
        final int week, day, startTime, endTime;

        TimeSlotKey(int week, int day, int startTime, int endTime) {
            this.week = week;
            this.day = day;
            this.startTime = startTime;
            this.endTime = endTime;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            TimeSlotKey that = (TimeSlotKey) o;
            return week == that.week && day == that.day &&
                    startTime == that.startTime && endTime == that.endTime;
        }

        @Override
        public int hashCode() {
            return Objects.hash(week, day, startTime, endTime);
        }
    }
}
