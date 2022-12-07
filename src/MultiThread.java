/*
学号：20307011
姓名：林浩媚
班级：20级地信班
作业简述：本次Java作业实现了降雨插值、流向计算的多线程运行。选择上述三个功能的原因是插值与流向计算功能无输入-输出关系，即三线程可以平行运行。
本作业没有将累积流功能加入多线程计算中，原因是多线程计算需以流向计算结果作为输入，故不适合参与多线程运作。
主要设计思路是：由于多线程只能执行无参或单参的方法，而本作业的主要函数均需要多个参数的输入，故构建了Data类以进行数据的初始化读取和输入。
分别新建三个线程类继承Thread类，重写run()函数，将主干功能函数置于run()函数函数体，通过Data类实现函数参数输入，并将计算结果存入.asc文件中。
本作业还将部分运行结果可视化打印，可以发现进程运行时各个线程抢占CPU时间片优先级较高的抢到的CPU时间线较多，且各线程交替进行.
 */
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class MultiThread {


    public static void main(String []args){
        for(int i=0;i<6;i++){
            if(i==2){
                MyThread_V v=new MyThread_V();
                v.setPriority(4);
                v.start();
            }
            if(i==3){
                MyThread_I I=new MyThread_I();
                I.setPriority(1);
                I.start();
            }
            if(i==5){
                MyThread_F f=new MyThread_F();
                f.setPriority(6);
                f.start();
            }
            System.out.println(Thread.currentThread().getName()+" "+i);
        }

    }
}

class Data{
    private int grid_col;//网格列数
    private int grid_row;//网格行数
    private int grid_size;//网格尺寸
    private double[] corner=new double[2];//网格左上角坐标
    private int NoData;//记录无效数据
    private ArrayList<List<Integer>> DEM=new ArrayList<>();//高程数据
    private int num_station;//站点数量
    private int num_day;//存储站点记录天数
    private ArrayList<List<Double>> station=new ArrayList<>();//站点坐标
    private ArrayList<List<Integer>> Pre=new ArrayList<>();//站点降水观测数据

    public Data(){
        //读取dem.asc文件
        //ArrayList<List<Integer>> DEM = new ArrayList<>();//存储asc文件中DEM数据；
        //ArrayList<List<Double>> Pro = new ArrayList<>();//存储测站属性
        //ArrayList<List<Integer>> Pre = new ArrayList<>();//存储测站降水数据
        int[]para=new int[4];//存储列、行、网格尺寸和无效值
        //int num_day=0;//存储降水量记录天数
        String filename1="src\\dem.asc";
        String filename2="src\\StationProperty.txt";
        String filename3="src\\rain.txt";
        readasc(para,corner,DEM,filename1);//读取DEM数据
        num_station=readproperty(station,filename2);//读取测站属性文件并返回测站数
        num_day=readpre(Pre,filename3);//读取降水量文件并返回记录天数
        grid_col=para[0];
        grid_row=para[1];
        grid_size=para[2];
        NoData=para[3];

    }
    public int[] get_para(){
        int[]para=new int[4];
        para[0]=grid_col;
        para[1]=grid_row;
        para[2]=grid_size;
        para[3]=NoData;
        return para;
    }

    public ArrayList<List<Integer>> getDEM(){
        return DEM;
    }

    public ArrayList<List<Integer>> getPre(){
        return Pre;
    }

    public ArrayList<List<Double>> get_station(){
        return station;
    }

    public double[] getCorner(){
        return corner;
    }

    public int getNum_station(){
        return num_station;
    }

    public int getNum_day(){
        return num_day;
    }

    //读取asc文件的函数
    public static void readasc(int[]para,double[]corner,ArrayList<List<Integer>> DEM,String filename){
        int ncols;//列数
        int nrows;//行数
        int cellsize;//网格大小
        int NoData_Value;//无效值
        double xllcorner;//左下角X坐标
        double yllcorner;//左下角Y坐标

        String temp;//临时存储字符串
        File DEMfile=new File(filename);
        try{
            Scanner inDEM=new Scanner(DEMfile);
            //利用光标移动录入各头文件参数
            temp=inDEM.next();//过行
            ncols=inDEM.nextInt();
            para[0]=ncols;//列
            temp=inDEM.next();
            nrows=inDEM.nextInt();
            para[1]=nrows;//行
            temp=inDEM.next();
            xllcorner=inDEM.nextDouble();
            corner[0]=xllcorner;
            temp=inDEM.next();
            yllcorner=inDEM.nextDouble();
            corner[1]=yllcorner;
            temp=inDEM.next();
            cellsize=inDEM.nextInt();
            para[2]=cellsize;
            temp=inDEM.next();
            NoData_Value=inDEM.nextInt();
            para[3]=NoData_Value;
            for(int i=0;i<nrows;i++){
                List datatemp= new ArrayList();
                for(int j=0;j<ncols;j++){
                    datatemp.add(inDEM.nextInt());//逐行录入数据
                }
                DEM.add(datatemp);
            }
            inDEM.close();
        }catch(FileNotFoundException e){
            System.out.println(e);
        }
    }

