package tcp;

import java.io.*;
import java.net.Socket;

public class Worker extends Thread {
    private Socket socket;
    private File logsFile;
    private RandomAccessFile counterRaf;

    public Worker(Socket socket, File logsFile, RandomAccessFile counterRaf) {
        this.socket = socket;
        this.logsFile = logsFile;
        this.counterRaf = counterRaf;
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void execute() throws IOException {
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String message;
            String response;

            response = reader.readLine();
            if (response.equals("log in")) {
                Integer currentClients = incrementRaf(counterRaf);
                System.out.println("SERVER: curr clients: " + currentClients);
                writer.write("logged in\n");
                writer.flush();
                System.out.println("SERVER: client logged in");

                while (true) {
                    response = reader.readLine();
                    writeLog(logsFile, response);
                    if (response.equals("log out")) {
                        writer.write("logging out\n");
                        writer.flush();
                        break;
                    }
                    writer.write("Server echo: " + response + "\n");
                    writer.flush();
                }
            } else {
                writer.write("Failed to log in\n");
                writer.flush();
                System.out.println("SERVER: client failed to log in");
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            writer.flush();
            writer.close();
            reader.close();
            socket.close();
        }
    }

    private synchronized void writeLog(File logsFile, String response) throws IOException {
        BufferedWriter fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logsFile,true)));
        fileWriter.append(response + "\n");
        fileWriter.flush();
        fileWriter.close();
    }

    private synchronized Integer incrementRaf(RandomAccessFile counterRaf) {
        Integer curr = 0;
        try {
            counterRaf.seek(0);
            curr = counterRaf.readInt();
            counterRaf.seek(0);
            counterRaf.writeInt(++curr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return curr;
    }
}
