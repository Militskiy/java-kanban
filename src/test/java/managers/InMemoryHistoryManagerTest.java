package managers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static tasks.util.Status.NEW;
import static tasks.util.TaskType.*;



class InMemoryHistoryManagerTest {

    private HistoryManager historyManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;
    final static LocalDateTime DAY_1 = LocalDateTime.of(2022, 1, 1, 0, 0);
    final static LocalDateTime DAY_2 = LocalDateTime.of(2022, 1, 2, 0, 0);

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task = new Task("Task-id", TASK, "Task-1", "New Task-1", NEW, DAY_1, 1440);
        epic = new Epic("Epic-id",EPIC, "Epic-1", "Test Epic");
        subtask = new Subtask("Subtask-id",SUBTASK, "Subtask-1", "Test Subtask-1",
                NEW, epic.getId(), DAY_2, 1440);
    }

    @Test
    void geEmptyHistory() {
        List<Task> history = historyManager.getHistory();
        assertNotNull(history);
        assertEquals(0, history.size());
    }

    @Test
    void add() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
    }
    @Test
    void addTwoSameTasks() {
        historyManager.add(task);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
    }
    @Test
    void addThreeDifferentTasks() {
        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subtask);
        historyManager.add(subtask);
        historyManager.add(epic);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        List<Task> orderTest = List.of(task, epic, subtask);
        assertEquals(orderTest, historyManager.getHistory(), "История отображается не корректно");
    }

    @Test
    void remove() {
        historyManager.add(task);
        historyManager.remove(List.of(task.getId()));
        assertEquals(0, historyManager.getHistory().size());
    }
}