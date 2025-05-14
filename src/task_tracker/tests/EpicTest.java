package task_tracker.tests;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task_tracker.managers.Managers;
import task_tracker.managers.TaskManager;
import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Status;
import task_tracker.tasks_type.Subtask;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EpicTest {
    private TaskManager taskManager;
    private Epic epic;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getInMemory();
        epic = taskManager.addEpic(new Epic("Epic"));
    }

    @Test
    void shouldReturnNewWhenSubtaskListIsEmpty() {
        Assertions.assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldReturnNewWhenAllSubtasksAreNew() {
        taskManager.addSubtask(new Subtask("Сделать математику", epic.getID()));
        taskManager.addSubtask(new Subtask("Сделать русский", epic.getID()));
        assertEquals(Status.NEW, epic.getStatus());
    }

    @Test
    void shouldReturnDoneWhenAllSubtasksAreDone() {
        taskManager.addSubtask(new Subtask("Сделать математику", "subtask 1", Status.DONE, epic.getID()));
        taskManager.addSubtask(new Subtask("Сделать русский", "subtask 2", Status.DONE, epic.getID()));
        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    void shouldReturnInProgressWhenSubtasksAreNewAndDone() {
        taskManager.addSubtask(new Subtask("Сделать математику", "subtask 1", Status.DONE, epic.getID()));
        taskManager.addSubtask(new Subtask("Сделать русский", "subtask 2", Status.NEW, epic.getID()));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }

    @Test
    void shouldReturnInProgressWhenAnySubtaskInProgress() {
        taskManager.addSubtask(new Subtask("Сделать математику", "subtask 1", Status.NEW, epic.getID()));
        taskManager.addSubtask(new Subtask("Сделать русский", "subtask 2", Status.IN_PROGRESS, epic.getID()));
        taskManager.addSubtask(new Subtask("Сделать английский", "subtask 3", Status.DONE, epic.getID()));
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
    }
}