package task_tracker.tasks_type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected Integer id;
    protected LocalDateTime startTime;
    protected Duration duration;
    protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

    public Task() {
    }

    public Task(String name) {
        this(name, null, Status.NEW, -1, LocalDateTime.now(), Duration.ZERO);
    }

    public Task(String name, String description) {
        this(name, description, Status.NEW, -1, LocalDateTime.now(), Duration.ZERO);
    }

    public Task(String name, String description, Integer id) {
        this(name, description, Status.NEW, id, LocalDateTime.now(), Duration.ofSeconds(0));
    }

    public Task(String name, String description, Status status, Integer id) {
        this(name, description, status, id, LocalDateTime.now(), Duration.ZERO);
    }

    public Task(Task task) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.id = task.getID();
        this.startTime =  task.getStartTime();
        this.duration = task.getDuration();
    }

    public Task(String name, String description, Status status, Integer id, LocalDateTime startTime) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startTime = startTime;
        this.duration = Duration.ZERO;
    }

    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = -1;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description, Status status, Integer id, LocalDateTime startTime, Duration duration) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
        this.startTime = startTime;
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Integer getID() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                name +
                ", " + description +
                ", " + id +
                ", " + status +
                ", " + startTime.format(formatter) +
                ", " + duration +
                "}";
    }

    public String toCsvString() {
        return String.format("%d,TASK,%s,%s,%s,%s,%s",
                id, name, status, description, startTime, duration);
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name)
                && Objects.equals(description, task.description)
                && status == task.status
                && Objects.equals(id, task.id)
                && Objects.equals(startTime, task.startTime)
                && Objects.equals(duration, task.duration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id, startTime, duration);
    }
}
