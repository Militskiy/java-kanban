package managers;

import managers.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.util.Status;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tasks.util.Status.DONE;
import static tasks.util.Status.IN_PROGRESS;
import static tasks.util.Status.NEW;
import static tasks.util.TaskType.EPIC;
import static tasks.util.TaskType.SUBTASK;
import static tasks.util.TaskType.TASK;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
        task = new Task(TASK, "Task-1", "New Task-1", NEW, DAY_1, 1440);
        epic = new Epic(EPIC, "Epic-1", "Test Epic");
        taskManager.addEpic(epic);
        subtask1 = new Subtask(SUBTASK, "Subtask-1", "Test Subtask-1",
                NEW, DAY_2, 1440, epic.getId());
        subtask2 = new Subtask(SUBTASK, "Subtask-2", "Test Subtask-2",
                DONE, DAY_3, 1440, epic.getId());
        subtask3 = new Subtask(SUBTASK, "Subtask-3", "Test Subtask-3",
                IN_PROGRESS, DAY_4, 1440, epic.getId());
    }

    @Test
    void validatesNewTask() {
        final String taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);
        for (LocalDateTime o : taskManager.taskValidator.newCheckList(savedTask)) {
            assertEquals(true, taskManager.taskValidator.getValidationMap().get(o),
                    "Не корректно заполнилась карта валидации");
        }
    }

    @Test
    void validatesUpdatedTask() {
        final String taskId = taskManager.addTask(task);
        final Task updatedTask = new Task(
                taskId,
                TASK,
                "Task-1",
                "Updated Task-1",
                Status.DONE, DAY_1,
                Duration.ofDays(2).toMinutes());

        taskManager.updateTask(updatedTask);
        for (LocalDateTime o : taskManager.taskValidator.newCheckList(updatedTask)) {
            assertEquals(true, taskManager.taskValidator.getValidationMap().get(o),
                    "Не корректно заполнилась карта валидации");
        }
    }

    @Test
    void clearsValidateMapWhenDeletingTask() {
        final String taskId = taskManager.addTask(task);
        taskManager.deleteTask(taskId);
        final Task deletedTask = taskManager.getTaskById(taskId);
        assertNull(deletedTask, "Задаче не удалена.");
        final Map<String, Task> tasks = taskManager.listTasks();
        assertEquals(0, tasks.size(), "Задачи не удалились из списка");
        for (LocalDateTime o : taskManager.taskValidator.newCheckList(task)) {
            assertEquals(false, taskManager.taskValidator.getValidationMap().get(o),
                    "Не очистилась карта валидации");
        }
    }

    @Test
    void throwsValidationExceptionWhenAddingOverlappingNewTask() {
        taskManager.addTask(task);
        final Task overLappingTask = new Task(null, TASK,
                "Overlapping Task", "Will throw exception", NEW, DAY_1, 1440);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> taskManager.taskValidator.validateNewTask(overLappingTask, "add"));
        assertEquals("Cannot add Task: Task Overlapping Task intersects with another Task",
                ex.getDetailedMessage());
    }

    @Test
    void throwsValidationExceptionWhenAddingOverlappingUpdatedTask() {
        taskManager.addTask(task);
        final Task newTask = new Task(null, TASK,
                "Overlapping Task", "Will throw exception", NEW, DAY_2, 1440);
        taskManager.addTask(newTask);
        final String id = newTask.getId();
        final Task overLappingTask = new Task(id, TASK,
                "Updated Task", "Should throw exception", IN_PROGRESS, DAY_1, 1440);
        ValidationException ex = assertThrows(ValidationException.class,
                () -> taskManager.taskValidator.validateUpdatedTask(overLappingTask,
                        newTask.getStartDate(), newTask.getDuration(), "update"));
        assertEquals("Cannot update Task: Task Updated Task intersects with another Task",
                ex.getDetailedMessage());
    }

    @Test
    void expandsValidationMapWhenAddingVeryLongTask() {
        final Task veryLongTask = new Task(
                null,
                TASK,
                "Task-1",
                "1 year long task",
                Status.DONE, DAY_1,
                Duration.ofDays(365).toMinutes());
        taskManager.addTask(veryLongTask);
        assertEquals(35232, taskManager.taskValidator.getValidationMap().size(),
                "Сетка валидации не расширяется");
    }

    @Test
    void fillsHistoryList() {
        final String newTaskID = taskManager.addTask(task);
        final String newEpicID = taskManager.addEpic(epic);
        final String newSubtaskID = taskManager.addSubTask(subtask1);
        taskManager.getTaskById(newTaskID);
        taskManager.getEpicById(newEpicID);
        taskManager.getSubtaskById(newSubtaskID);
        List<Task> testList = new ArrayList<>(List.of(subtask1, epic, task));
        assertEquals(testList, taskManager.historyManager.getHistory(), "История отображается не корректно");
    }
}
