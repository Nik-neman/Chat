package com.javarush.task.task30.task3008;

import com.javarush.task.task30.task3008.client.Client;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.javarush.task.task30.task3008.ConsoleHelper.readInt;

public class Server {
    private static Map<String, Connection> connectionMap = new ConcurrentHashMap<>();

    public static void sendBroadcastMessage(Message message){
        for (Map.Entry<String, Connection> conect: connectionMap.entrySet()) {
            try {
                conect.getValue().send(message);
            } catch (IOException e) {
                e.printStackTrace();
                ConsoleHelper.writeMessage("Сообщение для " + conect.getKey() + " не отправлено!");
            }
        }
    }
    public static void main(String[] args) throws IOException {
        
           int port = readInt();
        ServerSocket  serverSocket = null;
        try {
           serverSocket = new ServerSocket(port);

        System.out.println("Сервер запущен");

            while (true) {
                     new Handler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.out.println(e);
            serverSocket.close();
        }


    }
    private static class Handler extends Thread{
        private Socket socket;
        public Handler (Socket socket){
            this.socket = socket;
        }

        public void run() {
            try {
                SocketAddress socketAddress = socket.getRemoteSocketAddress();
                ConsoleHelper.writeMessage("Установленно соединение с удалённым адресом: "+ socketAddress);
                Connection connection = new Connection(socket) ;
                String userName = serverHandshake(connection);
                sendBroadcastMessage(new Message(MessageType.USER_ADDED, userName));
                notifyUsers(connection, userName);
                serverMainLoop(connection, userName);
                connectionMap.remove(userName);
                sendBroadcastMessage(new Message(MessageType.USER_REMOVED , userName ));
                ConsoleHelper.writeMessage("Cоединение с удаленным адресом закрыто");
            } catch (IOException|ClassNotFoundException e) {
                e.printStackTrace();
                ConsoleHelper.writeMessage("Ошибка обмена данными");
            }finally {

                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        private String serverHandshake(Connection connection)throws IOException, ClassNotFoundException {
           Message message = null;
           String userName = null;
           try {
               do {
                   connection.send(new Message(MessageType.NAME_REQUEST));
                   message = connection.receive();
                   userName = message.getData();
               }while (message.getType() !=(MessageType.USER_NAME)|| userName.equals("") || connectionMap.containsKey(userName));
               connectionMap.put(userName, connection);
               connection.send(new Message(MessageType.NAME_ACCEPTED));
           }catch (ClassNotFoundException e){
               e.printStackTrace();
           }catch (IOException e){
            e.printStackTrace();
             }
            return userName;
        }

        private void notifyUsers(Connection connection, String userName) throws IOException{
            for (Map.Entry<String, Connection> map: connectionMap.entrySet()){
                if(!userName.equals(map.getKey()))

                connection.send(new Message(MessageType.USER_ADDED,  map.getKey()));
            }
        }
        private  void serverMainLoop(Connection connection, String userName) throws IOException, ClassNotFoundException{

            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    sendBroadcastMessage(new Message(MessageType.TEXT, userName + ": " + message.getData()));
                } else {
                    ConsoleHelper.writeMessage("Некорректный тип сообщения");
                }
            }


        }
    }
}
