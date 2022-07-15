package com.javarush.task.task30.task3008.client;

public class ClientGuiController extends Client {
    private ClientGuiModel model = new ClientGuiModel();
    private ClientGuiView view = new ClientGuiView(this);

    public ClientGuiController() {
        super();

    }

    public class GuiSocketThread extends SocketThread{

        protected void processIncomingMessage(String message){
            model.setNewMessage(message);
            view.refreshMessages();
        }

        protected void informAboutAddingNewUser(String userName){
            model.addUser(userName);
            view.refreshUsers();
        }

        protected void informAboutDeletingNewUser(String userName){
            model.deleteUser(userName);
            view.refreshUsers();
        }

        protected void notifyConnectionStatusChanged(boolean clientConnected){
            view.notifyConnectionStatusChanged(clientConnected);
        }

    }
    protected SocketThread getSocketThread(){
        return new GuiSocketThread();
    }

    public void run(){
       this.getSocketThread().run();
    }

    public String getServerAddress(){
        return view.getServerAddress();
    }

    public int getServerPort(){
        return view.getServerPort();
    }

    public String getUserName(){
        return view.getUserName();
    }

    public ClientGuiModel getModel(){
        return model;
    }

    public static void main(String[] args){
        new ClientGuiController().run();
    }
}
