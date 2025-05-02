package task_tracker.utils;

import task_tracker.tasks_type.Epic;
import task_tracker.tasks_type.Subtask;
import task_tracker.tasks_type.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class TimeCalculator {


    private TimeCalculator() {
    }

    public static LocalDateTime calculateEndTime(LocalDateTime startTime, Duration duration) {
        if (startTime == null){
            return null;
        }
        return startTime.plus(duration);
    }

    public static void updateEpicTime(Epic epic, List<Subtask> subtasks) {
        Optional<LocalDateTime> minStart = subtasks.stream()
                .map(Task::getStartTime)
                .filter(Objects::nonNull)
                .min(LocalDateTime::compareTo);

        Optional<LocalDateTime> maxEnd = subtasks.stream()
                .map(Task::getEndTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo);

        if (minStart.isPresent() && maxEnd.isPresent()) {
            epic.setStartTime(minStart.get());
            epic.setDuration(Duration.between(minStart.get(), maxEnd.get()));
        } else {
            epic.setStartTime(Task.getDefaultTime());
            epic.setDuration(Duration.ZERO);
        }
    }
}