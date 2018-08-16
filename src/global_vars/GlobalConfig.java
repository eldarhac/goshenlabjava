package global_vars;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GlobalConfig {
    // root directory for the project, change this before compilation
    // to be the path of the directory in which the project is stored
    private static String basePath = "C:\\Users\\owner\\Documents\\VR_PROJECT";
    // gets system path delimiter
    private static String system_del = System.getProperty("os.name").startsWith("Windows") ? "\\" : "/";
    private static String replacement_del = system_del.equals("\\") ? "/": "\\";

    /**
     * creates a global system invariant path
     * @param path a path to manipulate
     * @return a global path
     */
    public static String makePathGlobalString(String path){

        return Paths.get(basePath.replace(replacement_del, system_del), path.replace(replacement_del, system_del)).toString();
    }

    /**
     * creates a global system invariant path
     * @param path a path to manipulate
     * @return a global path
     */
    public static Path makePathGlobalPath(String path){

        return Paths.get(basePath.replace(replacement_del, system_del), path.replace(replacement_del, system_del));
    }

    /**
     * creates a local system invariant path
     * @param path a path to manipulate
     * @return a global path
     */
    public static String makePathSysytemIn(String path){
        return path.replace(replacement_del, system_del);
    }
}
