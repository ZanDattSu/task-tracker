package task_tracker.tasks_type;

import java.util.Objects;

public class Subtask extends Task {
    private final Integer epicID;

    public Subtask(Subtask subtask) {
        super(subtask);
        this.epicID = subtask.getEpicID();
    }

    public Subtask(String name, Integer epicID) {
        super(name, -1);
        this.epicID = epicID;
    }

    public Subtask(String name, Integer id, Integer epicID) {
        super(name, id);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Integer id, Integer epicID) {
        super(name, description, id);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, Status status, Integer id, Integer epicID) {
        super(name, description, status, id);
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
                ", " + epicID +
                "}";
    }

    @Override
    public String toCsvString() {
        return String.format("%d, TASK, %s, %s, %s, %d", id, name, status, description, epicID);
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
