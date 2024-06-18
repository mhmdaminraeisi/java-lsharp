package institute.teias.IO;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class FileManager {

    public static void deleteDirIfExistsAndCreateNew(String dirName) {
        File file = new File(dirName);
        if (file.exists()) {
            Arrays.stream(Objects.requireNonNull(file.list()))
                    .map(s -> new File(file.getPath(), s))
                    .forEach(File::deleteOnExit);
        } else file.mkdir();
    }

    public static void createDirIfNotExists(String dirName) {
        File file = new File(dirName);
        if (!file.exists()) file.mkdir();
    }

    public static void createFileIfNotExists(String dirName) throws IOException {
        File file = new File(dirName);
        if (!file.exists()) {
            file.createNewFile();
        }
    }
}
