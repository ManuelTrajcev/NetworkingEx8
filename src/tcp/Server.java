package tcp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Thread {
    private int port;
    private String filePathRaf;
    private String filePathLogs;
    private static File counterFile;
    private static File logs;
    private static RandomAccessFile counterRaf;

    public Server(int port, String filePathRaf, String filePathLogs) {
        this.port = port;
        this.filePathRaf = filePathRaf;
        counterFile = new File(filePathRaf);
        logs = new File(filePathLogs);
        try {
            counterRaf = new RandomAccessFile(counterFile, "rw");
            counterRaf.writeInt(0);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        ServerSocket serverSocket = null;
        Socket socket = null;

        try {
            System.out.println("SERVER: staring...");
            serverSocket = new ServerSocket(port);
            while (true) {
                socket = serverSocket.accept();
                System.out.println("SERVER: Client accepted");
                Worker worker = new Worker(socket, logs, counterRaf);
                worker.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server(7070, "./src/tcp/CounterFile.RAF", "./src/tcp/LogsFile.csv");
        server.start();
    }
}
