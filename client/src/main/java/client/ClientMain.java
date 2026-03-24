package client;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8080;
        ServerFacade facade = new ServerFacade(port);
        PreloginUI prelogin = new PreloginUI(facade);
        prelogin.start();
    }
}