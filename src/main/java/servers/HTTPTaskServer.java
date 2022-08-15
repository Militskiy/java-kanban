package servers;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import managers.TaskManager;
import managers.exceptions.ValidationException;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class HTTPTaskServer {

    private static final int PORT = 8080;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (value, type, context) ->
                    new JsonPrimitive(value.format(DateTimeFormatter.ISO_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (jsonElement, type, context) ->
                    LocalDateTime.parse(jsonElement.getAsJsonPrimitive()
                            .getAsString(), DateTimeFormatter.ISO_DATE_TIME))
            .serializeNulls()
            .create();
    private final TaskManager tasksManager;
    private final HttpServer server;

    public HTTPTaskServer(TaskManager taskManager) throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks/task", this::handleTasks);
        server.createContext("/tasks/epic", this::handleEpics);
        server.createContext("/tasks/subtask", this::handleSubtasks);
        server.createContext("/tasks", this::handleAllTasks);
        server.createContext("/tasks/history", this::handleHistory);
        this.tasksManager = taskManager;
    }

    private void handleHistory(HttpExchange exchange) throws IOException {
        try {
            String getPath = exchange.getRequestURI().getPath();
            if (getPath.replace("/", "").equals("taskshistory")) {
                if (exchange.getRequestMethod().equals("GET")) {
                    String getQuery = exchange.getRequestURI().getQuery();
                    if (getQuery == null) {
                        sendTasks(exchange, tasksManager.getHistory().stream()
                                .collect(Collectors
                                        .toMap(Task::getId, task -> task, (e1, e2) -> e1, LinkedHashMap::new)));
                        System.out.println("Sending task request history");
                    }
                } else {
                    System.out.println("Unsupported request method");
                    exchange.sendResponseHeaders(405, 0);
                }
            } else {
                sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong path");
            }
        } finally {
            exchange.close();
        }
    }

    private void handleAllTasks(HttpExchange exchange) throws IOException {
        try {
            String getPath = exchange.getRequestURI().getPath();
            if (getPath.replace("/", "").equals("tasks")) {
                if (exchange.getRequestMethod().equals("GET")) {
                    String getQuery = exchange.getRequestURI().getQuery();
                    if (getQuery == null) {
                        sendTasks(exchange, tasksManager.listEveryTaskAndEpicAndSubtask().stream()
                                .collect(Collectors
                                        .toMap(Task::getId, task -> task, (e1, e2) -> e1, LinkedHashMap::new)));
                        System.out.println("Sending all tasks");
                    } else if (getQuery.equals("prioritized=true")) {
                        sendTasks(exchange, tasksManager.listPrioritizedTasks().stream()
                                .collect(Collectors
                                        .toMap(Task::getId, task -> task, (e1, e2) -> e1, LinkedHashMap::new)));
                        System.out.println("Sending prioritized tasks");
                    } else {
                        sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong query");
                    }
                } else {
                    System.out.println("Unsupported request method");
                    exchange.sendResponseHeaders(405, 0);
                }
            } else {
                sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong path");
            }
        } finally {
            exchange.close();
        }
    }

    private void handleTasks(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.replace("/", "").equals("taskstask")) {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        getHandler(exchange, "TASK");
                        break;
                    case "POST":
                        String postQuery = exchange.getRequestURI().getQuery();
                        Task task = readTask(exchange);
                        try {
                            if (task == null) {
                                System.out.println("Invalid POST/tasks/Task request");
                                sendBadRequestMessage(exchange, "Request body is empty, cannot add Task");
                                return;
                            } else if (task.getType() == null || task.getName() == null) {
                                System.out.println("Invalid POST/tasks/Task request");
                                sendBadRequestMessage(exchange, "Request body is invalid, cannot add Task");
                                return;
                            } else if (postQuery == null) {
                                tasksManager.addTask(task);
                                System.out.println("Added " + task.getType() + " " + task.getName());
                                sendText(exchange, task.getId());
                            } else if (postQuery.split("=")[0].equals("id") &&
                                    task.getId().equals(postQuery.split("=")[1])) {
                                tasksManager.updateTask(task);
                                System.out.println("Updated " + task.getType() + " " + task.getName());
                                exchange.sendResponseHeaders(200, 0);
                            } else {
                                sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong query");
                            }
                        } catch (ValidationException exception) {
                            System.out.println("Validation error");
                            sendValidationError(exchange, exception);
                        }
                        break;
                    case "DELETE":
                        deleteHandler(exchange, "TASK");
                        break;
                    default:
                        System.out.println("Unsupported request method");
                        exchange.sendResponseHeaders(405, 0);
                }
            } else {
                sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong path");
            }
        } finally {
            exchange.close();
        }
    }


    private void handleEpics(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.replace("/", "").equals("tasksepic")) {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        getHandler(exchange, "EPIC");
                        break;
                    case "POST":
                        String postQuery = exchange.getRequestURI().getQuery();
                        Epic epic = readEpic(exchange);
                        if (epic == null) {
                            System.out.println("Invalid POST/tasks/Task request");
                            sendBadRequestMessage(exchange, "Request body is empty, cannot add Epic");
                            return;
                        } else if (epic.getType() == null || epic.getName() == null) {
                            System.out.println("Invalid POST/tasks/Task request");
                            sendBadRequestMessage(exchange, "Request body is invalid, cannot add Epic");
                            return;
                        } else if (postQuery == null) {
                            tasksManager.addEpic(epic);
                            System.out.println("Added " + epic.getType() + " " + epic.getName());
                            sendText(exchange, epic.getId());
                        } else if (postQuery.split("=")[0].equals("id") &&
                                epic.getId().equals(postQuery.split("=")[1])) {
                            tasksManager.updateEpic(epic);
                            System.out.println("Updated " + epic.getType() + " " + epic.getName());
                            exchange.sendResponseHeaders(200, 0);
                        } else {
                            sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong query");
                        }
                        break;
                    case "DELETE":
                        deleteHandler(exchange, "EPIC");
                        break;
                    default:
                        System.out.println("Unsupported request method");
                        exchange.sendResponseHeaders(405, 0);
                }
            } else {
                sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong path");
            }
        } finally {
            exchange.close();
        }
    }


    private void handleSubtasks(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            if (path.replace("/", "").equals("taskssubtask") ||
                    path.replace("/", "").equals("taskssubtaskepic")) {
                switch (exchange.getRequestMethod()) {
                    case "GET":
                        getHandler(exchange, "SUBTASK");
                        break;
                    case "POST":
                        String postQuery = exchange.getRequestURI().getQuery();
                        Subtask subtask = readSubtask(exchange);
                        try {
                            if (subtask == null) {
                                System.out.println("Invalid POST/tasks/Task request");
                                sendBadRequestMessage(exchange, "Request body is empty, cannot add Task");
                                return;
                            } else if (subtask.getType() == null || subtask.getName() == null) {
                                System.out.println("Invalid POST/tasks/Task request");
                                sendBadRequestMessage(exchange, "Request body is invalid, cannot add Task");
                                return;
                            } else if (postQuery == null) {
                                tasksManager.addSubTask(subtask);
                                System.out.println("Added " + subtask.getType() + " " + subtask.getName());
                                sendText(exchange, subtask.getId());
                            } else if (postQuery.split("=")[0].equals("id") &&
                                    subtask.getId().equals(postQuery.split("=")[1])) {
                                tasksManager.updateSubtask(subtask);
                                System.out.println("Updated " + subtask.getType() + " " + subtask.getName());
                                exchange.sendResponseHeaders(200, 0);
                            } else {
                                sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong query");
                            }
                        } catch (ValidationException exception) {
                            System.out.println("Validation error");
                            sendValidationError(exchange, exception);
                        }
                        break;
                    case "DELETE":
                        deleteHandler(exchange, "SUBTASK");
                        break;
                    default:
                        System.out.println("Unsupported request method");
                        exchange.sendResponseHeaders(405, 0);
                }
            } else {
                sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong path");
            }
        } finally {
            exchange.close();
        }
    }

    private <T extends Task> void sendTasks(HttpExchange exchange, Map<String, T> taskMap) throws IOException {
        byte[] response = GSON.toJson(taskMap).getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, 0);
        exchange.getResponseBody().write(response);
    }

    private void sendText(HttpExchange exchange, String text) throws IOException {
        byte[] response = text.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        exchange.sendResponseHeaders(200, 0);
        exchange.getResponseBody().write(response);
    }

    private Task readTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        if (body.isEmpty()) {
            return null;
        }
        return GSON.fromJson(body, Task.class);
    }

    private Epic readEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        if (body.isEmpty()) {
            return null;
        }
        return GSON.fromJson(body, Epic.class);
    }

    private Subtask readSubtask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
        if (body.isEmpty()) {
            return null;
        }
        return GSON.fromJson(body, Subtask.class);
    }

    private void sendValidationError(HttpExchange exchange, ValidationException e) throws IOException {
        byte[] response = e.getDetailedMessage().getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "text/plain");
        exchange.sendResponseHeaders(409, 0);
        exchange.getResponseBody().write(response);
    }

    private void sendErrorMessage(HttpExchange exchange, String message) throws IOException {
        byte[] response = message.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "text/html");
        exchange.sendResponseHeaders(404, 0);
        exchange.getResponseBody().write(response);
    }

    private void sendBadRequestMessage(HttpExchange exchange, String message) throws IOException {
        byte[] response = message.getBytes(DEFAULT_CHARSET);
        exchange.getResponseHeaders().add("Content-Type", "text/html");
        exchange.sendResponseHeaders(400, 0);
        exchange.getResponseBody().write(response);
    }

    private void getHandler(HttpExchange exchange, String taskType) throws IOException {
        String getQuery = exchange.getRequestURI().getQuery();
        String getPath = exchange.getRequestURI().getPath();
        switch (taskType) {
            case "TASK":
                if (getQuery == null) {
                    sendTasks(exchange, tasksManager.listTasks());
                    System.out.println("Sending all tasks");
                } else if (getQuery.split("=")[0].equals("id")) {
                    String id = getQuery.substring("id=".length());
                    sendTasks(exchange, Map.of(id, tasksManager.getTaskById(id)));
                    System.out.println("Sending task by ID");
                } else {
                    sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong query");
                }
                break;
            case "EPIC":
                if (getQuery == null) {
                    sendTasks(exchange, tasksManager.listEpics());
                    System.out.println("Sending all tasks");
                } else if (getQuery.split("=")[0].equals("id")) {
                    String id = getQuery.substring("id=".length());
                    sendTasks(exchange, Map.of(id, tasksManager.getEpicById(id)));
                    System.out.println("Sending task by ID");
                } else {
                    sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong query");
                }
                break;
            case "SUBTASK":
                if (getPath.replace("/", "").equals("taskssubtask")) {
                    if (getQuery == null) {
                        sendTasks(exchange, tasksManager.listAllSubtasks());
                        System.out.println("Sending all tasks");
                    } else if (getQuery.split("=")[0].equals("id")) {
                        String id = getQuery.substring("id=".length());
                        sendTasks(exchange, Map.of(id, tasksManager.getSubtaskById(id)));
                        System.out.println("Sending task by ID");
                    } else {
                        sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong query");
                    }
                } else if (getPath.replace("/", "").equals("taskssubtaskepic")) {
                    if (getQuery != null && getQuery.split("=")[0].equals("id")) {
                        String id = getQuery.substring("id=".length());
                        sendTasks(exchange, tasksManager.listEpicSubtasks(id));
                        System.out.println("Sending epic subtasks");
                    } else {
                        sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong query");
                    }
                }
                break;
        }
    }

    private void deleteHandler(HttpExchange exchange, String taskType) throws IOException {
        String deleteQuery = exchange.getRequestURI().getQuery();
        switch (taskType) {
            case "TASK":
                if (deleteQuery == null) {
                    tasksManager.deleteAllTasks();
                    System.out.println("Deleted all tasks");
                    exchange.sendResponseHeaders(200, 0);
                } else if (deleteQuery.split("=")[0].equals("id")) {
                    String id = deleteQuery.substring("id=".length());
                    tasksManager.deleteTask(id);
                    System.out.println("Deleted task with id: " + id);
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong query");
                }
                break;
            case "EPIC":
                if (deleteQuery == null) {
                    tasksManager.deleteAllEpics();
                    System.out.println("Deleted all tasks");
                    exchange.sendResponseHeaders(200, 0);
                } else if (deleteQuery.split("=")[0].equals("id")) {
                    String id = deleteQuery.substring("id=".length());
                    tasksManager.deleteEpic(id);
                    System.out.println("Deleted task with id: " + id);
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong query");
                }
                break;
            case "SUBTASK":
                if (deleteQuery == null) {
                    tasksManager.deleteAllSubTasks();
                    System.out.println("Deleted all tasks");
                    exchange.sendResponseHeaders(200, 0);
                } else if (deleteQuery.split("=")[0].equals("id")) {
                    String id = deleteQuery.substring("id=".length());
                    tasksManager.deleteSubTask(id);
                    System.out.println("Deleted task with id: " + id);
                    exchange.sendResponseHeaders(200, 0);
                } else {
                    sendErrorMessage(exchange, "<h1>404 Not Found</h1>Wrong query");
                }
                break;
        }
    }

    public void start() {
        System.out.println("HTTPTaskServer: http://localhost:" + PORT + "/tasks");
        server.start();
    }

    public void stop() {
        server.stop(0);
    }
}
