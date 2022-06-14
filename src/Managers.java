public final class Managers {
    // Приватный констуктор для защиты от создания экземпляров класса
    private Managers() {
    }
    // Методы выбора менеджеров по умолчанию
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
