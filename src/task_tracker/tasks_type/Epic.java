package task_tracker.tasks_type;

import task_tracker.utils.TimeCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name) {
        super(name, null, Status.NEW, DEFAULT_ID);
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW, DEFAULT_ID);
    }

    public Epic(String name, String description, Integer id) {
        super(name, description, Status.NEW, id);
    }

    public Epic(Epic epic) {
        super(epic);
        this.subtasks = epic.getSubtasks();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public List<Integer> getSubtasksID() {
        return subtasks.stream()
                .map(Subtask::getID)
                .toList();
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        updateTimeParameters();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        updateTimeParameters();
    }

    public void updateTimeParameters() {
        TimeCalculator.updateEpicTime(this, subtasks);
    }

    @Override
    public String toString() {
        return "Epic{" +
                getName() +
                ", " + getDescription() +
                ", " + getID() +
                ", " + getStatus() +
                ", " + startTime.format(formatter) +
                ", " + duration +
                ", " + getSubtasks() +
                "}";
    }

    @Override
    public String toCsvString() {
        return String.format("%d,EPIC,%s,%s,%s,%s,%s",
                id, name, status, description, startTime, duration);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Epic epic = (Epic) o;
        return Objects.equals(subtasks, epic.subtasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subtasks);
    }
}
