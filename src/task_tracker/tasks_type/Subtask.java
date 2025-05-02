package task_tracker.tasks_type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    private final Integer epicID;

    public Subtask(String name, Integer epicID) {
        super(name, null, Status.NEW, DEFAULT_ID, DEFAULT_TIME, Duration.ZERO);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Integer epicID) {
        super(name, description, Status.NEW, DEFAULT_ID, DEFAULT_TIME, Duration.ZERO);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Integer id, Integer epicID) {
        super(name, description, Status.NEW, id, DEFAULT_TIME, Duration.ZERO);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Status status, Integer epicID) {
        super(name, description, status, DEFAULT_ID, DEFAULT_TIME, Duration.ZERO);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Status status, Integer id, Integer epicID) {
        super(name, description, status, id, DEFAULT_TIME, Duration.ZERO);
        this.epicID = epicID;
    }

    public Subtask(Subtask subtask) {
        super(subtask);
        this.epicID = subtask.getEpicID();
    }

    public Subtask(String name, String description, Status status,
                   LocalDateTime startTime, Duration duration, Integer epicID) {
        super(name, description, status, DEFAULT_ID, startTime, duration);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Status status, Integer id,
                   LocalDateTime startTime, Duration duration, Integer epicID) {
        super(name, description, status, id, startTime, duration);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                name +
                ", " + description +
                ", " + id +
                ", " + status +
                ", " + startTime.format(formatter) +
                ", " + duration +
                ", " + epicID +
                "}";
    }

    @Override
    public String toCsvString() {
        return String.format("%d,SUBTASK,%s,%s,%s,%s,%s,%d",
                id, name, status, description, startTime, duration, epicID);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Subtask subtask = (Subtask) o;
        return Objects.equals(epicID, subtask.epicID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), epicID);
    }
}
