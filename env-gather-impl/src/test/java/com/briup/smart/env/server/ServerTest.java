package com.briup.smart.env.server;

import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

public class ServerTest {
    Server server = new ServerImpl();
    @Test
    public void testServer() throws Exception {
        server.reciver();
    }

    @Test
    public void testClose() throws IOException {
        Socket socket = new Socket("127.0.0.1",9999);
    }
}
