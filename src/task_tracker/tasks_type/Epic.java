package task_tracker.tasks_type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Epic extends Task {
    private List<Subtask> subtasks = new ArrayList<>();

    public Epic(String name) {
        super(name, null, Status.NEW, -1);
        calculateTime();
    }

    public Epic(String name, String description) {
        super(name, description, Status.NEW, -1);
        calculateTime();
    }

    public Epic(String name, String description, Integer id) {
        super(name, description, Status.NEW, id);
        calculateTime();
    }

    public Epic(Epic epic) {
        super(epic);
        this.subtasks = epic.getSubtasks();
    }

    public List<Subtask> getSubtasks() {
        return subtasks;
    }

    public List<Integer> getSubtasksID() {
        List<Integer> subtasksID = new ArrayList<>();
        for (Subtask subtask : subtasks) {
            subtasksID.add(subtask.getID());
        }
        return subtasksID;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
        calculateTime();
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
        calculateTime();
    }

    public void calculateTime() {
        Optional<LocalDateTime> minStart = subtasks.stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> maxEnd = subtasks.stream()
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        if (minStart.isPresent() && maxEnd.isPresent()) {
            this.startTime = minStart.get();
            this.duration = Duration.between(startTime, maxEnd.get());
        } else {
            this.startTime = LocalDateTime.MIN;
            this.duration = Duration.ZERO;
        }
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
