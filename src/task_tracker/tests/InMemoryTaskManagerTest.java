package task_tracker.tests;

import org.junit.jupiter.api.BeforeEach;
import task_tracker.InMemoryTaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void inMemorySetup() {
        taskManager = new InMemoryTaskManager();
        super.setup();
    }
}
