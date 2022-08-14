package managers.util;

import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static managers.util.Constants.DELIMITER;

public final class Converter {

    private final static Gson GSON = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, (JsonSerializer<LocalDateTime>) (value, type, context) ->
                    new JsonPrimitive(value.format(DateTimeFormatter.ISO_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class, (JsonDeserializer<LocalDateTime>) (jsonElement, type, context) ->
                    LocalDateTime.parse(jsonElement.getAsJsonPrimitive()
                            .getAsString(), DateTimeFormatter.ISO_DATE_TIME))
            .serializeNulls()
            .create();
    private Converter() {
    }

    public static <T> String convertListToJson(List<T> list) {
        return GSON.toJson(list);
    }
    public static List<Task> convertJsonToTaskList(String json) {
        return GSON.fromJson(json, new TypeToken<List<Task>>(){}.getType());
    }
    public static List<Epic> convertJsonToEpicList(String json) {
        return GSON.fromJson(json, new TypeToken<List<Epic>>(){}.getType());
    }
    public static List<Subtask> convertJsonToSubtaskList(String json) {
        return GSON.fromJson(json, new TypeToken<List<Subtask>>(){}.getType());
    }

    public static String convertTaskToCSV(Task task) {
        if (task.getClass().equals(Subtask.class)) {
            if (task.getStartDate() == null) {
                return task.getId() + DELIMITER +
                        task.getType() + DELIMITER +
                        task.getName() + DELIMITER +
                        task.getStatus() + DELIMITER +
                        task.getDescription() + DELIMITER +
                        ((Subtask) task).getEpicId() + DELIMITER +
                        task.getStartDate() + DELIMITER +
                        task.getDuration();
            }
            return task.getId() + DELIMITER +
                    task.getType() + DELIMITER +
                    task.getName() + DELIMITER +
                    task.getStatus() + DELIMITER +
                    task.getDescription() + DELIMITER +
                    ((Subtask) task).getEpicId() + DELIMITER +
                    task.getStartDate().toString() + DELIMITER +
                    task.getDuration();
        }
        if (task.getStartDate() == null) {
            return task.getId() + DELIMITER +
                    task.getType() + DELIMITER +
                    task.getName() + DELIMITER +
                    task.getStatus() + DELIMITER +
                    task.getDescription() + DELIMITER +
                    "" + DELIMITER +
                    task.getStartDate() + DELIMITER +
                    task.getDuration();
        }
        return task.getId() + DELIMITER +
                task.getType() + DELIMITER +
                task.getName() + DELIMITER +
                task.getStatus() + DELIMITER +
                task.getDescription() + DELIMITER +
                "" + DELIMITER +
                task.getStartDate().toString() + DELIMITER +
                task.getDuration();
    }
}
