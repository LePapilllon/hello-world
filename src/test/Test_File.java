package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Test_File {
    public static void  readArray(ArrayList<List<Integer>> data,String filename) {
        //声明一个字符输入流
        FileReader reader = null;
        //声明一个字符输入缓冲流
        BufferedReader readerBuf = null;

        try {
            //指定reader的读取路径
            reader = new FileReader(filename);
            //通过BufferedReader包装字符输入流
            readerBuf = new BufferedReader(reader);
            //创建一个集合，用来存放读取的文件的数据
            List<String> strList = new ArrayList<>();
            //用来存放一行的数据
            String lineStr;
            //逐行读取csv文件中的内容
            while((lineStr = readerBuf.readLine()) != null) {
                //9.把读取的行添加到list中
                strList.add(lineStr);
            }
            //获取文件有多少行
            int lineNum = strList.size();
            //获取数组有多少列
            String s =  strList.get(0);
            int columnNum = s.split("\\,").length;
            //记录输出当前行
            int count = 0;
            //循环遍历集合，将集合中的数据放入ArrayList中
            for(String str : strList) {
                List temp= new ArrayList();
                //15.将读取的str按照","分割，用字符串数组来接收
                String[] strs = str.split("\\,");
                for(int i = 0; i < columnNum; i++) {
                    //array[count][i] = Integer.valueOf(strs[i]);
                    temp.add(Integer.valueOf(strs[i]));
                }
                data.add(temp);
                //16.将行数 + 1
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭字符输入流
            try {
                if(reader != null)
                    reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //关闭字符输入缓冲流
            try {
                if(readerBuf != null)
                    readerBuf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void main(String []args) {
        ArrayList<List<Integer>> data = new ArrayList<List<Integer>>();
        readArray(data,"D:\\test.csv");
        System.out.println(data.get(0).get(0));
        System.out.println("输入数据为");
        for(int x=0;x<data.size();x++){
            for(int y=0;y<data.get(0).size();y++) {
                System.out.printf("%5d ",data.get(x).get(y));
                if(y==data.get(0).size()-1)
                    System.out.print("\n");
            }
        }
        //"D:\\test.txt"
    }


}
