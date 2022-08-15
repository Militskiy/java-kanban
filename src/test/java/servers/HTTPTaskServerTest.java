package servers;

import com.google.gson.*;
import managers.InMemoryTaskManager;
import org.junit.jupiter.api.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static managers.util.Constants.UTF_8;
import static org.junit.jupiter.api.Assertions.*;
import static tasks.util.Status.*;
import static tasks.util.TaskType.*;
import static tasks.util.TaskType.SUBTASK;

class HTTPTaskServerTest {
    InMemoryTaskManager taskManager;
    HttpClient client;
    HttpRequest request;
    HttpResponse<String> response;
    URI url;
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (value, type, context) ->
                    new JsonPrimitive(value.format(DateTimeFormatter.ISO_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (jsonElement, type, context) ->
                    LocalDateTime.parse(jsonElement.getAsJsonPrimitive()
                            .getAsString(), DateTimeFormatter.ISO_DATE_TIME))
            .serializeNulls()
            .create();
    HTTPTaskServer server;
    Task task;
    Epic epic;
    Subtask subtask1;
    Subtask subtask2;
    Subtask subtask3;
    final static LocalDateTime DAY_1 = LocalDateTime.of(2022, 1, 1, 0, 0);
    final static LocalDateTime DAY_2 = LocalDateTime.of(2022, 1, 2, 0, 0);
    final static LocalDateTime DAY_3 = LocalDateTime.of(2022, 1, 3, 0, 0);
    final static LocalDateTime DAY_4 = LocalDateTime.of(2022, 1, 4, 0, 0);
    final static LocalDateTime DAY_5 = LocalDateTime.of(2022, 1, 5, 0, 0);
    String taskID;
    String epicID;
    String subtask1ID;
    String subtask2ID;
    String subtask3ID;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = new InMemoryTaskManager();
        server = new HTTPTaskServer(taskManager);
        server.start();
        task = new Task(TASK, "Task-1", "New Task-1", NEW, DAY_1, 1440);
        taskID = taskManager.addTask(task);
        epic = new Epic(EPIC, "Epic-1", "Test Epic");
        epicID = taskManager.addEpic(epic);
        subtask1 = new Subtask(
                SUBTASK,
                "Subtask-1",
                "Test Subtask-1",
                NEW,
                DAY_2,
                1440,
                epicID);
        subtask2 = new Subtask(
                SUBTASK,
                "Subtask-2",
                "Test Subtask-2",
                DONE,
                DAY_3,
                1440,
                epicID);
        subtask3 = new Subtask(
                SUBTASK,
                "Subtask-3",
                "Test Subtask-3",
                IN_PROGRESS,
                DAY_4,
                1440,
                epicID);
        subtask1ID = taskManager.addSubTask(subtask1);
        subtask2ID = taskManager.addSubTask(subtask2);
        subtask3ID = taskManager.addSubTask(subtask3);
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
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void listHistory() throws IOException, InterruptedException {
        LinkedHashMap<String, Task> testMap = taskManager.getHistory().stream()
                .collect(Collectors.toMap(Task::getId, task1 -> task1, (e1, e2) -> e1, LinkedHashMap::new));
        String testJson = GSON.toJson(testMap);
        url = URI.create("http://localhost:8080/tasks/history");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(testJson, response.body());
    }

    @Test
    void listHistoryWrongMethod() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/history");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(405, response.statusCode());
    }

