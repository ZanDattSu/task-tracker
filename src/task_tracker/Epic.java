package task_tracker;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Subtask> subtasks = new ArrayList<>();

    public Epic(Epic epic) {
        super(epic);
        this.subtasks = epic.getSubtasks();
    }

    public Epic(String name, Integer id) {
        super(name, id);
    }

    public Epic(String name, String description, Integer id) {
        super(name, description, id);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Subtask> subtasks) {
        this.subtasks = subtasks;
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    @Override
    public String toString() {
        return "Epic{" +
                getName() +
                ", " + getDescription() +
                ", " + getID() +
                ", " + getStatus() +
                ", " + getSubtasks() +
                "}";
    }

}