    //读取站点属性数据
    public static int readproperty(ArrayList<List<Double>> station ,String filename){
        int num=0;//存储测站数量
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
            //逐行读取txt文件中的内容
            //读取站点数量
            lineStr=readerBuf.readLine();
            String[] str_for_num = lineStr.split("\t");//利用制表符分割
            num=Integer.parseInt(str_for_num[0]);//提取测站数量
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
                List temp= new ArrayList();
                //将读取的str按照","分割，用字符串数组来接收
                String[] strs = str.split("\t");
                for(int i = 3; i < columnNum-1; i++) {//根据文件格式确定读取列范围
                    temp.add(Double.valueOf(strs[i]));
                }
                station.add(temp);
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
        return num;//返回测站数量
    }

    //读取站点数据
    public static int readpre(ArrayList<List<Integer>> pre,String filename){
        int day=0;//存储降水数据记录天数
        //声明一个字符输入流
        FileReader reader = null;
        //声明一个字符输入缓冲流
        BufferedReader readerBuf = null;
        try{
            //指定reader的读取路径
            reader = new FileReader(filename);
            //通过BufferedReader包装字符输入流
            readerBuf = new BufferedReader(reader);
            //创建一个集合，用来存放读取的文件的数据
            List<String> strList = new ArrayList<>();
            //用来存放一行的数据
            String lineStr;
            lineStr = readerBuf.readLine();
            String[] str_for_day = lineStr.split("\t");
            day=Integer.parseInt(str_for_day[3]);//提取观测天数
            lineStr = readerBuf.readLine();//跳过表头两行
            //逐行读取txt文件中的内容
            while((lineStr = readerBuf.readLine()) != null) {
                //把读取的行添加到list中
                strList.add(lineStr);
            }
            //获取文件有多少行
            int num = strList.size();
            //获取数组有多少列
            String s =  strList.get(0);
            int columnNum = s.split("\t").length;
            //循环遍历集合，将集合中的数据放入ArrayList中
            for(String str : strList) {
                List temp= new ArrayList();
                //将读取的str按照","分割，用字符串数组来接收
                String[] strs = str.split("\t");
                for(int i = 2; i < columnNum; i++) {//从第三列数据开始读入
                    temp.add(Integer.valueOf(strs[i]));
                }
                pre.add(temp);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
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
        return day;
    }

}

//泰森多边形插值子类
class MyThread_V extends Thread{
    Data d=new Data();
    ArrayList<List<Integer>> DEM=d.getDEM();
    ArrayList<List<Double>> station=d.get_station();
    ArrayList<List<Integer>> Pre=d.getPre();
    int[]para=d.get_para();
    double[]corner=d.getCorner();
    int num_station=d.getNum_station();
    int num_day=d.getNum_day();
    //int[]corner={200,200};
    //double[][] station= {{245,354,400.23},{438,590,230.48},{320,447,179.52}};//设置三个站点x,y,precipitation
    //Grid.grid d= new Grid.grid(6,6,120,corner,DEM,3,station);
    @Override
    public void run(){
        System.out.println("此处运行泰森多边形插值线程");
        interpolation_Vorono(num_station,station,Pre,num_day,para,corner);//泰森多边形插值

    }
    //泰森多边形插值辅助函数
    //距离计算函数
    static double Dist(int i,int j,double a,double b,double grid_size,double[] cell_corner,int grid_row){//计算单元中心点与站点距离
        double Dist;//attention
        //int grid_row=para[1];
        //j方向欧式距离+i方向欧氏距离
        Dist=pow(((cell_corner[1]+grid_size*grid_row-grid_size/2-i*grid_size)-b),2)+pow(((cell_corner[0]+grid_size/2+j*grid_size)-a),2);//单元中心点坐标通过索引转换计算
        Dist = sqrt(Dist);
        return Dist;
    }
    //求取最小距离并返回其索引
    static int minDist(double[] l,int num_station){//返回距离最近站点的索引
        int k=0;
        //double[] sort={l1,l2,l3};
        for(int i=0;i<num_station;i++){
            if(l[i]<=l[k]){//如果比最小值小 更新索引
                k=i;
            }
        }
        return k;//根据站点索引取站点降水量值
    }
    public static void interpolation_Vorono(int num_station,ArrayList<List<Double>> Pro,ArrayList<List<Integer>> Pre,int day,int[]para,double[]corner){
        //从参数数组提取行列数
        int grid_row=para[1];
        int grid_col=para[0];
        double[][] precp=new double[grid_row][grid_col];//存储插值结果降水量
        int[]sum_pre=new int[num_station];//
        //对天数降水量求和
        for(int a=0;a<num_station;a++){
            sum_pre[a]=0;//初始化降水数据
            for(int b=0;b<day;b++){
                sum_pre[a]+=Pre.get(b).get(a);//将对应站点的降水量求和
            }
        }
        System.out.println("泰森多边形插值结果:");
        for (int i = 0; i <grid_row; i++) {//利用泰森多边形插值单元降水量
            for(int j=0;j<grid_col;j++){
                double[] l=new double [num_station];
                for(int n=0;n<num_station;n++){
                    l[n]=Dist(i,j,Pro.get(n).get(0),Pro.get(n).get(1),para[2],corner,grid_row);//求取任意单元中心坐标到三个站点的距离
                }
                int k=minDist(l,num_station);//求取距离最近的站点的索引
                precp[i][j]=sum_pre[k];//根据返回索引取对应站点的降水量值
                System.out.print(precp[i][j]+" ");//以一定格式输出打印
                if(j==grid_col-1)
                    System.out.println("\n");
            }
        }
        System.out.println("\n");

        //将插值结果导出为asc文件
        File file=new File("src\\Vorono1.asc");
        try {
            FileWriter fileout = new FileWriter(file);//文件写入流
            for (int ii = 0; ii < grid_row; ii++) {
                for (int jj = 0; jj < grid_col; jj++) {
                    fileout.write(String.valueOf(precp[ii][jj]) + " ");
                }
                fileout.write("\r\n");
            }
        }catch(IOException e){
            System.out.println(e);
        }
        //fileout.close();
    }
}

//反距离权重法插值子类
class MyThread_I extends Thread{
    Data d=new Data();
    ArrayList<List<Integer>> DEM=d.getDEM();
    ArrayList<List<Double>> station=d.get_station();
    ArrayList<List<Integer>> Pre=d.getPre();
    int[]para=d.get_para();
    double[]corner=d.getCorner();
    int num_station=d.getNum_station();
    int num_day=d.getNum_day();
    //int[]corner={200,200};
    //double[][] station= {{245,354,400.23},{438,590,230.48},{320,447,179.52}};//设置三个站点x,y,precipitation
    //Grid.grid d= new Grid.grid(6,6,120,corner,DEM,3,station);
    @Override
    public void run(){
        System.out.println("此处运行反距离权重法插值线程");
        interpolation_InvstDistWght(num_station,station,Pre,num_day,para,corner);//反距离权重法插值

    }
    //反距离权重法插值辅助函数
    //距离计算函数
    static double Dist(int i,int j,double a,double b,double grid_size,double[] cell_corner,int grid_row){//计算单元中心点与站点距离
        double Dist;//attention
        //int grid_row=para[1];
        //j方向欧式距离+i方向欧氏距离
        Dist=pow(((cell_corner[1]+grid_size*grid_row-grid_size/2-i*grid_size)-b),2)+pow(((cell_corner[0]+grid_size/2+j*grid_size)-a),2);//单元中心点坐标通过索引转换计算
        Dist = sqrt(Dist);
        return Dist;
    }
    //求取最小距离并返回其索引
    static int minDist(double[] l,int num_station){//返回距离最近站点的索引
        int k=0;
        //double[] sort={l1,l2,l3};
        for(int i=0;i<num_station;i++){
            if(l[i]<=l[k]){//如果比最小值小 更新索引
                k=i;
            }
        }
        return k;//根据站点索引取站点降水量值
    }
    //反距离权重法权重计算
    static void Weight(double[] l,int p,double[] lbda,int num_station){
        double sumDist=0.0;
        for(int i=0;i<num_station;i++)
            sumDist+=pow(l[i],-p);//调用pow函数进行幂指数运算
        for(int n=0;n<num_station;n++){
            lbda[n]=pow(l[n],-p)/sumDist;//反距离权重法计算权重
        }
    }
    //反距离权重法插值方法
    public static void interpolation_InvstDistWght(int num_station,ArrayList<List<Double>> Pro,ArrayList<List<Integer>> Pre,int day,int[]para,double[]corner){
        //从参数数组提取行列数
        int grid_row=para[1];
        int grid_col=para[0];
        double[][] precp=new double[grid_row][grid_col];//存储插值结果降水量
        double[] lbda=new double[num_station];//存储权重
        int[]sum_pre=new int[num_station];//
        //对天数降水量求和
        for(int a=0;a<num_station;a++){
            sum_pre[a]=0;//对测站降水数据进行初始化
            for(int b=0;b<day;b++){
                sum_pre[a]+=Pre.get(b).get(a);
            }
        }
        int p=2;//p为指数值，可修改 此处选取2为反距离平方法
        System.out.println("反距离权重法插值结果:");
        for (int i = 0; i < grid_row; i++) {
            for(int j = 0;j < grid_col;j++){
                double[] l=new double [num_station];//存储任意单元中心点到站点距离
                for(int n=0;n<num_station;n++){
                    l[n]=Dist(i,j,Pro.get(n).get(0),Pro.get(n).get(1),para[2],corner,grid_row);//求取任意单元中心坐标到三个站点的距离
                }
                Weight(l,p,lbda,num_station);//进行反距离权重计算
                //precp[i][j]=lbda[0]*station_pre[0][2]+lbda[1]*station_pre[1][2]+lbda[2]*station_pre[2][2];//反距离权重法计算降水量
                for(int m=0;m<num_station;m++){
                    precp[i][j]+=lbda[m]*sum_pre[m];
                }
                System.out.printf("%.2f ",precp[i][j]);//以一定格式输出打印 控制打印两位小数(四舍五入)
                //System.out.print(precp[i][j]+" ");//以一定格式输出打印
                if(j==grid_col-1)
                    System.out.println("\n");
            }
        }
        //将插值结果导出为asc文件
        File file=new File("src\\InvsDistWght1.asc");
        try {
            FileWriter fileout = new FileWriter(file);//文件写入流
            for (int ii = 0; ii < grid_row; ii++) {
                for (int jj = 0; jj < grid_col; jj++) {
                    fileout.write(String.valueOf(precp[ii][jj]) + " ");
                }
                fileout.write("\r\n");
            }
        }catch(IOException e){
            System.out.println(e);
        }
    }
}

//流向计算子类
class MyThread_F extends Thread{
    Data d=new Data();
    ArrayList<List<Integer>> DEM=d.getDEM();
    ArrayList<List<Double>> station=d.get_station();
    ArrayList<List<Integer>> Pre=d.getPre();
    int[]para=d.get_para();
    double[]corner=d.getCorner();
    int num_station=d.getNum_station();
    int num_day=d.getNum_day();
    //int[]corner={200,200};
    //double[][] station= {{245,354,400.23},{438,590,230.48},{320,447,179.52}};//设置三个站点x,y,precipitation
    //Grid.grid d= new Grid.grid(6,6,120,corner,DEM,3,station);
    @Override
    public void run(){
        System.out.println("此处运行流向计算线程");
        flow_direction(DEM,para);//流向计算

    }
    //流向计算辅助函数
    //填充函数，该函数作用是将当前ArrayList数据的四周填充数值(本程序设定为无效值-1)，以实现每个单元均存在八领域便于流向和累积流的判断
    static void Padding(ArrayList<List<Integer>> l,ArrayList<List<Integer>> f){//l为原ArrayList f为填充后ArrayList
        for(int i=0;i<l.size()+2;i++){//基于输入DEM数据的行尺寸进行循环和填充。对已有数据最上和最下行进行填充故+2
            List temp= new ArrayList();
            //int[] temp=new int[DEM.get(0).size()+2];
            for(int j=0;j<l.get(0).size()+2;j++){//基于输入DEM数据的列尺寸进行循环和填充。对已有数据最左和最右列进行填充故+2
                if(i==0||j==0||i==l.size()+1||j==l.get(0).size()+1){
                    temp.add(-1);//在数据外围加上-1 padding
                }
                else{
                    temp.add(l.get(i-1).get(j-1));//非填充部分进行原始数据的复制
                    //temp[j]=0;
                }
            }
            //List templist=Arrays.asList(temp);
            f.add(temp);//以行为单位构成填充后ArrayList
        }
    }
    //该函数作用是输入当前单元 计算八邻域最小值并返回中心单元对应流向数值
    static int flowDirect(ArrayList<List<Integer>> l, int i, int j,int NoData){//i,j为中心单元坐标 NoData为无效数据值
        //将八邻域降水量存为数组进行最小值定位
        int[] unit_8={l.get(i-1).get(j-1),l.get(i-1).get(j),l.get(i-1).get(j+1),l.get(i).get(j-1),l.get(i).get(j),l.get(i).get(j+1),l.get(i+1).get(j-1),l.get(i+1).get(j),l.get(i+1).get(j+1)};
        int min=l.get(i).get(j);//默认中心点为初始最小值
        int Index=4;//将最小值索引初始化为单元中心对应索引
        for(int k=0;k<9;k++){//求八邻域最小值
            if(unit_8[k]!=-1&&unit_8[k]!=NoData){//剔除NoData
                if(unit_8[k]<min){
                    min=unit_8[k];
                    Index=k;
                }
            }
        }
        int Direction=-1;//初始化返回值
        //根据表格给出流向对应数值
        switch (Index){//使用switch函数进行流向数值确定
            case 0:
                Direction=32;
                break;
            case 1:
                Direction=64;
                break;
            case 2:
                Direction=128;
                break;
            case 3:
                Direction=16;
                break;
            case 4:
                System.out.println("("+i+","+j+")处为内流点");//此处进行内流点提醒
                Direction=0;
                break;
            case 5:
                Direction=1;
                break;
            case 6:
                Direction=8;
                break;
            case 7:
                Direction=4;
                break;
            case 8:
                Direction=2;
                break;
        }
        return Direction;
    }
    //流向计算方法
    public static int[][] flow_direction(ArrayList<List<Integer>> DEM,int[]para){
        ArrayList<List<Integer>> DEMPad = new ArrayList<List<Integer>>();//存储填充数据，同样使用不需预先知道尺寸的ArrayList
        //注释占位
        Padding(DEM,DEMPad);
        int[][] FlowDirction=new int[DEM.size()][DEM.get(0).size()];//根据输入数据尺寸初始化 流向数据数组
        //ArrayList<List<Integer>> FlowDirction= new ArrayList<List<Integer>>();//存储流向数据
        for(int a=0;a<DEM.size()+2;a++){
            if(a!=0&&a!=DEM.size()+1){
                //List tempp= new ArrayList();//临时存储某一行的流向数据
                for(int b=0;b<DEM.get(0).size()+2;b++){//调用函数赋值数据流向
                    if(DEMPad.get(a).get(b)!=-1){//此处避开了填充的NA
                        if(DEMPad.get(a).get(b)==para[3]){//此处将输入数据的无效数据(-9999)处流向值赋值为-1
                            FlowDirction[a-1][b-1]=-1;
                        }
                        else {
                            FlowDirction[a-1][b-1]=(flowDirect(DEMPad, a, b,para[3]));
                        }
                    }
                }
                //FlowDirction.add(tempp);
            }
        }
        System.out.println("流向计算结果:");
        for (int[] ints : FlowDirction) {
            for (int j = 0; j < FlowDirction[0].length; j++) {
                System.out.printf("%4d", ints[j]);
                if (j == FlowDirction[0].length - 1) {
                    System.out.print("\n");
                }
            }
        }
        System.out.print("\n");
        //将插值结果导出为asc文件
        File file=new File("src\\FlowDirection1.asc");
        try {
            FileWriter fileout = new FileWriter(file);//文件写入流
            for (int ii = 0; ii < FlowDirction.length; ii++) {
                for (int jj = 0; jj < FlowDirction[0].length; jj++) {
                    fileout.write(String.valueOf(FlowDirction[ii][jj]) + " ");
                }
                fileout.write("\r\n");
            }
        }catch(IOException e){
            System.out.println(e);
        }
        return FlowDirction;
    }
}

