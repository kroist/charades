package main.java.org;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

public class Server {

    //private static ArrayList<ConnectionThread> connections = new ArrayList<>();
    private static CopyOnWriteArrayList<Game> games = new CopyOnWriteArrayList<>();
    private static ConcurrentHashMap<Integer, Game> gameIDs = new ConcurrentHashMap<>();
    private static CopyOnWriteArrayList<Integer> freeIDs = new CopyOnWriteArrayList<>();
    private static ConnectionThread host;
    private static final Object lock = new Object();
    public static class ConnectionThread extends Thread {

        private final Socket socket;
        private final ObjectInputStream in;
        private final ObjectOutputStream out;
        private static String username;
        private boolean inGame;
        Player player;

        public ConnectionThread(Socket socket) throws IOException {
            this.socket = socket;
            this.in = new ObjectInputStream(this.socket.getInputStream());
            this.out = new ObjectOutputStream(this.socket.getOutputStream());
            inGame = false;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    if (socket.isClosed()) {
                        System.out.println("socket is closed");
                        break;
                    }
                    try {
                        Object recievedEvent = in.readObject();
                        System.out.println(recievedEvent);
                        if (!inGame){
                            if (!(recievedEvent instanceof String))break;
                            if (recievedEvent.equals("create new game")){
                                if (freeIDs.isEmpty())break;
                                int ID = freeIDs.get(0);
                                freeIDs.remove(ID);
                                Game game = new Game(ID);
                                games.add(game);
                                gameIDs.put(ID, game);
                                game.start();
                                System.out.println("new game started with ID: " + ID);

                                player = new Player(this, game);
                                game.addPlayer(player);
                                System.out.println("player " + username + " connected to game with ID: " + ID);
                                inGame = true;
                                synchronized (game) {
                                    game.notify();
                                }
                                out.writeObject(ID);
                            }else
                            if (recievedEvent.equals("connect to the existing game")){
                                try{
                                    Object nextEvent = in.readObject();
                                    if (!(nextEvent instanceof Integer))break;
                                    int ID = (int) nextEvent;
                                    if (gameIDs.containsKey(ID)){
                                        Game game = gameIDs.get(ID);
                                        player = new Player(this, game);
                                        game.addPlayer(player);
                                        System.out.println("player " + username + " connected to game with ID: " + ID);
                                        inGame = true;
                                        synchronized (game) {
                                            game.notify();
                                        }
                                    }else break;
                                } catch (IOException | ClassNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }else break;
                        }else {

                            if (recievedEvent != null) {
                                //System.out.println("We got object from: " + username);
                                System.out.println(player.isDrawing());
                                if (player.isDrawing())player.getGame().writeEvent(recievedEvent);
                            } else {
                                System.out.println("Disconnected + NullMessage");
                                break;
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Disconnected + IOException");
                        break;
                    }
                }
            }catch(Exception notignored){
                System.out.println("ingored " + notignored);
                notignored.printStackTrace();
            } finally{
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println(username + " disconnected");
            }
        }

        protected void sendObject(Object o) throws IOException {
            out.writeObject(o);
        }

        /*private void sendToAnyone(Object o) throws IOException {
            for (ConnectionThread conn : connections) {
                conn.sendObject(o);
            }
        }*/

    }

    public static void main(String[] args) throws IOException {
        /*
        if (args.length != 1) {
            System.err.println("java clientEcho <portNumber>");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
         */
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        int portNumber = 4000;
        try {
            System.out.println("Enter port number");
            portNumber = Integer.parseInt(stdIn.readLine());
        } catch (IOException e){
        }
        for (int i = 0; i < 100; ++i){
            freeIDs.add(i);
        }
        try (
                ServerSocket serverSocket = new ServerSocket(portNumber);
        ) {
            while (true) {
                Socket socket = serverSocket.accept();
                ConnectionThread service = new ConnectionThread(socket);
                service.start();
            }
        } catch (IOException e) {
            System.out.println("Exception caught while listening on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }


    }
}