package com.briup.smart.env.client;

import com.briup.smart.env.entity.Environment;
import com.briup.smart.env.server.Server;
import com.briup.smart.env.server.ServerImpl;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;

public class GatherTest {
    @Test
    public void t(){
        Gather gather = new GatherImpl();
        Client client = new ClientImpl();

        try {
            Collection<Environment> envLists = gather.gather();
            client.send(envLists);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test16(){
        int i = Integer.parseInt("57a4", 16);
        System.out.println(i);
    }

    @Test
    public void getDay(){
        Timestamp timestamp = new Timestamp(1516323596029L);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(timestamp);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        System.out.println("通过时间戳得到的天是：" + day);
    }
}
