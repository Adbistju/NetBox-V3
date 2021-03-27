import FileManager.Commands;
import FileManager.FileManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

public class ClientControl{

    FileManager fileManager;

    public ClientControl(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    public void start(){
        System.out.println("Добро пожаловать! Авторизируйтесь auth почта пароль. " +
                "\nВведите команду:");
        Scanner scanner = new Scanner(System.in);



        String input = scanner.nextLine();

        while (!input.equals(Commands.EXIT)) {
            String tokens[] = input.split("\\s");
            String command = tokens[0];
            switch (command) {
                case Commands.LIST_OF_FILES:
                    fileManager.listOfFiles(false);
                    break;
                case Commands.LIST_OF_WITH_SIZE:
                    fileManager.listOfFiles(true);
                    break;
                case Commands.COPY_FILE:
                    String sourceFileName = buildierM(tokens);
                    String destFileName = buildierTwoNameFile(tokens);
                    if(destFileName.equals("")){
                        fileManager.copyFile(sourceFileName);
                    } else {
                        fileManager.copyFile(sourceFileName, destFileName);
                    }
                    break;
                case Commands.CREATE_FILE: {
                    String folderName = buildierM(tokens);
                    fileManager.createFile(folderName);
                    break;
                }
                case Commands.FILE_CONTENT:{
                    String folderName = buildierM(tokens);
                    fileManager.fileContent(folderName);
                    break;
                }
                case Commands.CHANGE_DIRECTORY:
                    String folderName = buildierM(tokens);
                    fileManager.changeDirectory(folderName);
                    break;
                case Commands.MESSAGE:
                    ProtoFileSender.sendMessage(input,Network.getInstance().getCurrentChannel());
                    break;
                case Commands.AUTH:
                    ProtoFileSender.sendAuth(input,Network.getInstance().getCurrentChannel());
                    break;

                case Commands.SERVER:
                    ProtoFileSender.sendCommand(input,Network.getInstance().getCurrentChannel());
                    break;
                case Commands.SERVER_DOWNLOAD: {
                    System.out.println(input);
                    String sourceFileName1 = buildierM(tokens);
                    System.out.println(sourceFileName1);
                    ProtoFileSender.sendReQuestFile(sourceFileName1,Network.getInstance().getCurrentChannel());
                    break;
                }
                case Commands.SERVER_REQUEST_FILE:
                    Path path = Paths.get(tokens[1]);
                    try {
                        ProtoFileSender.sendFile(path,Network.getInstance().getCurrentChannel());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                case Commands.DELETE_FILE: {
                    String fileDelete = buildierM(tokens);
                    fileManager.deleteFile(fileDelete);
                    System.out.println("delete  : " + fileDelete);
                    break;
                }
                case Commands.HELP:
                    System.out.println("ls     - список файлов");
                    System.out.println("ll     - список файлов с их размером");
                    System.out.println("cp     - копировать файл, написав после имени файла -> можно указать свое имя файла");
                    System.out.println("ls     - создать файл");
                    System.out.println("cat    - показать содержимое файла");
                    System.out.println("msg    - отправить текствое сообщение");
                    System.out.println("mkdir  - создать папку");
                    System.out.println("dl     - удалить 'имя файла'");
                    System.out.println("srv    - писать перед командами, которые нужно выполнить на сервере");
                    System.out.println("srvdwn - запросить файл с сервера");
                    System.out.println("srvrgs - отправить файл на сервер");
                    break;
                case Commands.MAKE_DIR:{
                    String sourceDirName = buildierM(tokens);
                    fileManager.createDir(sourceDirName);
                }
            }
            input = scanner.nextLine();
        }
    }

    public String buildierM(String[] credentialValues){
        String stopNameFileOneToNameFileTwo = "->";
        int corrector = 0;//TUT
        int index = 0;
        int lengthStr = 0;
        for (int j = 0; j < credentialValues.length-(1+corrector); j++) {
            if(credentialValues[j+(1+corrector)].equals(stopNameFileOneToNameFileTwo)){
                index = j;
                break;
            }
        }
        if(index ==0){
            lengthStr = credentialValues.length-(1+corrector);
        } else {
            lengthStr = (credentialValues.length - (credentialValues.length - index));
        }
        String[] add = new String[lengthStr];
        for (int i = 0; i < add.length; i++) {
            add[i]=credentialValues[i+(1+corrector)];
        }
        return stringConstruction(add);
    }

    protected String stringConstruction(String[] strings){
        String str = null;
        for (int i = 0; i < strings.length; i++) {
            if (i==0){
                str=strings[i];
            }else {
                str = str +" "+strings[i];
            }
        }
        return str;
    }

    public String buildierTwoNameFile(String[] credentialValues){
        String stopNameFileOneToNameFileTwo = "->";
        int corrector = 1;
        int index = 0;
        boolean serach = false;
        for (int i = 0; i < credentialValues.length; i++) {
            if(credentialValues[i].equals(stopNameFileOneToNameFileTwo)){
                System.out.println(credentialValues[i]);
                index = i;
                serach = true;
                break;
            }
        }
        if(serach == false){
            return "";
        }
        String add[] = new String[credentialValues.length-index-corrector];

        for (int i = 0; i < add.length; i++) {
            add[i] = credentialValues[i+index+corrector];
        }

        return stringConstruction(add);
    }

}
