package com.briup.smart.env.server;

import com.briup.smart.env.Configuration;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Log;
import com.briup.smart.env.util.LogImpl;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

public class ServerImpl implements Server, ConfigurationAware, PropertiesAware {
    ServerSocket serverSocket = null;
    DBStore dbStore;
    Log log;
    int serverPort,stopPort;
    @Override
    public void reciver() throws Exception {
//        while循环去监听客户端 处理一个 继续监听
//        1、开启一个服务 ServerSocket
        log.info("等待客户端的监听,服务端的端口号是"+serverPort);
        serverSocket = new ServerSocket(serverPort);
        toShutDown();
//        2、监听客户端的连接accept
        while (true){
            Socket socket = serverSocket.accept();
            log.info("监听到了客户端的接入");
            InputStream is = socket.getInputStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            Collection<Environment> c = (Collection<Environment>) ois.readObject();
            dbStore.saveDB(c);
            //4、打印对象的长度
        }
    }

    public void toShutDown(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    log.info("等待断开服务监听，端口号是"+stopPort);
                    ServerSocket closeSocket = new ServerSocket(stopPort);
                    closeSocket.accept();
//                    监听9999客户端的连接
                    shutdown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void shutdown() throws Exception {
        serverSocket.close();
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        this.dbStore = configuration.getDbStore();
        this.log = configuration.getLogger();
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.serverPort = Integer.valueOf(properties.getProperty("server-port"));
        this.stopPort = Integer.valueOf(properties.getProperty("stop-port"));
    }
}
