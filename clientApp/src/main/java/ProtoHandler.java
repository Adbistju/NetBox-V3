import FileManager.FileManager;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ProtoHandler extends ChannelInboundHandlerAdapter {
    public enum State {
        IDLE, NAME_LENGTH, NAME, FILE_LENGTH, FILE, FILE_LIST_SIZE, FILE_LIST_ELEMENT
    }

    private State currentState = State.IDLE;
    private int nextLength;
    private long fileLength;
    private long receivedFileLength;
    private BufferedOutputStream out;
    private FileManager fileManager;

    private StringBuilder stringBuilder = new StringBuilder();

    public ProtoHandler(FileManager fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = ((ByteBuf) msg);
        while (buf.readableBytes() > 0) {
            if (currentState == State.IDLE) {
                byte readed = buf.readByte();
                if (readed == (byte) 25) {
                    currentState = State.NAME_LENGTH;
                    receivedFileLength = 0L;
                    System.out.println("STATE: Start file receiving");
                } else if (readed == (byte) 24) {
                    System.out.println("REQUEST");
                    while (buf.isReadable()){
                        stringBuilder.append((char) buf.readByte());
                    }
                    String fileName = String.valueOf(stringBuilder);
                    System.out.println(fileName);
                    Path path = Paths.get(fileName);
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
                    while (buf.isReadable()){
                        System.out.print((char) buf.readByte());
                    }
                } else if (readed == (byte) 22) {
                    while (buf.isReadable()){
                        stringBuilder.append((char) buf.readByte());
                    }
                } else if (readed == (byte) 33) {
                    currentState = State.FILE_LIST_SIZE;
                } else {
                    System.out.println("ERROR: Invalid first byte - " + readed);
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
                    System.out.println("STATE: Filename received - " + new String(fileName, "UTF-8"));
                    String currentFolderBuff = fileManager.getCurrentFolder();
                    out = new BufferedOutputStream(new FileOutputStream(currentFolderBuff+ "\\" + new String(fileName)));
                    currentState = State.FILE_LENGTH;
                }
            }

            if (currentState == State.FILE_LIST_SIZE) {
                if (buf.readableBytes() >= 4) {
                    nextLength = buf.readInt();
                    currentState = State.FILE_LIST_ELEMENT;
                }
            }

            if(currentState == State.FILE_LIST_ELEMENT){
                if (buf.readableBytes() >= nextLength) {
                    byte [] message = new byte[nextLength];
                    for (int i = 0; i < message.length; i++) {
                        message[i] = buf.readByte();
                    }
                    String str = new String(message, "UTF-8");
                    currentState = State.IDLE;
                    System.out.println(str);
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
