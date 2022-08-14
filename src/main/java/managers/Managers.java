package managers;

import static managers.util.Constants.KV_SERVER_URL;

public final class Managers {

    // Приватный конструктор для защиты от создания экземпляров класса
    private Managers() {
    }

    // Методы выбора менеджеров по умолчанию
    public static TaskManager getDefault() {
        return new HTTPTaskManager(KV_SERVER_URL);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
