package managers;

public final class Managers {
    private static final TaskManager taskManager = new InMemoryTaskManager();
    private static final HistoryManager historyManager = new InMemoryHistoryManager();

    // Приватный констуктор для защиты от создания экземпляров класса
    private Managers() {
    }

    // Методы выбора менеджеров по умолчанию
    public static TaskManager getDefault() {
        return taskManager;
    }

    public static HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
