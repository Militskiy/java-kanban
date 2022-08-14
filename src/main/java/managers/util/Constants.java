package managers.util;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class Constants {
    private Constants() {
    }

    public static final String DELIMITER = ",";
    public static final String DEFAULT_FILE_PATH = System.getProperty("user.dir") + "\\src\\main\\java\\data\\savedState.csv";
    public static final String NEXT_LINE = System.lineSeparator();

    public static final String KV_SERVER_URL = "http://localhost:8078/";
    public static final Charset UTF_8 = StandardCharsets.UTF_8;
}
