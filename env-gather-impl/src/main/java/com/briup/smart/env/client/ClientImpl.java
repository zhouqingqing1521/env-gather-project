package com.briup.smart.env.client;

import com.briup.smart.env.Configuration;
import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.support.ConfigurationAware;
import com.briup.smart.env.support.PropertiesAware;
import com.briup.smart.env.util.Log;

import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.Properties;

public class ClientImpl implements Client, ConfigurationAware, PropertiesAware {
    String host;
    int port;
    Log log;
    @Override
    public void send(Collection<Environment> c) throws Exception {
//        1、准备一个socket
        log.info("准备连接"+host+":"+port);
        Socket socket = new Socket(host,port);
        OutputStream os = socket.getOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os);
        log.info("准备发送数据");
        oos.writeObject(c);
        log.info("已发送数据");
        oos.flush();
        socket.close();
        log.info("客户端任务完成 已关闭");
    }

    @Override
    public void setConfiguration(Configuration configuration) throws Exception {
        this.log = configuration.getLogger();
    }

    @Override
    public void init(Properties properties) throws Exception {
        this.port = Integer.valueOf(properties.getProperty("port"));
        this.host = properties.getProperty("host");
    }
}
