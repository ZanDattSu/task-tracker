package task_tracker.tests;

import org.junit.jupiter.api.Test;
import task_tracker.managers.InMemoryTaskManager;
import task_tracker.managers.Managers;
import task_tracker.managers.TaskManager;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Subtask;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SubtaskTest {

    @Test
    void shouldAddSubtasksToEpic() {
        TaskManager taskManager = Managers.getDefault();
        Epic epic = taskManager.addEpic(new Epic("Epic"));
        Subtask subtask1 = taskManager.addSubtask(new Subtask("Subtask 1", epic.getID()));
        Subtask subtask2 = taskManager.addSubtask(new Subtask("Subtask 2", epic.getID()));

        List<Subtask> subtasks = epic.getSubtasks();

        assertEquals(epic.getID(), subtask1.getEpicID());
        assertEquals(epic.getID(), subtask2.getEpicID());

        assertEquals(2, subtasks.size());
    }
}