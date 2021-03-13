package FileManager;

public interface Commands {
    String LIST_OF_FILES = "ls";
    String LIST_OF_WITH_SIZE = "ll";
    String COPY_FILE = "cp";
    String CREATE_FILE = "touch";
    String FILE_CONTENT = "cat";
    String CHANGE_DIRECTORY = "cd";
    String EXIT = "exit";

    String MESSAGE = "msg";
    String AUTH = "auth";

    String SERVER = "srv";
    String SERVER_DOWNLOAD = "srvdwn";
    String SERVER_REQUEST_FILE = "srvrqs";

    String DELETE_FILE = "dl";
    String HELP = "help";
    String MAKE_DIR = "mkdir";
}
