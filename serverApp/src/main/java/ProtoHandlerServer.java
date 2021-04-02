//import DataBase.DataBaseList;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProtoHandlerServer extends ChannelInboundHandlerAdapter {
    public enum State {
        IDLE, NAME_LENGTH, NAME, FILE_LENGTH, FILE, MESSAGE_LENGTH, MESSAGE, SERVER_COMMAND_LENGTH, SERVER_COMMAND
    }

    private State currentState = State.IDLE;
    private int nextLength;
    private long fileLength;
    private long receivedFileLength;
    private BufferedOutputStream out;

    private StringBuilder stringBuilder = new StringBuilder();

    ServerControl svrc = new ServerControl();

    private static List<Channel> clients = new ArrayList<>();

    public ProtoHandlerServer() {
    }

    private boolean userIsAuth(ChannelHandlerContext ctx) {
        for (int i = 0; i < clients.size(); i++) {
            if (clients.get(i).equals(ctx.channel())){
                return true;
            }
        }
        return false;
    }

    private void toAuth(ChannelHandlerContext ctx, Object msg) {
        String str = null;
        ByteBuf buf = ((ByteBuf) msg);
        while (buf.readableBytes() > 0){
            byte readed = buf.readByte();
            if (readed == (byte) 26) {
                currentState = State.MESSAGE_LENGTH;
            }
            if (currentState == State.MESSAGE_LENGTH) {
                if (buf.readableBytes() >= 4) {
                    nextLength = buf.readInt();
                    currentState = State.MESSAGE;
                }
            }
            if(currentState == State.MESSAGE){
                if (buf.readableBytes() >= nextLength) {
                    System.out.println(nextLength);
                    byte [] message = new byte[nextLength];
                    for (int i = 0; i < message.length; i++) {
                        message[i] = buf.readByte();
                    }
                    //str = null;
                    try {
                        str = new String(message, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    currentState = State.IDLE;
                    System.out.println("------------------------------");
                    System.out.println(str);
                    System.out.println("------------------------------");
                    String[] credentialValues = str.split("\\s");
                    if(credentialValues[1].equals("reg+")){
                        DataBaseParam.doRegist(credentialValues[3], credentialValues[4], credentialValues[2]);
                        //DataBaseList.addUSer(credentialValues[2], credentialValues[3], credentialValues[4]);
                        svrc.fileManager.createDir(credentialValues[3], svrc.fileManager.getCurrentFolder());
                        svrc.setFileAddresUser(svrc.fileManager.getCurrentFolder() + credentialValues[3]);
                        clients.add(ctx.channel());
//Проблем с регистр была выше
                        for (int i = 0; i < clients.size(); i++) {
                            System.out.println(clients.get(i));
                        }
                        break;
                    }
//                    if (DataBaseList.searchName(credentialValues[1], credentialValues[2])){
//                        System.out.println("add-added");
//                        svrc.setFileAddresUser(".\\ServerRoot\\"+ DataBaseList.getName(credentialValues[1]));
//                        clients.add(ctx.channel());
//                        break;
//                    }
                    if (DataBaseParam.doAuth(credentialValues[1], credentialValues[2])){
                        System.out.println("add-added");
                        System.out.println(".\\ServerRoot\\"+ credentialValues[1]);
                        svrc.setFileAddresUser(".\\ServerRoot\\"+ credentialValues[1]);
                        clients.add(ctx.channel());
                        break;
                    }
                }
            }
        }
    }

    private String userName(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().toString());
        return "1";
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = ((ByteBuf) msg);
        if(!userIsAuth(ctx)){
            toAuth(ctx, msg);
            buf.release();
            return;
        }
        while (buf.readableBytes() > 0) {
            if (currentState == State.IDLE) {
                byte readed = buf.readByte();
                if (readed == (byte) 25) {
                    currentState = State.NAME_LENGTH;
                    receivedFileLength = 0L;
                    System.out.println("STATE: Start file receiving");
                } else if (readed == (byte) 24) {
                    System.out.println("REQUEST");
                    stringBuilder.delete(0, 200);
                    while (buf.isReadable()){
                        stringBuilder.append((char) buf.readByte());
                    }
                    String fileName = String.valueOf(stringBuilder);
                    System.out.println(fileName);
                    Path path = Paths.get(svrc.getCurrentDir()+"\\"+fileName);
                    ProtoFileSender.sendFile(path, ctx.channel(), future -> {
                        if (!future.isSuccess()) {
                            future.cause().printStackTrace();
                        }
                        if (future.isSuccess()) {
                            System.out.println("Файл успешно передан");
                        }
                    });
                    System.out.println("-------------------------------------------------");

                } else if (readed == (byte) 23) {
                    currentState = State.MESSAGE_LENGTH;
                } else if (readed == (byte) 22) {
                    currentState = State.SERVER_COMMAND_LENGTH;
                } else {
                    System.out.println("ERROR: Invalid first byte - " + readed);
                }
            }

            if (currentState == State.SERVER_COMMAND_LENGTH) {
                if (buf.readableBytes() >= 4) {
                    nextLength = buf.readInt();
                    currentState = State.SERVER_COMMAND;
                }
            }

            if(currentState == State.SERVER_COMMAND){
                if (buf.readableBytes() >= nextLength) {
                    byte [] message = new byte[nextLength];
                    for (int i = 0; i < message.length; i++) {
                        message[i] = buf.readByte();
                    }
                    String str = new String(message, "UTF-8");
                    svrc.setCtx(ctx);
                    svrc.command(str);
                    currentState = State.IDLE;
                }
            }

            if (currentState == State.MESSAGE_LENGTH) {
                if (buf.readableBytes() >= 4) {
                    nextLength = buf.readInt();
                    currentState = State.MESSAGE;
                }
            }

            if(currentState == State.MESSAGE){
                if (buf.readableBytes() >= nextLength) {
                    System.out.println(nextLength);
                    byte [] message = new byte[nextLength];
                    for (int i = 0; i < message.length; i++) {
                        message[i] = buf.readByte();
                    }
                    String str = new String(message, "UTF-8");
                    System.out.println(str);
                    currentState = State.IDLE;
                }
            }

            if (currentState == State.NAME_LENGTH) {
                if (buf.readableBytes() >= 4) {
                    System.out.println("STATE: Get filename length");
                    nextLength = buf.readInt();
                    currentState = State.NAME;
                }
            }

            if (currentState == State.NAME) {
                if (buf.readableBytes() >= nextLength) {
                    byte[] fileName = new byte[nextLength];
                    buf.readBytes(fileName);
                    System.out.println("STATE: Filename received - _" + new String(fileName, "UTF-8"));
                    out = new BufferedOutputStream(new FileOutputStream(svrc.getCurrentDir()+"\\"+"_" + new String(fileName))); //FILEMANAGER getCurrentFolder
                    currentState = State.FILE_LENGTH;
                }
            }

            if (currentState == State.FILE_LENGTH) {
                if (buf.readableBytes() >= 8) {
                    fileLength = buf.readLong();
                    System.out.println("STATE: File length received - " + fileLength);
                    currentState = State.FILE;
                }
            }

            if (currentState == State.FILE) {
                while (buf.readableBytes() > 0) {
                    out.write(buf.readByte());
                    receivedFileLength++;
                    if (fileLength == receivedFileLength) {
                        currentState = State.IDLE;
                        System.out.println("File received");
                        out.close();
                        break;
                    }
                }
            }
        }
        if (buf.readableBytes() == 0) {
            buf.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
