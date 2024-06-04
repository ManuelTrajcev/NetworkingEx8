package tcp;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Client extends Thread {
    private int serverPort;
    private List<String> messages;
    private static Random random = new Random();

    public Client(int serverPort) {
        this.serverPort = serverPort;
        messages = new ArrayList<>();
        messages.add("log in");
        messages.add("log out");
        messages.add("hello");
        messages.add("how are you");
        messages.add("good morning");
    }

    @Override
    public void run() {
        try {
            execute();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void execute() throws UnknownHostException, InterruptedException {
        Socket socket = null;
        BufferedWriter writer = null;
        BufferedReader reader = null;

        try {
            socket = new Socket(InetAddress.getLocalHost(), serverPort);
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String message;
            String response;

            message = messages.get(random.nextInt(messages.size()));
            writer.write(message + "\n");
            writer.flush();
            response = reader.readLine();
            if (response.equals("logged in")) {
                System.out.println("CLIENT: logged in");
                while (true) {
                    message = messages.get(random.nextInt(messages.size()));
                    writer.write(message + "\n");
                    writer.flush();

                    response = reader.readLine();
                    if (response.equals("logging out")){
                        System.out.println("CLIENT: logging out");
                        break;
                    }
                    System.out.println(response);
                    Thread.sleep(100);
                }
            }else {
                System.out.println("CLIENT: failed to log in");
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (writer != null) {
                try {
                    writer.flush();
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        List<Client> clients = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            clients.add(new Client(7070));
        }
        clients.forEach(Thread::start);
        clients.forEach(client -> {
            try {
                client.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
