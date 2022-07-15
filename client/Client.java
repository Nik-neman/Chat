package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.Connection;
import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.Message;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.net.Socket;

public class Client {
    protected Connection connection;
    private volatile boolean clientConnected = false;

    public static void main(String[] args){
        Client client = new Client();
        client.run();
    }

    protected String getServerAddress(){
        ConsoleHelper.writeMessage("Введите адрес сервера");
        String adress = null;
        try {
            adress = ConsoleHelper.readString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return adress;
    }

    protected int getServerPort(){
        ConsoleHelper.writeMessage("Введите порт");
        int port = 0;
        port = ConsoleHelper.readInt();
        return port;
    }

    protected String getUserName(){
        ConsoleHelper.writeMessage("Введите имя пользователя");
        String userName = null;
        try {
            userName = ConsoleHelper.readString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userName;
    }

    protected boolean shouldSendTextFromConsole(){
        return true;
    }

    protected SocketThread getSocketThread(){
        return new SocketThread();
    }

    protected void sendTextMessage(String text){
        try {
            connection.send(new Message(MessageType.TEXT, text));
        } catch (IOException e) {
            ConsoleHelper.writeMessage("Ошибка отправки сообщения");
            clientConnected = false;
            e.printStackTrace();
        }
    }


    public class SocketThread extends Thread {

        protected void processIncomingMessage(String message){
            ConsoleHelper.writeMessage(message);
        }

        protected void informAboutAddingNewUser(String userName){
            ConsoleHelper.writeMessage("Участник с именем " + userName + " присоединился к чату");
        }

        protected void informAboutDeletingNewUser(String userName){
            ConsoleHelper.writeMessage("Участник с именем " + userName + " покинул чат");
        }

        protected synchronized void notifyConnectionStatusChanged(boolean clientConnected){
            Client.this.clientConnected = clientConnected;
            synchronized (Client.this) {
                Client.this.notify();
            }
        }

        protected void clientHandshake() throws IOException, ClassNotFoundException{
            while (true) {
               Message message = connection.receive();
               if(message.getType() == MessageType.NAME_REQUEST){
                   connection.send(new Message(MessageType.USER_NAME, getUserName()));
               } else if(message.getType() == MessageType.NAME_ACCEPTED){
                   notifyConnectionStatusChanged(true);
                   break;
               } else {
                   throw new IOException("Unexpected MessageType");
               }

            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException{
            while (true) {
                Message message = connection.receive();
                if (message.getType() == MessageType.TEXT) {
                    processIncomingMessage(message.getData());
                } else if (message.getType() == MessageType.USER_ADDED) {
                    informAboutAddingNewUser(message.getData());
                } else if (message.getType() == MessageType.USER_REMOVED) {
                    informAboutDeletingNewUser(message.getData());
                } else {
                    throw new IOException("Unexpected MessageType");
                }
            }
        }

        public void run(){
            Socket socket;

            try {
                socket = new Socket(getServerAddress(), getServerPort());
                connection = new Connection(socket);
                clientHandshake();
                clientMainLoop();
            } catch (IOException | ClassNotFoundException e) {
                notifyConnectionStatusChanged(false);
                e.printStackTrace();
            }

        }
    }

    public void run() {
        SocketThread socketThread = getSocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
        synchronized (this) {
            try {
                this.wait();
            } catch (InterruptedException e) {
                ConsoleHelper.writeMessage("Ошибка подключения");
                e.printStackTrace();
                return;
            }
        }
        if(!clientConnected) {
            ConsoleHelper.writeMessage("Произошла ошибка во время работы клиента.");
        } else {
            ConsoleHelper.writeMessage("Соединение установлено. Для выхода наберите команду 'exit'.");
        }
        while (clientConnected){
            String text = null;
            try {
                text = ConsoleHelper.readString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (text.equals("exit")){
              return;
            } else if (shouldSendTextFromConsole()){
              sendTextMessage(text);
            }
        }

    }

}
