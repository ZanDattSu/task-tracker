package task_tracker.managers;

import com.google.gson.Gson;
import task_tracker.server.GsonProvider;
import task_tracker.server.KVServer;
import task_tracker.server.KVTaskClient;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.util.List;
import java.util.stream.Collectors;

public class HTTPTaskManager extends FileBackedTaskManager {
    private static final String STATE_KEY = "manager_state";
    private final KVTaskClient client;
    private final Gson gson = GsonProvider.getGson();

    public HTTPTaskManager() {
        client = new KVTaskClient(KVServer.getUrl());
        String json = client.load(STATE_KEY);
        if (json != null && !json.isBlank()) {
            loadFromJsonState(json);
        }
    }

    @Override
    public void save() {
        String json = toJsonState();
        client.put(STATE_KEY, json);
    }

    private static class State {
        List<Task> tasks;
        List<Epic> epics;
        List<Subtask> subtasks;
        List<Integer> history;

        public State(List<Task> tasks, List<Epic> epics, List<Subtask> subtasks, List<Integer> history) {
            this.tasks = tasks;
            this.epics = epics;
            this.subtasks = subtasks;
            this.history = history;
        }
    }

    private String toJsonState() {
        State s = new State(
                this.getTasks(),
                this.getEpics(),
                this.getSubtasks(),
                this.getHistory().stream()
                        .map(Task::getID)
                        .collect(Collectors.toList())
        );
        return gson.toJson(s);
    }

    private void loadFromJsonState(String json) {
        State s = gson.fromJson(json, State.class);
        this.clearTasks();
        this.clearEpics();

        for (Task t : s.tasks) {
            super.addTask(t);
        }
        for (Epic e : s.epics) {
            super.addEpic(e);
        }
        for (Subtask st : s.subtasks) {
            super.addSubtask(st);
        }
        super.setHistory(s.history);
    }
}
