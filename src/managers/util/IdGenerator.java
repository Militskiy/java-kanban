package managers.util;

import java.util.UUID;

public final class IdGenerator {
    private IdGenerator() {
    }

    // Сохраняю класс для возможности смены способа генерации ИД
    public static String generateID() {
        return UUID.randomUUID().toString();
    }
}