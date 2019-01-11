package com.gs.utils;

import com.gs.bean.ContactInfo;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class FileUtils {
    public static List<String> readFromFile(String path) throws IOException {
        File file = new File(path);
        List<String> list = new ArrayList<String>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
        String str;
        while((str = reader.readLine())!=null){
            if(!StringUtils.isEmpty(str)){
                list.add(str);
            }
        }
        return list;
    }

    public static List<ContactInfo> getContacts(List<String> list) throws ParseException {
        List<ContactInfo> contactInfos = new ArrayList<ContactInfo>();
        for(int i=0;i<list.size()/5;i++){
            SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String date = list.get(i * 5 + 0).substring(5);
            if(DateUtiles.longOfTwoDate(dateFormat.parse(date),new Date())==0){
                String name = list.get(i * 5 + 1).split(":")[1];
                String subject = list.get(i * 5 + 2).split(":")[1];
                String email = list.get(i * 5 + 3).split(":")[1];
                String content = list.get(i * 5 + 4).split(":")[1];
                contactInfos.add(new ContactInfo(date,name,subject,email,content));
            }
        }
        return contactInfos;
    }

    public static List<String> getRevisitors(String path) throws IOException {
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyy-MM-dd");
        String now  = dateFormat.format(new Date())+".txt";
        File file = new File(path);
        if(file.exists()){
            for(File f : file.listFiles()){
                if(f.getName().equals(now)){
                    return readFromFile(f.getAbsolutePath());
                }
            }
        }
        return Collections.emptyList();
    }
}
