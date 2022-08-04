package tasks.util;

import managers.TaskManager;
import managers.exceptions.ValidationException;
import managers.util.StateLoader;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TaskValidator {
    // В валидаторе реализована логика дополнительного задания, где нужно сделать сетку из 15ти минутных слотов.
    private final LinkedHashMap<LocalDateTime, Boolean> validationMap = new LinkedHashMap<>();
    private boolean initialized = false;
    private LocalDateTime endDate;
    private final TaskManager taskManager;

    public TaskValidator(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    // Инициализация валидатора и предзаполнение сетки слотов на год вперед в зависимости от условий
    private void init(LocalDateTime date) {
        // Логика если не идет загрузка данных из файла
        LocalDateTime beginDate;
        if (!StateLoader.isDataLoaded) {
            beginDate = date.truncatedTo(ChronoUnit.DAYS);
            endDate = beginDate.plusMonths(1);
            fillValidationMap(beginDate, endDate);
            // Логика если идет загрузка из файла
        } else {
            if (taskManager.listPrioritizedTasks().first().getStartDate()
                    .truncatedTo(ChronoUnit.DAYS).isBefore(date.truncatedTo(ChronoUnit.DAYS))) {
                beginDate = taskManager.listPrioritizedTasks().first().getStartDate()
                        .truncatedTo(ChronoUnit.DAYS);
            } else {
                beginDate = date.truncatedTo(ChronoUnit.DAYS);
            }
            endDate = beginDate.plusMonths(1);
            fillValidationMap(beginDate, endDate);
            taskManager.listPrioritizedTasks().forEach(task -> {
                if (task.getStartDate() != null) {
                    newCheckList(task).forEach(timeSlot -> validationMap.put(timeSlot, true));
                }
            });
        }
    }

    public LinkedHashMap<LocalDateTime, Boolean> getValidationMap() {
        return validationMap;
    }

    public void validateNewTask(Task task, String cause) throws ValidationException {
        if (!initialized && !(task.getStartDate() == null)) {
            init(task.getStartDate());
        }
        if (task.getStartDate() != null) {
            while (task.getStartDate().isAfter(endDate) ||
                    task.getStartDate().plusMinutes(task.getDuration()).isAfter(endDate)) {
                extendValidationMap();
            }
            Map<LocalDateTime, Boolean> updateList = new HashMap<>();

            for (LocalDateTime date : newCheckList(task)) {
                if (validationMap.get(roundDown(date))) {
                    throw new ValidationException(task, cause);
                } else {
                    updateList.put(roundDown(date), true);
                }
            }
            validationMap.putAll(updateList);
        }
    }

    public void validateUpdatedTask(
            Task task, LocalDateTime oldStartDate, long oldDuration, String cause) throws ValidationException {
        if (!initialized) {
            init(task.getStartDate());
        }
        if (oldStartDate == null) {
            validateNewTask(task, cause);
        } else if (!(task.getStartDate().isEqual(oldStartDate) && task.getDuration() == oldDuration)) {
            Map<LocalDateTime, Boolean> updateCheckList = updateCheckList(oldStartDate, oldDuration).stream()
                    .collect(Collectors.toMap(Function.identity(), (date) -> false));
            validationMap.putAll(updateCheckList);
            if (task.getStartDate() != null) {
                while (task.getStartDate().isAfter(endDate) ||
                        task.getStartDate().plusMinutes(task.getDuration()).isAfter(endDate)) {
                    extendValidationMap();
                }
                Map<LocalDateTime, Boolean> updateList = new HashMap<>();

                for (LocalDateTime date : newCheckList(task)) {
                    if (validationMap.get(roundDown(date))) {
                        updateCheckList.forEach((k, v) -> validationMap.put(k, true));
                        throw new ValidationException(task, cause);
                    } else {
                        updateList.put(roundDown(date), true);
                    }
                }
                validationMap.putAll(updateList);
            }
        }
    }

    public void removeTaskFromValidationMap(Task task) {
        Map<LocalDateTime, Boolean> updateList = new HashMap<>();
        for (LocalDateTime date : newCheckList(task)) {
            updateList.put(roundDown(date), false);
        }
        validationMap.putAll(updateList);
    }

    private LocalDateTime roundDown(LocalDateTime time) {
        int minute = time.getMinute();
        int remainder = minute % 15;
        if (remainder != 0) {
            time = time.withMinute(minute - remainder);
        }
        return time;
    }
    // Метод создания списка временных "слотов" для валидации новой задачи
    public List<LocalDateTime> newCheckList(Task task) {
        LocalDateTime startTime = roundDown(task.getStartDate());
        List<LocalDateTime> result = new ArrayList<>(List.of(startTime));
        int slotsNumber = (int) task.getDuration() / 15;
        for (int i = 1; i < slotsNumber; i++) {
            result.add(result.get(i - 1).plusMinutes(15));
        }
        return result;
    }

    // Метод создания списка временных "слотов" для валидации обновленной задачи
    private List<LocalDateTime> updateCheckList(LocalDateTime oldStartTime, long oldDuration) {
        LocalDateTime startTime = roundDown(oldStartTime);
        List<LocalDateTime> result = new ArrayList<>(List.of(startTime));
        int slotsNumber = (int) oldDuration / 15;
        for (int i = 1; i < slotsNumber; i++) {
            result.add(result.get(i - 1).plusMinutes(15));
        }
        return result;
    }

    // Метод заполнения сетки валидации при инициализации
    private void fillValidationMap(LocalDateTime beginDate, LocalDateTime endDate) {
        while (!beginDate.isEqual(endDate)) {
            validationMap.put(beginDate, false);
            beginDate = beginDate.plusMinutes(15);
        }
        initialized = true;
    }

    // Метод расширения сетки валидации если заканчиваются доступные "слоты"
    private void extendValidationMap() {
        LocalDateTime beginDate = endDate;
        endDate = beginDate.plusWeeks(1);
        fillValidationMap(beginDate, endDate);
    }
}
