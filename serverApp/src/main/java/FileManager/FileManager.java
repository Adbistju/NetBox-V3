package FileManager;

import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileManager {

    private String currentFolder = "ServerRoot\\";
    private String root;

    public FileManager(String currentFolder){
        this.currentFolder = currentFolder;
        this.root = currentFolder;
    }

    public void setRoot(String root) {
        this.currentFolder = root;
        this.root = root;
    }

    public String getCurrentFolder() {
        return currentFolder;
    }

    public void listOfFiles(boolean withSize){
        File currentFolderAsFile = new File(currentFolder);

        File files[] = currentFolderAsFile.listFiles();

        for (File file : files){
            if(file.isDirectory()){
                if(withSize){
                    System.out.print(file.getName() + "\\ " + FileUtils.sizeOfDirectory(file));
                } else {
                    System.out.print(file.getName() + "\\ ");
                }
                System.out.println();
            } else {
                if(withSize){
                    System.out.print(file.getName() + " " + file.length());
                } else {
                    System.out.print(file.getName() + " ");
                }
                System.out.println();
            }
        }
    }

    public List listOfFilesList(boolean withSize){
        File currentFolderAsFile = new File(currentFolder);
        List<String> listFile = new ArrayList<>();
        File files[] = currentFolderAsFile.listFiles();

        for (File file : files){
            if(file.isDirectory()){
                if(withSize){
                    listFile.add(file.getName() + "\\ " + FileUtils.sizeOfDirectory(file));
                } else {
                    listFile.add(file.getName() + "\\ ");
                }
                System.out.println();
            } else {
                if(withSize){
                    listFile.add(file.getName() + " " + file.length());
                } else {
                    listFile.add(file.getName() + " ");
                }
            }
        }
        return listFile;
    }

    public void copyFile(String sourceFileName, String destFileName) {
        File source = new File(currentFolder + "\\" + sourceFileName);
        File dest = new File(currentFolder + "\\" + destFileName);
        try {
            FileUtils.copyFile(source, dest);
        } catch (IOException e) {
            System.err.println("Произошла ошибка!");
        }
    }

    public void copyFile(String sourceFileName) {
        File source = new File(currentFolder + "\\" + sourceFileName);
        String [] arr = sourceFileName.split("\\.");
        File dest = new File(currentFolder + "\\"+arr[arr.length-2] + " - copy"+"."+arr[arr.length-1]);
        try {
            FileUtils.copyFile(source, dest);
        } catch (IOException e) {
            System.err.println("Произошла ошибка!");
        }
    }

    public void deleteFile(String sourceFileName) {
        File source = new File(currentFolder + "\\" + sourceFileName);
        FileUtils.deleteQuietly(source);
    }

    public void createFile(String fileName) {
        File file = new File(currentFolder + "\\" + fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            System.err.println("Произошла ошибка!");
        }
    }

    public void fileContent(String fileName) {
        File file = new File(currentFolder + "\\" + fileName);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
        } catch (IOException e){
            System.err.println("Произошла ошибка!");
        }
    }

    public List fileContentList(String fileName) {
        File file = new File(currentFolder + "\\" + fileName);
        List<String> list = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = reader.readLine();
            while (line != null) {
                list.add(line);
                System.out.println(line);
                line = reader.readLine();
            }
        } catch (IOException e){
            System.err.println("Произошла ошибка!");
        }
        return list;
    }

    public void changeDirectory(String folderName) {
        if(folderName.equals("/")){
            this.currentFolder = this.root;
        } else if (folderName.equals("..")){
            int startLastFolderPosition = this.currentFolder.lastIndexOf("\\");
            this.currentFolder = this.currentFolder.substring(0, startLastFolderPosition);
        } else {
            this.currentFolder = this.currentFolder + "\\" + folderName;
        }
    }

    public void createDir(String fileName, String fullPath) {
        new File(fullPath+"\\"+fileName).mkdir();
    }

    public void createDir(String fileName) {
        new File(currentFolder+"\\"+fileName).mkdir();
    }

    public static void createDirStatic(String fileName) {
        new File("ServerRoot"+"\\"+fileName).mkdir();
    }
}