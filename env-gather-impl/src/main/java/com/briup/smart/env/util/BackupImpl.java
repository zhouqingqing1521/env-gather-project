package com.briup.smart.env.util;

import java.io.*;

public class BackupImpl implements Backup{

    @Override
    public Object load(String fileName, boolean del) throws Exception {
        File file = new File(fileName);
        //判断文件是否存在
        if(!file.exists())
            return null;
        ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(file));
        Object o = ois.readObject();
        //判断是否需要删除文件
        if(del){
            file.delete();
        }
        return o;
    }

    //fileName = src/main/resources/backup
    //obj = 长度（21999）
    //append = false
    @Override
    public void store(String fileName, Object obj, boolean append) throws Exception {
        File file = new File(fileName);
        //获取对应的文件 并且输出
        ObjectOutputStream oos = new
            ObjectOutputStream(new FileOutputStream(file,append));
        oos.writeObject(obj);
        oos.flush();
        oos.close();
    }
}
