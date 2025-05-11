package task_tracker.tasks_type;

import task_tracker.utils.TimeCalculator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected int id;
    protected LocalDateTime startTime;
    protected Duration duration;

    protected static final int DEFAULT_ID = -1;
    protected static final LocalDateTime DEFAULT_TIME = LocalDateTime.MIN;
    protected static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy");

    public Task() {
    }

    public Task(String name) {
        this(name, null, Status.NEW, DEFAULT_ID, DEFAULT_TIME, Duration.ZERO);
    }

    public Task(String name, String description) {
        this(name, description, Status.NEW, DEFAULT_ID, DEFAULT_TIME, Duration.ZERO);
    }

    public Task(String name, String description, int id) {
        this(name, description, Status.NEW, id, DEFAULT_TIME, Duration.ZERO);
    }

    public Task(String name, String description, Status status, int id) {
        this(name, description, status, id, DEFAULT_TIME, Duration.ZERO);
    }

    public Task(Task task) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.id = task.getID();
        this.startTime =  task.getStartTime();
        this.duration = task.getDuration();
    }

    public Task(String name, String description, Status status, int id, LocalDateTime startTime) {
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

    public Task(String name, String description, Status status, int id, LocalDateTime startTime, Duration duration) {
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

    public int getID() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return TimeCalculator.calculateEndTime(startTime, duration);
    }

    public static LocalDateTime getDefaultTime() {
        return DEFAULT_TIME;
    }

    public static int getDefaultId() {
        return DEFAULT_ID;
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
