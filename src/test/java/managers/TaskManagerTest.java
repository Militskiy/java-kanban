package managers;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;
import tasks.util.TaskComparator;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static tasks.util.Status.*;
import static tasks.util.TaskType.*;

abstract class TaskManagerTest<T extends TaskManager> {


    T taskManager;
    static Task task;
    static Epic epic;
    static Subtask subtask1;
    static Subtask subtask2;
    static Subtask subtask3;
    final static LocalDateTime DAY_1 = LocalDateTime.of(2022, 1, 1, 0, 0);
    final static LocalDateTime DAY_2 = LocalDateTime.of(2022, 1, 2, 0, 0);
    final static LocalDateTime DAY_3 = LocalDateTime.of(2022, 1, 3, 0, 0);
    final static LocalDateTime DAY_4 = LocalDateTime.of(2022, 1, 4, 0, 0);

    @BeforeAll
    static void beforeAll() {
    }

    @BeforeEach
    void beforeEach() {
    }

    @Test
    void addsSubTask() {
        assertNull(taskManager.addSubTask(subtask1), "Добавляется задача с несуществующим эпиком");
        final String epicID = taskManager.addEpic(epic);
        final String subtaskID = taskManager.addSubTask(subtask2);
        final Epic savedEpic = taskManager.getEpicById(epicID);
        final Subtask savedSubtask = taskManager.getSubtaskById(subtaskID);

        assertNotNull(savedSubtask, "Задача на найдена.");
        assertEquals(subtask2, savedSubtask, "Задачи не совпадают.");
        assertEquals(savedEpic, savedSubtask.getEpic(), "Не правильный эпик в задаче");
        assertEquals(savedSubtask, savedEpic.getSubtaskList().get(0), "Задача не добавилась в список эпика");
        assertEquals(DONE, epic.getStatus(), "Неверный статус эпика");

        final Map<String, Subtask> subtasks = taskManager.listAllSubtasks();

        assertNotNull(subtasks, "Задачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество задач.");
        assertEquals(subtask2, subtasks.get(subtaskID), "Задачи не совпадают");
    }

    @Test
    void updatesSubtask() {
        taskManager.addEpic(epic);
        final String subtaskID = taskManager.addSubTask(subtask1);
        final Subtask updatedSubtask = new Subtask(subtaskID, SUBTASK,
                "Updated Subtask-1",
                "Update",
                DONE,
                epic,
                DAY_2,
                Duration.ofDays(2).toMinutes());
        taskManager.updateSubtask(updatedSubtask);
        final Subtask savedUpdatedSubtask = taskManager.getSubtaskById(subtaskID);
        assertNotNull(savedUpdatedSubtask, "Задача не найдена.");
        assertEquals(updatedSubtask, savedUpdatedSubtask, "Задачи не совпадают.");
        assertEquals(updatedSubtask, epic.getSubtaskList().get(0), "Неверная подзадача в эпике.");
        assertEquals(1, taskManager.listAllSubtasks().size(), "Неверное количество подзадач в списке.");
        assertEquals(1, epic.getSubtaskList().size(), "Неверное количество подзадач у эпика.");
        assertEquals(DONE, epic.getStatus(), "Статус эпика не обновился.");
    }

    @Test
    void deletesSubTask() {
        taskManager.addEpic(epic);
        final String subtaskId = taskManager.addSubTask(subtask2);
        taskManager.addSubTask(subtask3);
        taskManager.deleteSubTask(subtaskId);
        assertEquals(IN_PROGRESS, epic.getStatus(), "Неверный статус эпика");
        Subtask deletedSubtask = taskManager.getSubtaskById(subtaskId);
        assertNull(deletedSubtask, "Задаче не удалена.");
        final Map<String, Subtask> tasks = taskManager.listAllSubtasks();
        assertEquals(1, tasks.size(), "Задача не удалилась из списка");
        assertEquals(1, epic.getSubtaskList().size(), "Задача не удалилась из списка epic");
    }

