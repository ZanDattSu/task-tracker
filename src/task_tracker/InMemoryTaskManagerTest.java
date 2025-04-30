package task_tracker;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void inMemorySetup() {
        taskManager = new InMemoryTaskManager();
        super.setup();
    }
}