    @Test
    void listHistoryWrongPath() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/historylist");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
    }

    @Test
    void tasksGetRequestNoQueryShouldListEverything() throws IOException, InterruptedException {
        LinkedHashMap<String, Task> testMap = taskManager.listEveryTaskAndEpicAndSubtask().stream()
                .collect(Collectors.toMap(Task::getId, task1 -> task1, (e1, e2) -> e1, LinkedHashMap::new));
        String testJson = GSON.toJson(testMap);
        url = URI.create("http://localhost:8080/tasks");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(testJson, response.body());
    }

    @Test
    void tasksGetRequestPrioritizedTrueQuery() throws IOException, InterruptedException {
        LinkedHashMap<String, Task> testMap = taskManager.listPrioritizedTasks().stream()
                .collect(Collectors.toMap(Task::getId, task1 -> task1, (e1, e2) -> e1, LinkedHashMap::new));
        String testJson = GSON.toJson(testMap);
        url = URI.create("http://localhost:8080/tasks?prioritized=true");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(testJson, response.body());
    }

    @Test
    void tasksWrongMethod() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks?prioritized=true");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(405, response.statusCode());
    }

    @Test
    void tasksWrongPath() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasksall");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
    }

    @Test
    void tasksWrongQuery() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks?pri");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
    }

    @Test
    void tasksTaskGetRequestNoQueryShouldListTasks() throws IOException, InterruptedException {
        String testJson = GSON.toJson(taskManager.listTasks());
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(testJson, response.body());
    }

    @Test
    void tasksTaskDeleteRequestNoQueryShouldDeleteAllTasks() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.listTasks().size());
    }

    @Test
    void tasksTaskDeleteRequestShouldDeleteSpecificTasks() throws IOException, InterruptedException {
        Task testTask = new Task(TASK, "Task-test", "New Task-test", NEW, DAY_5, 1440);
        String testTaskID = taskManager.addTask(testTask);
        assertEquals(2, taskManager.listTasks().size());
        url = URI.create("http://localhost:8080/tasks/task?id=" + testTaskID);
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(1, taskManager.listTasks().size());
    }

    @Test
    void tasksTaskDeleteWrongQuery() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task?iddqd=yes");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
        assertEquals("<h1>404 Not Found</h1>Wrong query", response.body());
    }

    @Test
    void tasksTaskGetRequestQuerySpecificTask() throws IOException, InterruptedException {
        String testJson = GSON.toJson(Stream.of(taskManager.getTaskById(taskID))
                .collect(Collectors.toMap(Task::getId, task1 -> task1)));
        url = URI.create("http://localhost:8080/tasks/task?id=" + taskID);
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(testJson, response.body());
    }

    @Test
    void tasksTaskPostNewTask() throws IOException, InterruptedException {
        Task testTask = new Task(TASK, "Task-test", "New Task-test", NEW, DAY_5, 1440);
        String testJson = GSON.toJson(testTask);
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        Task newTask = taskManager.getTaskById(response.body());
        testTask.setId(response.body());
        assertEquals(testTask, newTask);
    }

    @Test
    void tasksTaskPostUpdateTask() throws IOException, InterruptedException {
        Task testTask = new Task(
                taskID,
                TASK,
                "Task-1 updated",
                "Updated Task-1",
                IN_PROGRESS,
                DAY_1,
                1440);
        String testJson = GSON.toJson(testTask);
        url = URI.create("http://localhost:8080/tasks/task?id=" + taskID);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals("", response.body());
        Task newTask = taskManager.getTaskById(taskID);
        assertEquals(testTask, newTask);
    }

    @Test
    void tasksValidationError() throws IOException, InterruptedException {
        Task testTask = new Task(TASK, "Task-test", "New Task-test", NEW, DAY_1, 1440);
        String testJson = GSON.toJson(testTask);
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(409, response.statusCode());
        assertEquals("Cannot add Task: Task Task-test intersects with another Task", response.body());
        assertEquals(1, taskManager.listTasks().size());
    }

    @Test
    void tasksTaskWrongMethod() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString("test"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(405, response.statusCode());
    }

    @Test
    void tasksTaskWrongPath() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/taskall");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
    }

    @Test
    void tasksTaskWrongQuery() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task?pri");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
    }

    @Test
    void tasksTaskPostEmptyBodyTask() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("", UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
        assertEquals("Request body is empty, cannot add Task", response.body());
    }

    @Test
    void tasksTaskPostNullTask() throws IOException, InterruptedException {
        Task nullTask = new Task(null, null, null, null);
        String testJson = GSON.toJson(nullTask);
        url = URI.create("http://localhost:8080/tasks/task");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
        assertEquals("Request body is invalid, cannot add Task", response.body());
    }

    @Test
    void tasksTaskPostPostWrongQuery() throws IOException, InterruptedException {
        Task testTask = new Task(TASK, "Task-test", "New Task-test", NEW, DAY_1, 1440);
        String testJson = GSON.toJson(testTask);
        url = URI.create("http://localhost:8080/tasks/task?test=yes");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
        assertEquals("<h1>404 Not Found</h1>Wrong query", response.body());
    }

    @Test
    void tasksEpicGetRequestNoQueryShouldListTasks() throws IOException, InterruptedException {
        String testJson = GSON.toJson(taskManager.listEpics());
        url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(testJson, response.body());
    }

    @Test
    void tasksEpicDeleteRequestNoQueryShouldDeleteAllEpics() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.listEpics().size());
    }

    @Test
    void tasksEpicDeleteRequestShouldDeleteSpecificEpic() throws IOException, InterruptedException {
        Epic testEpic = new Epic(EPIC, "Epic-test", "New Epic-test");
        String testTaskID = taskManager.addEpic(testEpic);
        assertEquals(2, taskManager.listEpics().size());
        url = URI.create("http://localhost:8080/tasks/epic?id=" + testTaskID);
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(1, taskManager.listEpics().size());
    }

    @Test
    void tasksEpicDeleteWrongQuery() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic?iddqd=yes");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
        assertEquals("<h1>404 Not Found</h1>Wrong query", response.body());
    }

    @Test
    void tasksEpicGetRequestQuerySpecificEpic() throws IOException, InterruptedException {
        String testJson = GSON.toJson(Stream.of(taskManager.getEpicById(epicID))
                .collect(Collectors.toMap(Epic::getId, epic1 -> epic1)));
        url = URI.create("http://localhost:8080/tasks/epic?id=" + epicID);
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(testJson, response.body());
    }

    @Test
    void tasksEpicPostNewEpic() throws IOException, InterruptedException {
        Epic testEpic = new Epic(
                null,
                EPIC,
                "test",
                "New test",
                NEW,
                null,
                0,
                null);
        String testJson = GSON.toJson(testEpic);
        url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        Epic newEpic = taskManager.getEpicById(response.body());
        testEpic.setId(response.body());
        assertEquals(testEpic, newEpic);
    }

    @Test
    void tasksEpicPostUpdateEpic() throws IOException, InterruptedException {
        Epic testEpic = taskManager.getEpicById(epicID);
        testEpic.setName("Updated epic-1");
        String testJson = GSON.toJson(testEpic);
        url = URI.create("http://localhost:8080/tasks/epic?id=" + epicID);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals("", response.body());
        Epic newEpic = taskManager.getEpicById(epicID);
        assertEquals(testEpic, newEpic);
        assertEquals("Updated epic-1", newEpic.getName());
    }

    @Test
    void tasksEpicWrongMethod() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString("test"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(405, response.statusCode());
    }

    @Test
    void tasksEpicWrongPath() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epicall");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
    }

    @Test
    void tasksEpicWrongQuery() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic?pri");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
    }

    @Test
    void tasksEpicPostEmptyBodyEpic() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("", UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
        assertEquals("Request body is empty, cannot add Epic", response.body());
    }

    @Test
    void tasksEpicPostNullEpic() throws IOException, InterruptedException {
        Epic nullTask = new Epic(null, null, null, null);
        String testJson = GSON.toJson(nullTask);
        url = URI.create("http://localhost:8080/tasks/epic");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
        assertEquals("Request body is invalid, cannot add Epic", response.body());
    }

    @Test
    void tasksEpicPostPostWrongQuery() throws IOException, InterruptedException {
        Epic testEpic = new Epic(epicID, EPIC, "Epic-1 updated", "Updated Epic-1");
        String testJson = GSON.toJson(testEpic);
        url = URI.create("http://localhost:8080/tasks/epic?test=yes");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
        assertEquals("<h1>404 Not Found</h1>Wrong query", response.body());
    }

    @Test
    void tasksSubTaskGetRequestNoQueryShouldListSubTasks() throws IOException, InterruptedException {
        String testJson = GSON.toJson(taskManager.listAllSubtasks());
        url = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(testJson, response.body());
    }

    @Test
    void listAllSubtasksOfSpecificEpic() throws IOException, InterruptedException {
        String testJson = GSON.toJson(taskManager.listEpicSubtasks(epicID));
        url = URI.create("http://localhost:8080/tasks/subtask/epic?id=" + epicID);
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(testJson, response.body());
    }

    @Test
    void listAllSubtasksOfSpecificEpicWrongQuery() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/subtask/epic?iddqd=yes");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
        assertEquals("<h1>404 Not Found</h1>Wrong query", response.body());
    }

    @Test
    void tasksSubTaskDeleteRequestNoQueryShouldDeleteAllSubTasks() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(0, taskManager.listAllSubtasks().size());
    }

    @Test
    void tasksSubTaskDeleteRequestShouldDeleteSpecificSubTasks() throws IOException, InterruptedException {
        Subtask testTask = new Subtask(
                TASK,
                "Subtask-test",
                "New Subtask-test",
                NEW,
                DAY_5,
                1440,
                epicID);
        String testTaskID = taskManager.addSubTask(testTask);
        assertEquals(4, taskManager.listAllSubtasks().size());
        url = URI.create("http://localhost:8080/tasks/subtask?id=" + testTaskID);
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(3, taskManager.listAllSubtasks().size());
    }

    @Test
    void tasksSubTaskDeleteWrongQuery() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/subtask?iddqd=yes");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
        assertEquals("<h1>404 Not Found</h1>Wrong query", response.body());
    }

    @Test
    void tasksSubTaskGetRequestQuerySpecificSubTask() throws IOException, InterruptedException {
        String testJson = GSON.toJson(Stream.of(taskManager.getSubtaskById(subtask1ID))
                .collect(Collectors.toMap(Subtask::getId, task1 -> task1)));
        url = URI.create("http://localhost:8080/tasks/subtask?id=" + subtask1ID);
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals(testJson, response.body());
    }

    @Test
    void tasksSubTaskPostNewSubTask() throws IOException, InterruptedException {
        Subtask testTask = new Subtask(
                TASK,
                "Subtask-test",
                "New Subtask-test",
                NEW,
                DAY_5,
                1440,
                epicID);
        String testJson = GSON.toJson(testTask);
        url = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        Subtask newTask = taskManager.getSubtaskById(response.body());
        testTask.setId(response.body());
        assertEquals(testTask, newTask);
    }

    @Test
    void tasksSubTaskPostUpdateSubTask() throws IOException, InterruptedException {
        Subtask testTask = new Subtask(
                subtask1ID,
                SUBTASK,
                "Task-1 updated",
                "Updated Task-1",
                IN_PROGRESS,
                epicID,
                DAY_2,
                1440);
        String testJson = GSON.toJson(testTask);
        url = URI.create("http://localhost:8080/tasks/subtask?id=" + subtask1ID);
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(200, response.statusCode());
        assertEquals("", response.body());
        Subtask newTask = taskManager.getSubtaskById(subtask1ID);
        assertEquals(testTask, newTask);
    }

    @Test
    void subtasksValidationError() throws IOException, InterruptedException {
        Subtask testTask = new Subtask(
                SUBTASK,
                "Subtask-test",
                "New Subtask-test",
                NEW,
                DAY_1,
                1440,
                epicID);
        String testJson = GSON.toJson(testTask);
        url = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(409, response.statusCode());
        assertEquals("Cannot add Subtask: Subtask Subtask-test intersects with another Task", response.body());
        assertEquals(3, taskManager.listAllSubtasks().size());
    }

    @Test
    void tasksSubTaskWrongMethod() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder()
                .uri(url)
                .PUT(HttpRequest.BodyPublishers.ofString("test"))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(405, response.statusCode());
    }

    @Test
    void tasksSubTaskWrongPath() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/subtaskall");
        request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
    }

    @Test
    void tasksSubTaskWrongQuery() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/subtask?pri");
        request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
    }

    @Test
    void tasksSubTaskPostEmptyBodyTask() throws IOException, InterruptedException {
        url = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString("", UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
        assertEquals("Request body is empty, cannot add Task", response.body());
    }

    @Test
    void tasksSubTaskPostNullTask() throws IOException, InterruptedException {
        Subtask nullTask = new Subtask(null, null, null, null);
        String testJson = GSON.toJson(nullTask);
        url = URI.create("http://localhost:8080/tasks/subtask");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(400, response.statusCode());
        assertEquals("Request body is invalid, cannot add Task", response.body());
    }

    @Test
    void tasksSubTaskPostPostWrongQuery() throws IOException, InterruptedException {
        Subtask testTask = new Subtask(
                TASK,
                "Subtask-test",
                "New Subtask-test",
                NEW,
                DAY_5,
                1440,
                epicID);
        String testJson = GSON.toJson(testTask);
        url = URI.create("http://localhost:8080/tasks/subtask?test=yes");
        request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(testJson, UTF_8))
                .build();
        response = client.send(request, HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(404, response.statusCode());
        assertEquals("<h1>404 Not Found</h1>Wrong query", response.body());
    }

}