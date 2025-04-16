package task_tracker.tasks_type;

import java.util.Objects;

public class Task {
    protected String name;
    protected String description;
    protected Status status;
    protected Integer id;

    public Task() {
    }

    public Task(String name) {
        this(name, null, Status.NEW, -1);
    }


    public Task(Task task) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.status = task.getStatus();
        this.id = task.getID();
    }

    public Task(String name, Integer id) {
        this(name, null, Status.NEW, id);
    }

    public Task(String name, String description, Integer id) {
        this(name, description, Status.NEW, id);
    }

    public Task(String name, String description, Status status, Integer id) {
        this.name = name;
        this.description = description;
        this.status = status;
        this.id = id;
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

    public void setID(Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Task{" +
                name +
                ", " + description +
                ", " + id +
                ", " + status +
                "}";
    }

    public String toCsvString() {
        return String.format("%d, TASK, %s, %s, %s", id, name, status, description);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(name, task.name) && Objects.equals(description, task.description) && status == task.status && Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, status, id);
    }
}
