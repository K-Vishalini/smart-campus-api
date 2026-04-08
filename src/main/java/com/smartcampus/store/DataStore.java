package com.smartcampus.store;

import com.smartcampus.model.Room;
import com.smartcampus.model.Sensor;
import com.smartcampus.model.SensorReading;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataStore {

    private static final Map<String, Room> rooms = new HashMap<>();
    private static final Map<String, Sensor> sensors = new HashMap<>();
    private static final Map<String, List<SensorReading>> readings = new HashMap<>();

    public static Map<String, Room> getRooms() { return rooms; }
    public static Map<String, Sensor> getSensors() { return sensors; }
    public static Map<String, List<SensorReading>> getReadings() { return readings; }

    public static List<SensorReading> getReadingsForSensor(String sensorId) {
        readings.putIfAbsent(sensorId, new ArrayList<>());
        return readings.get(sensorId);
    }
}