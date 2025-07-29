package client;

import com.google.gson.Gson;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson;

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
        this.gson = new Gson();
    }
}
