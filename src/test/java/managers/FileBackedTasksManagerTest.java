package managers;

import managers.exceptions.LoadStateException;
import managers.exceptions.ManagerSaveException;
import managers.util.StateSaver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.util.List;

import static managers.util.Constants.DEFAULT_FILE_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static tasks.util.Status.*;
import static tasks.util.TaskType.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @BeforeEach
    void beforeEach() {
        StateSaver.saveEmptyState(DEFAULT_FILE_PATH);
        taskManager = new FileBackedTasksManager(DEFAULT_FILE_PATH);
        task = new Task(TASK, "Task-1", "New Task-1", NEW, DAY_1, 1440);
        epic = new Epic(EPIC, "Epic-1", "Test Epic");
        final String epicID = taskManager.addEpic(epic);
        subtask1 = new Subtask(SUBTASK, "Subtask-1", "Test Subtask-1",
                NEW, DAY_2, 1440, epicID);
        subtask2 = new Subtask(SUBTASK, "Subtask-2", "Test Subtask-2",
                DONE, DAY_3, 1440, epicID);
        subtask3 = new Subtask(SUBTASK, "Subtask-3", "Test Subtask-3",
                IN_PROGRESS, DAY_4, 1440, epicID);

    }

    @Test
    void loadsFromFileGeneratedHere() {
        taskManager.addTask(task);
        taskManager.addSubTask(subtask1);
        taskManager.addSubTask(subtask2);
        taskManager.addSubTask(subtask3);
        taskManager.listEveryTaskAndEpicAndSubtask().forEach(task1 -> {
            switch (task1.getType()) {
                case TASK:
                    taskManager.getTaskById(task1.getId());
                    break;
                case EPIC:
                    taskManager.getEpicById(task1.getId());
                    break;
                case SUBTASK:
                    taskManager.getSubtaskById(task1.getId());
            }
        });
        final List<Task> allTaskList = taskManager.listEveryTaskAndEpicAndSubtask();
        final FileBackedTasksManager fileManager = new FileBackedTasksManager(DEFAULT_FILE_PATH);
        final List<Task> loadedList = fileManager.listEveryTaskAndEpicAndSubtask();
        for (int i = 0; i < allTaskList.size(); i++) {
            assertEquals(allTaskList.get(i), loadedList.get(i),
                    "Загрузка задач прошла с ошибками");
        }

        final List<Task> historyList = taskManager.historyManager.getHistory();
        final List<Task> loadedHistoryList = fileManager.historyManager.getHistory();
        for (int i = 0; i < historyList.size(); i++) {
            assertEquals(historyList.get(i), loadedHistoryList.get(i),
                    "Загрузка истории прошла с ошибками");
        }
    }

    @Test
    void throwsManagerSaveExceptionWhenSavingToBadPath() {
        taskManager.addTask(task);
        ManagerSaveException ex = assertThrows(ManagerSaveException.class,
                () -> StateSaver.saveState(taskManager.listEveryTaskAndEpicAndSubtask(), taskManager.historyManager.getHistory(),
                        "V:\\hopefullyNoSuchDiskExists.csv"));
        assertEquals("V:\\hopefullyNoSuchDiskExists.csv (The system cannot find the path specified)",
                ex.getMessage());
    }

    @Test
    void addsTasksToLoadedManager() {
        taskManager.addSubTask(subtask1);
        final FileBackedTasksManager fileManager = new FileBackedTasksManager(DEFAULT_FILE_PATH);
        final Task newNullDateTask = new Task(TASK, "Task-2", "New Task-2");
        final Task newTask = new Task(TASK, "Task-2", "New Task-2", NEW, DAY_4.plusWeeks(1),
                1440);
        final Epic newEpic = new Epic(EPIC, "Epic-2", "Test Epic-2");
        fileManager.addTask(newTask);
        final String newTaskID = fileManager.addTask(newNullDateTask);
        final String newEpicID = fileManager.addEpic(newEpic);
        final Subtask newSubtask = new Subtask(
                SUBTASK, "Subtask-2", "Test Subtask-2", newEpicID);
        final String subtask2ID = fileManager.addSubTask(newSubtask);
        final Task savedTask = fileManager.getTaskById(newTaskID);
        final Epic savedEpic = fileManager.getEpicById(newEpicID);
        final Subtask savedSubtask = fileManager.getSubtaskById(subtask2ID);

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(savedEpic, "Задача не найдена.");
        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(newNullDateTask, fileManager.getTaskById(newTaskID), "Задачи не совпадают.");
        assertEquals(newEpic, fileManager.getEpicById(newEpicID), "Задачи не совпадают.");
        assertEquals(newSubtask, fileManager.getSubtaskById(subtask2ID), "Задачи не совпадают.");
        assertEquals(savedEpic.getId(), savedSubtask.getEpicId(), "Не правильный эпик в задаче");
        assertEquals(savedSubtask.getId(), savedEpic.getSubtaskIdList().get(0), "Задача не добавилась в список эпика");
        assertEquals(NEW, savedEpic.getStatus(), "Неверный статус эпика");
        assertEquals(6, fileManager.listEveryTaskAndEpicAndSubtask().size());
    }

    @Test
    void throwsLoadStateExceptionWhenLoadingNonexistentFile() {
        LoadStateException ex = assertThrows(LoadStateException.class,
                () -> new FileBackedTasksManager("C:\\Files\\fileThatDoesntExist.csv"));
        assertEquals("C:\\Files\\fileThatDoesntExist.csv file not found or not accessible", ex.getMessage());
    }
}