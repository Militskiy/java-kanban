package tasks.util;

import managers.Managers;
import managers.exceptions.ValidationException;
import managers.util.StateLoader;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

public final class TaskValidator {
    private TaskValidator() {
    }

    public final static Map<LocalDateTime, Boolean> validationMap = new LinkedHashMap<>();
    private static boolean initialized = false;

    private static void init(LocalDateTime dateTime) {
        if (!StateLoader.isDataLoaded) {
            LocalDateTime beginDate = dateTime.truncatedTo(ChronoUnit.DAYS);
            LocalDateTime endDate = beginDate.plusYears(1);
            fillValidationMap(beginDate, endDate);
        } else {
            LocalDateTime beginDate;
            if (Managers.getDefault().listPrioritizedTasks().first().getStartDate()
                    .truncatedTo(ChronoUnit.DAYS).isBefore(dateTime.truncatedTo(ChronoUnit.DAYS))) {
                beginDate = Managers.getDefault().listPrioritizedTasks().first().getStartDate()
                        .truncatedTo(ChronoUnit.DAYS);
            } else {
                beginDate = dateTime.truncatedTo(ChronoUnit.DAYS);
            }
            LocalDateTime endDate = beginDate.plusYears(1);
            fillValidationMap(beginDate, endDate);
            Managers.getDefault().listPrioritizedTasks().forEach(task -> {
                if (task.getStartDate() != null) {
                    checkList(task).forEach(timeSlot -> validationMap.put(timeSlot, true));
                }
            });
        }
    }

    public static void validateTask(Task task, String cause) throws ValidationException {
        if (!initialized) {
            init(task.getStartDate());
        }
        if (task.getStartDate() != null) {
            Map<LocalDateTime, Boolean> updateList = new HashMap<>();
            for (LocalDateTime date : checkList(task)) {
                if (validationMap.get(roundDown(date))) {
                    throw new ValidationException(task, cause);
                } else {
                    updateList.put(roundDown(date), true);
                }
            }
            validationMap.putAll(updateList);
        }
    }

    private static LocalDateTime roundDown(LocalDateTime time) {
        int minute = time.getMinute();
        int remainder = minute % 15;
        if (remainder != 0) {
            time = time.withMinute(minute - remainder);
        }
        return time;
    }

    private static List<LocalDateTime> checkList(Task task) {
        LocalDateTime startTime = roundDown(task.getStartDate());
        List<LocalDateTime> result = new ArrayList<>(List.of(startTime));
        int slotsNumber = (int) task.getDuration() / 15;
        for (int i = 1; i < slotsNumber; i++) {
            result.add(result.get(i - 1).plusMinutes(15));
        }
        return result;
    }
    private static void fillValidationMap(LocalDateTime beginDate, LocalDateTime endDate) {
        while (!beginDate.isEqual(endDate)) {
            validationMap.put(beginDate, false);
            beginDate = beginDate.plusMinutes(15);
        }
        initialized = true;
    }
}
