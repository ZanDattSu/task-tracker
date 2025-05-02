package task_tracker.utils;

import task_tracker.tasks_type.Task;

import java.time.LocalDateTime;
import java.util.Set;

public class TaskTimeValidator {

    public void validateTimeSlot(Set<Task> tasks) {
        Task prev = null;
        for (Task curr : tasks) {
            if (prev != null && !validateNonOverlap(prev, curr)) {
                throw new RuntimeException(
                        String.format("Задача %d (%s–%s) пересекается с %d (%s–%s)",
                                prev.getID(), prev.getStartTime(), prev.getEndTime(),
                                curr.getID(), curr.getStartTime(), curr.getEndTime())
                );
            }
            prev = curr;
        }
    }

    private boolean validateNonOverlap(Task t1, Task t2) {
        if (t1.getStartTime() == null || t2.getStartTime() == null) {
            return true;
        }
        if (t1.getEndTime().isEqual(Task.getDefaultTime()) || t2.getEndTime().isEqual(Task.getDefaultTime())){
            return true;
        }
        LocalDateTime s1 = t1.getStartTime(), e1 = t1.getEndTime();
        LocalDateTime s2 = t2.getStartTime(), e2 = t2.getEndTime();

        return e1.isBefore(s2) || e1.isEqual(s2) || e2.isBefore(s1) || e2.isEqual(s1);
    }
}
