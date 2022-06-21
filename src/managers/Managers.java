package managers;

public final class Managers {
    private static final TaskManager TASK_MANAGER = new InMemoryTaskManager();
    private static final HistoryManager HISTORY_MANAGER = new InMemoryHistoryManager();

    // Приватный констуктор для защиты от создания экземпляров класса
    private Managers() {
    }

    // Методы выбора менеджеров по умолчанию
    public static TaskManager getDefault() {
        return TASK_MANAGER;
    }

    public static HistoryManager getDefaultHistory() {
        return HISTORY_MANAGER;
    }
}
