package com.example.schedule.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.example.schedule.algorithm.ScheduleClassForRoom;
import com.example.schedule.dto.ScheduleRoomRequest;
import com.example.schedule.dto.ScheduleRoomResponse;
import com.example.schedule.model.Class;
import com.example.schedule.model.Room;
import com.example.schedule.model.ScheduleResult;
import com.example.schedule.model.Type;

@Service
public class ScheduleService {
	private final ScheduleClassForRoom scheduleClassForRoom;

	public ScheduleService(ScheduleClassForRoom scheduleClassForRoom) {
		this.scheduleClassForRoom = scheduleClassForRoom;
	}

	public ScheduleRoomResponse scheduleRoom(ScheduleRoomRequest request) {
		List<Class> allClasses = request.getClasses();
		List<Room> allRooms = request.getRooms();

		List<Class> classesGD = filterClassesByType(allClasses, Type.GD);
		List<Class> classesTN = filterClassesByType(allClasses, Type.TN);
		List<Room> roomsGD = filterRoomsByType(allRooms, Type.GD);
		List<Room> roomsTN = filterRoomsByType(allRooms, Type.TN);

		ScheduleRoomResponse results_GD = scheduleClassForRoom.scheduleClassToRoomGD(classesGD, roomsGD);
		ScheduleRoomResponse results_TN = scheduleClassForRoom.scheduleClassToRoomTN(classesTN, roomsTN);

		if (!results_GD.isScheduled() || !results_TN.isScheduled()) {
			return ScheduleRoomResponse.builder()
					.classes(Collections.emptyList())
					.isScheduled(false)
					.message("GD: " + results_GD.getMessage() + "\n" + "TN: "
							+ results_TN.getMessage())
					.unassignableClasses(Collections.emptyList())
					.build();
		}

		List<ScheduleResult> results = Stream.concat(
				results_GD.getClasses().stream(),
				results_TN.getClasses().stream()).collect(Collectors.toList());

		return ScheduleRoomResponse.builder()
				.classes(results)
				.isScheduled(true)
				.message("Successfull!")
				.unassignableClasses(Collections.emptyList())
				.build();

	}

	private List<Class> filterClassesByType(List<Class> classes, Type type) {
		return classes.stream().filter(c -> c.getType() == type).collect(Collectors.toList());
	}

	private List<Room> filterRoomsByType(List<Room> rooms, Type type) {
		return rooms.stream().filter(r -> r.getType() == type).collect(Collectors.toList());
	}

}
