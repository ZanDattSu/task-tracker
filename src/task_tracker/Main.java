package task_tracker;

import task_tracker.server.HttpTaskServer;
import task_tracker.server.KVServer;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        new KVServer().start();
        HttpTaskServer server = new HttpTaskServer();
        server.start();
    }
}

