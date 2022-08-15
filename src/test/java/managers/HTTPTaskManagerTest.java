package managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.KVServer;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.util.List;

import static managers.util.Constants.KV_SERVER_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tasks.util.Status.*;
import static tasks.util.TaskType.*;

public class HTTPTaskManagerTest extends TaskManagerTest<HTTPTaskManager> {

    private KVServer server;

    @BeforeEach
    void setUp() throws IOException {
        server = new KVServer();
        server.start();
        taskManager = new HTTPTaskManager(KV_SERVER_URL);
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

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void dataIsLoadedFromDataGeneratedHere() {
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
        final HTTPTaskManager httpTaskManager = new HTTPTaskManager(KV_SERVER_URL);
        httpTaskManager.load(KV_SERVER_URL);
        final List<Task> loadedList = httpTaskManager.listEveryTaskAndEpicAndSubtask();
        for (int i = 0; i < allTaskList.size(); i++) {
            assertEquals(allTaskList.get(i).toString(), loadedList.get(i).toString(),
                    "Загрузка задач прошла с ошибками");
        }

        final List<Task> historyList = taskManager.historyManager.getHistory();
        final List<Task> loadedHistoryList = httpTaskManager.historyManager.getHistory();
        for (int i = 0; i < historyList.size(); i++) {
            assertEquals(historyList.get(i), loadedHistoryList.get(i),
                    "Загрузка истории прошла с ошибками");
        }
    }

    @Test
    void addsTasksToLoadedManager() {
        taskManager.addSubTask(subtask1);
        final HTTPTaskManager httpTaskManager = new HTTPTaskManager(KV_SERVER_URL);
        httpTaskManager.load(KV_SERVER_URL);
        final Task newNullDateTask = new Task(TASK, "Task-2", "New Task-2");
        final Task newTask = new Task(TASK, "Task-2", "New Task-2", NEW, DAY_4.plusWeeks(1),
                1440);
        final Epic newEpic = new Epic(EPIC, "Epic-2", "Test Epic-2");
        httpTaskManager.addTask(newTask);
        final String newTaskID = httpTaskManager.addTask(newNullDateTask);
        final String newEpicID = httpTaskManager.addEpic(newEpic);
        final Subtask newSubtask = new Subtask(
                SUBTASK, "Subtask-2", "Test Subtask-2", newEpicID);
        final String subtask2ID = httpTaskManager.addSubTask(newSubtask);
        final Task savedTask = httpTaskManager.getTaskById(newTaskID);
        final Epic savedEpic = httpTaskManager.getEpicById(newEpicID);
        final Subtask savedSubtask = httpTaskManager.getSubtaskById(subtask2ID);

        assertNotNull(savedTask, "Задача не найдена.");
        assertNotNull(savedEpic, "Задача не найдена.");
        assertNotNull(savedSubtask, "Задача не найдена.");
        assertEquals(newNullDateTask, httpTaskManager.getTaskById(newTaskID), "Задачи не совпадают.");
        assertEquals(newEpic, httpTaskManager.getEpicById(newEpicID), "Задачи не совпадают.");
        assertEquals(newSubtask, httpTaskManager.getSubtaskById(subtask2ID), "Задачи не совпадают.");
        assertEquals(savedEpic.getId(), savedSubtask.getEpicId(), "Не правильный эпик в задаче");
        assertEquals(savedSubtask.getId(), savedEpic.getSubtaskIdList().get(0), "Задача не добавилась в список эпика");
        assertEquals(NEW, savedEpic.getStatus(), "Неверный статус эпика");
        assertEquals(6, httpTaskManager.listEveryTaskAndEpicAndSubtask().size());
    }

}
