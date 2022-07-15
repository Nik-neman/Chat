package com.javarush.task.task30.task3008.client;

import com.javarush.task.task30.task3008.ConsoleHelper;
import com.javarush.task.task30.task3008.MessageType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class BotClient extends Client {

    public class BotSocketThread extends SocketThread {

        protected void clientMainLoop() throws IOException, ClassNotFoundException{
            sendTextMessage( "Привет чатику. Я бот. Понимаю команды: дата, день, месяц, год, время, час, минуты, секунды."
            );
            super.clientMainLoop();
        }

        @Override
        protected void processIncomingMessage(String message) {
            ConsoleHelper.writeMessage(message);
            String[] messages = message.split(": ");
            if(messages.length == 2) {
                String nameSender = messages[0];
                String textMessage = messages[1];
                String answer = "Информация для " + nameSender + ": ";

                switch (textMessage) {
                    case ("дата"):
                        sendTextMessage(answer + new SimpleDateFormat("d.MM.YYYY").format(Calendar.getInstance().getTime()));
                        break;
                    case ("день"):
                        sendTextMessage(answer + new SimpleDateFormat("d").format(Calendar.getInstance().getTime()));
                        break;
                    case ("месяц"):
                        sendTextMessage(answer + new SimpleDateFormat("MMMM").format(Calendar.getInstance().getTime()));
                        break;
                    case ("год"):
                        sendTextMessage(answer + new SimpleDateFormat("YYYY").format(Calendar.getInstance().getTime()));
                        break;
                    case ("время"):
                        sendTextMessage(answer + new SimpleDateFormat("H:mm:ss").format(Calendar.getInstance().getTime()));
                        break;
                    case ("час"):
                        sendTextMessage(answer + new SimpleDateFormat("H").format(Calendar.getInstance().getTime()));
                        break;
                    case ("минуты"):
                        sendTextMessage(answer + new SimpleDateFormat("m").format(Calendar.getInstance().getTime()));
                        break;
                    case ("секунды"):
                        sendTextMessage(answer + new SimpleDateFormat("s").format(Calendar.getInstance().getTime()));
                        break;
                }
            }

        }

    }

    protected SocketThread getSocketThread(){
        return new BotSocketThread();
    }

    protected boolean shouldSendTextFromConsole() {
        return false;
    }

    protected String getUserName(){
        return "date_bot_" + (int)(Math.random()*100);
    }

    public static void main(String[] args){

        BotClient botClient = new BotClient();
        botClient.run();
    }
}
