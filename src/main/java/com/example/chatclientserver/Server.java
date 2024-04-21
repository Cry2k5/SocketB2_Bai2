package com.example.chatclientserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class Server {
    private static Set<PrintWriter> clients = new HashSet<>();

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(12345);
            System.out.println("Server started. Waiting for clients...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket);

                // Tạo một thread mới để xử lý kết nối đến từ client
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clientHandler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message) {
        for (PrintWriter client : clients) {
            client.println(message);
        }
    }

    static class ClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try {
                out = new PrintWriter(clientSocket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Yêu cầu client nhập username
                out.println("Please enter your username:");
                username = in.readLine();
                System.out.println("Client username: " + username);

                // Gửi thông báo đến tất cả client khác rằng một client mới đã tham gia
                broadcast("[" + username + "] has joined the chat.");

                // Lưu PrintWriter của client vào danh sách
                clients.add(out);

                String message;
                while ((message = in.readLine()) != null) {
                    // Gửi tin nhắn của client đến tất cả client khác
                    broadcast("[" + username + "]: " + message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Xóa PrintWriter của client khỏi danh sách và đóng kết nối
                clients.remove(out);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Gửi thông báo đến tất cả client khác rằng một client đã rời khỏi chat
                broadcast("[" + username + "] has left the chat.");
            }
        }
    }
}