import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class water_sql {
    public static void main(String[] args)throws SQLException, ClassNotFoundException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");//加载驱动
            System.out.println("数据库引擎加载成功");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        //try {
            String url = "jdbc:mysql://localhost:3306/sys?useUnicode=true&characterEncoding=utf8&useSSL=true";//url包含数据库名称
            String username = "root";
            String password = "111111";//用户信息与url

            Connection con_mysql=DriverManager.getConnection(url, username, password);//连接数据库对象connection
            System.out.println("数据库连接成功");
        /*}catch(SQLException e){
            e.printStackTrace();
        }*/

        /*Statement stat = con_mysql.createStatement();//创建statement接口
        System.out.println("Statement对象连接成功");
        *//*stat.executeUpdate("create database if not exists grid");
        System.out.println("数据库grid创建成功");*//*
        stat.close();
        con_mysql.close();*/

        //建立与数据库grid的连接
        String url1 = "jdbc:mysql://localhost:3306/grid?useUnicode=true&characterEncoding=utf8&useSSL=true";
        Connection con=DriverManager.getConnection(url1, username, password);
        System.out.println("连接数据库grid成功");
        Statement stat=con.createStatement();//创建statement接口
        System.out.println("statement对象连接成功");

        //创建DEM表
        stat.executeUpdate("create table dem_para(ncols int(4),nrows int(4),xllcorner double,yllcorner double,cellsize int,NODATA int(4))");
        System.out.println("数据表dem_para创建成功");
        /*stat.executeUpdate("create table dem(drow int(4),dcol int(4),value int(4))");
        System.out.println("数据库dem创建成功");*/
        //stat.executeUpdate("drop table dem");

        //插入dem_para表数据和dem表数据
        int ncols;//列数
        int nrows;//行数
        int cellsize;//网格大小
        int NoData;//无效值
        double xllcorner;//左下角X坐标
        double yllcorner;//左下角Y坐标

        String temp;//临时存储字符串
        String filename="src\\dem.asc";
        File DEMfile=new File(filename);
        try{
            Scanner inDEM=new Scanner(DEMfile,"gbk");
            //利用光标移动录入各头文件参数
            temp=inDEM.next();//过行
            ncols=inDEM.nextInt();
            temp=inDEM.next();
            nrows=inDEM.nextInt();
            temp=inDEM.next();
            xllcorner=inDEM.nextDouble();
            temp=inDEM.next();
            yllcorner=inDEM.nextDouble();
            temp=inDEM.next();
            cellsize=inDEM.nextInt();
            temp=inDEM.next();
            NoData=inDEM.nextInt();
            stat.executeUpdate("insert into dem_para(ncols,nrows,xllcorner,yllcorner,cellsize,NODATA) values('"+ncols+"','"+nrows+"','"+xllcorner+"','"+yllcorner+"','"+cellsize+"','"+NoData+"')");
            System.out.println("dem_para数据插入成功");
            for(int i=0;i<nrows;i++){
                for(int j=0;j<ncols;j++){
                    //datatemp.add(inDEM.nextInt());//逐行录入数据
                    int value=inDEM.nextInt();
                    stat.executeUpdate("insert into dem(drow,dcol,value)values('"+i+"','"+j+"','"+value+"')");
                }
                //DEM.add(datatemp);
            }
            System.out.println("dem数据插入成功");
            inDEM.close();
        }catch(FileNotFoundException e){
            System.out.println(e);
        }


        //创建rain表
        /*stat.executeUpdate("create table rain(id int,flow double,huanglongdai int,lianxing int,fengmulang int)");
        System.out.println("数据表rain创建成功");*/

        //插入rain表数据
        /*String filename1="src\\rain.txt";
        //声明一个字符输入流
        //FileReader reader = null;
        //声明一个字符输入缓冲流
        BufferedReader readerBuf = null;
        try {
            //指定reader的读取路径
            //reader = new FileReader(filename1);
            //通过BufferedReader包装字符输入流
            readerBuf = new BufferedReader(new InputStreamReader(new FileInputStream(filename1), Charset.forName("GBK")));
            //创建一个集合，用来存放读取的文件的数据
            List<String> strList = new ArrayList<>();
            //用来存放一行的数据
            String lineStr;
            //逐行读取txt文件中的内容
            //读取站点数量
            lineStr=readerBuf.readLine();
            lineStr=readerBuf.readLine();//过行
            while((lineStr = readerBuf.readLine()) != null) {//第三行开始读
                //把读取的行添加到list中
                strList.add(lineStr);
            }
            //获取文件有多少行
            int lineNum = strList.size();
            //获取数组有多少列
            String s =  strList.get(0);
            int columnNum = s.split("\t").length;
            //循环遍历集合，将集合中的数据放入ArrayList中
            for(String str : strList) {
                //List temp= new ArrayList();
                //将读取的str按照"\t"分割，用字符串数组来接收
                String[] strs = str.split("\t");
                int id=Integer.valueOf(strs[0]);
                System.out.println(id);
                double flow=Double.valueOf(strs[1]);
                int hld=Integer.valueOf(strs[2]);
                int lx=Integer.valueOf(strs[3]);
                int fml=Integer.valueOf(strs[4]);
                stat.executeUpdate("insert into rain(id,flow,huanglongdai,lianxing,fengmulang) values('"+id+"','"+flow+"','"+hld+"','"+lx+"','"+fml+"')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭字符输入缓冲流
            try {
                if(readerBuf != null)
                    readerBuf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //创建station表
        stat.close();
        con.close();
        System.out.println("降水数据插入成功");*/

        /*//创建stationproperty表
        stat.executeUpdate("create table StationProperty(id int,name varchar(10),sid varchar(3),x double,y double,enname varchar(20))");
        System.out.println("数据表StationProperty创建成功");*/

        //插入Stationproperty表数据
        /*String filename1="src\\StationProperty.txt";
        //声明一个字符输入流
        //FileReader reader = null;
        //声明一个字符输入缓冲流
        BufferedReader readerBuf = null;
        try {
            //指定reader的读取路径
            //reader = new FileReader(filename1);
            //通过BufferedReader包装字符输入流
            readerBuf = new BufferedReader(new InputStreamReader(new FileInputStream(filename1), Charset.forName("GBK")));
            //创建一个集合，用来存放读取的文件的数据
            List<String> strList = new ArrayList<>();
            //用来存放一行的数据
            String lineStr;
            //逐行读取txt文件中的内容
            //读取站点数量
            lineStr=readerBuf.readLine();
            lineStr=readerBuf.readLine();//过行
            while((lineStr = readerBuf.readLine()) != null) {//第三行开始读
                //把读取的行添加到list中
                strList.add(lineStr);
            }
            //获取文件有多少行
            int lineNum = strList.size();
            //获取数组有多少列
            String s =  strList.get(0);
            int columnNum = s.split("\t").length;
            //循环遍历集合，将集合中的数据放入ArrayList中
            for(String str : strList) {
                //List temp= new ArrayList();
                //将读取的str按照"\t"分割，用字符串数组来接收
                String[] strs = str.split("\t");
                int id=Integer.valueOf(strs[0]);
                System.out.println(id);
                String name=strs[1];
                System.out.println(name);
                String sid=strs[2];
                double x=Double.valueOf(strs[3]);
                double y=Double.valueOf(strs[4]);
                String enname=strs[5];
                stat.executeUpdate("insert into StationProperty(id,name,sid,x,y,enname) values('"+id+"','"+name+"','"+sid+"','"+x+"','"+y+"','"+enname+"')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭字符输入缓冲流
            try {
                if(readerBuf != null)
                    readerBuf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        stat.close();
        con.close();
        System.out.println("站点数据插入成功");*/


    }
}
