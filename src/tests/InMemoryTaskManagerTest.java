package tests;

import org.junit.jupiter.api.BeforeEach;
import task_tracker.managers.InMemoryTaskManager;
import task_tracker.managers.Managers;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void inMemorySetup() {
        taskManager = Managers.getInMemory();
        super.setup();
    }
}