    @Test
    void deletesAllSubTasks() {
        taskManager.addEpic(epic);
        final String subtask1ID = taskManager.addSubTask(subtask1);
        final String subtask2ID = taskManager.addSubTask(subtask2);
        final String subtask3ID = taskManager.addSubTask(subtask3);
        taskManager.deleteAllSubTasks();
        final Subtask deletedSubtask1 = taskManager.getSubtaskById(subtask1ID);
        final Subtask deletedSubtask2 = taskManager.getSubtaskById(subtask2ID);
        final Subtask deletedSubtask3 = taskManager.getSubtaskById(subtask3ID);
        assertEquals(NEW, epic.getStatus(), "Неверный статус эпика");
        assertNull(deletedSubtask1, "Задаче не удалена.");
        assertNull(deletedSubtask2, "Задаче не удалена.");
        assertNull(deletedSubtask3, "Задаче не удалена.");
        final Map<String, Subtask> tasks = taskManager.listAllSubtasks();
        assertEquals(0, tasks.size(), "Задачи не удалилась из списка");
        assertEquals(0, epic.getSubtaskList().size(), "Задачи не удалились из списка epic");
    }

    @Test
    void addsTask() {
        final String taskId = taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задаче на найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final Map<String, Task> tasks = taskManager.listTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(taskId), "Задачи не совпадают");
    }

    @Test
    void updatesTask() {
        final String taskId = taskManager.addTask(task);
        final Task updatedTask = new Task(
                taskId,
                TASK,
                "Task-1",
                "Updated Task-1",
                DONE,
                DAY_1,
                Duration.ofDays(2).toMinutes());

        taskManager.updateTask(updatedTask);

        final Task savedTask = taskManager.getTaskById(taskId);

        assertNotNull(savedTask, "Задаче на найдена.");
        assertEquals(updatedTask, savedTask, "Задачи не совпадают.");

        final Map<String, Task> tasks = taskManager.listTasks();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(updatedTask, tasks.get(taskId), "Задачи не совпадают");
    }

    @Test
    void deletesTask() {
        final String taskId = taskManager.addTask(task);
        taskManager.deleteTask(taskId);
        final Task deletedTask = taskManager.getTaskById(taskId);
        assertNull(deletedTask, "Задаче не удалена.");
        final Map<String, Task> tasks = taskManager.listTasks();
        assertEquals(0, tasks.size(), "Задачи не удалились из списка");
    }

    @Test
    void deletesAllTasks() {
        final String task1Id = taskManager.addTask(task);
        final Task task2 = new Task(null, TASK, "Task-2", "New Task-2", NEW, DAY_2, 1440);
        final String task2Id = taskManager.addTask(task2);
        taskManager.deleteAllTasks();
        final Task deletedTask1 = taskManager.getTaskById(task1Id);
        assertNull(deletedTask1, "Задача не удалена");
        final Task deletedTask2 = taskManager.getTaskById(task2Id);
        assertNull(deletedTask2, "Задача не удалена");
        final Map<String, Task> tasks = taskManager.listTasks();
        assertEquals(0, tasks.size(), "Задачи не удалились из списка");
    }

    @Test
    void addsEpic() {
        final String epicID = taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epicID);

        assertNotNull(savedEpic, "Задаче на найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

        final Map<String, Epic> epics = taskManager.listEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(epic, epics.get(epicID), "Задачи не совпадают");
    }

    @Test
    void updatesEpic() {
        final String epicID = taskManager.addEpic(epic);
        final Epic updatedEpic = new Epic(
                epicID,
                EPIC,
                "Epic-1",
                "Updated Epic-1");
        taskManager.updateEpic(updatedEpic);

        final Epic savedEpic = taskManager.getEpicById(epicID);

        assertNotNull(savedEpic, "Задаче на найдена.");
        assertEquals(updatedEpic, savedEpic, "Задачи не совпадают.");

        final Map<String, Epic> epics = taskManager.listEpics();

        assertNotNull(epics, "Задачи не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество задач.");
        assertEquals(updatedEpic, epics.get(epicID), "Задачи не совпадают");
    }

    @Test
    void deletesEpic() {
        final String epicID = taskManager.addEpic(epic);
        final String subtaskID = taskManager.addSubTask(subtask1);
        taskManager.deleteEpic(epicID);
        final Epic deletedEpic = taskManager.getEpicById(epicID);
        final Subtask deletedSubtask = taskManager.getSubtaskById(subtaskID);
        assertNull(deletedEpic, "Задаче не удалена.");
        assertNull(deletedSubtask, "Задаче не удалена.");
        final Map<String, Epic> epics = taskManager.listEpics();
        assertEquals(0, epics.size(), "Задачи не удалились из списка");
        final Map<String, Subtask> subtasks = taskManager.listAllSubtasks();
        assertEquals(0, subtasks.size(), "Задачи не удалились из списка");
    }

    @Test
    void deletesAllEpics() {
        final String epic1ID = taskManager.addEpic(epic);
        final String epic2ID = taskManager.addEpic(new Epic(EPIC, "Epic-2", "New Epic-2"));
        taskManager.deleteAllEpics();
        final Epic deletedEpic1 = taskManager.getEpicById(epic1ID);
        final Epic deletedEpic2 = taskManager.getEpicById(epic2ID);
        assertNull(deletedEpic1, "Задаче не удалена.");
        assertNull(deletedEpic2, "Задаче не удалена.");
        final Map<String, Epic> epics = taskManager.listEpics();
        assertEquals(0, epics.size(), "Задачи не удалились из списка");
    }

    @Test
    void getsTaskById() {
        final String id = taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskById(id), "Задачи не совпадают.");
    }

    @Test
    void getsEpicById() {
        final String id = taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicById(id), "Задачи не совпадают.");
    }

    @Test
    void getsSubtaskById() {
        taskManager.addEpic(epic);
        final String id = taskManager.addSubTask(subtask1);
        assertEquals(subtask1, taskManager.getSubtaskById(id), "Задачи не совпадают.");
    }


    @Test
    void listsEpicSubtasks() {
        taskManager.addEpic(epic);
        final String subtask1ID = taskManager.addSubTask(subtask1);
        final String subtask2ID = taskManager.addSubTask(subtask2);
        final String subtask3ID = taskManager.addSubTask(subtask3);
        final Map<String, Subtask> subtaskList = new LinkedHashMap<>();
        subtaskList.put(subtask1ID, subtask1);
        subtaskList.put(subtask2ID, subtask2);
        subtaskList.put(subtask3ID, subtask3);
        assertEquals(subtaskList, taskManager.listEpicSubtasks(epic.getId()), "Список подзадач не правильный");
    }

    @Test
    void listsEveryTaskAndEpicAndSubtask() {
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);
        final List<Task> allTasksList = new ArrayList<>(List.of(task, epic, subtask1, subtask2));
        assertEquals(allTasksList, taskManager.listEveryTaskAndEpicAndSubtask(),
                "Список всех задач неправильный");
    }

    @Test
    void listsPrioritizedTasks() {
        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);
        taskManager.addSubTask(subtask3);
        final TreeSet<Task> taskSortedMap = new TreeSet<>(new TaskComparator());
        taskSortedMap.addAll(List.of(task, subtask2, subtask3, subtask1));
        assertEquals(taskSortedMap, taskManager.listPrioritizedTasks(), "Сортированные списки не совпадают.");
        Task nullDateTask = new Task(TASK, "", "");
        taskManager.addTask(nullDateTask);
        assertNull(taskManager.listPrioritizedTasks().last().getStartDate(),
                "Задача без даты не в конце списка");
    }
}