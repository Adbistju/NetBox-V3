import FileManager.Commands;
import FileManager.FileManager;
import io.netty.channel.ChannelHandlerContext;

import java.util.List;

public class ServerControl {

    int corrector = 1;

    FileManager fileManager = new FileManager(".\\ServerRoot");
    private ChannelHandlerContext ctx;
    public void setCtx(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public String getCurrentDir() {
        return fileManager.getCurrentFolder();
    }

    public void command(String commandClient){
        String input = commandClient;
        String tokens[] = input.split("\\s");

        String command = tokens[0+corrector];

        switch (command) {
            case Commands.LIST_OF_FILES:
                List<String> listFalse = fileManager.listOfFilesList(false);
                for (int i = 0; i < listFalse.size(); i++) {
                    System.out.print(listFalse.get(i) + " ");
                    ProtoFileSender.sendListFile(listFalse.get(i), ctx.channel());
                }
                break;
            case Commands.LIST_OF_WITH_SIZE:{
                List<String> listTrue = fileManager.listOfFilesList(true);
                for (int i = 0; i < listTrue.size(); i++) {
                    System.out.print(listTrue.get(i) + " ");
                    ProtoFileSender.sendListFile(listTrue.get(i), ctx.channel());
                }
                break;
            }
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
                List<String> listContent = fileManager.fileContentList(folderName);
                for (int i = 0; i < listContent.size(); i++) {
                    ProtoFileSender.sendListFile(listContent.get(i), ctx.channel());
                }
                break;
            }
            case Commands.CHANGE_DIRECTORY:
                String folderName = buildierM(tokens);
                fileManager.changeDirectory(folderName);
                break;
            case Commands.DELETE_FILE:
                String sourceFileNameDelete = buildierM(tokens);
                fileManager.deleteFile(sourceFileNameDelete);
                break;
            case Commands.MAKE_DIR:{
                String sourceMakeDir = buildierM(tokens);
                System.out.println(sourceMakeDir);
                fileManager.createDir(sourceMakeDir);
                break;
            }
        }
    }

    public String buildierM(String[] credentialValues){
        String stopNameFileOneToNameFileTwo = "->";
        int corrector = 1;
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

    public void setFileAddresUser(String addres){
        fileManager.setRoot(addres);
    }
}
