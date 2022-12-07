/*
学号：20307011
姓名：林浩媚
班级：20级地信班
作业简述：本次作业实现了将dem.sac,rain.txt,stationproperty.txt数据表存入数据库中，并从数据库取出相关数据进行降水量的克里金插值和反距离权重插值计算。
并新建插值结果表将结果存入数据库中。
 */
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public class rain_sql {
    static double Dist(int i,int j,double a,double b,int grid_size,double[] cell_corner,int grid_row){//计算单元中心点与站点距离
        double Dist;//attention
        //int grid_row=para[1];
        //j方向欧式距离+i方向欧氏距离
        Dist=pow(((cell_corner[1]+grid_size*grid_row-1.0*grid_size/2-i*grid_size)-b),2)+pow(((cell_corner[0]+1.0*grid_size/2+j*grid_size)-a),2);//单元中心点坐标通过索引转换计算
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
    //反距离权重法插值辅助函数
    static void Weight(double[] l,int p,double[] lbda,int num_station){
        double sumDist=0.0;
        for(int i=0;i<num_station;i++)
            sumDist+=pow(l[i],-p);//调用pow函数进行幂指数运算
        for(int n=0;n<num_station;n++){
            lbda[n]=pow(l[n],-p)/sumDist;//反距离权重法计算权重
        }
    }
    public static void interpolation_Vorono(int num_station, ArrayList<List<Double>> Pro, double[][]Pre,int grid_row,int grid_col,int cellsize,double[]corner){
        //从参数数组提取行列数
        //int grid_row=para[1];
        //int grid_col=para[0];
        //double[][] precp=new double[grid_row][grid_col];//存储插值结果降水量
        System.out.println("泰森多边形插值结果:");
        for (int i = 0; i <grid_row; i++) {//利用泰森多边形插值单元降水量
            for(int j=0;j<grid_col;j++){
                double[] l=new double [num_station];
                for(int n=0;n<num_station;n++){
                    l[n]=Dist(i,j,Pro.get(n).get(0),Pro.get(n).get(1),cellsize,corner,grid_row);//求取任意单元中心坐标到三个站点的距离
                }
                int k=minDist(l,num_station);//求取距离最近的站点的索引
                Pre[i][j]=Pro.get(k).get(2);//根据返回索引取对应站点的降水量值
                System.out.print(Pre[i][j]+" ");//以一定格式输出打印
                if(j==grid_col-1)
                    System.out.println("\n");
            }
        }
        System.out.println("\n");
    }
    public static void interpolation_InvstDistWght(int num_station, ArrayList<List<Double>> Pro, double[][]Pre,int grid_row,int grid_col,int cellsize,double[]corner){
        //从参数数组提取行列数
        //int grid_row=para[1];
        //int grid_col=para[0];
        //double[][] precp=new double[grid_row][grid_col];//存储插值结果降水量
        double[] lbda=new double[num_station];//存储权重
        int p=2;//p为指数值，可修改 此处选取2为反距离平方法
        System.out.println("反距离权重法插值结果:");
        for (int i = 0; i < grid_row; i++) {
            for(int j = 0;j < grid_col;j++){
                double[] l=new double [num_station];//存储任意单元中心点到站点距离
                for(int n=0;n<num_station;n++){
                    l[n]=Dist(i,j,Pro.get(n).get(0),Pro.get(n).get(1),cellsize,corner,grid_row);//求取任意单元中心坐标到三个站点的距离
                }
                Weight(l,p,lbda,num_station);//进行反距离权重计算
                //precp[i][j]=lbda[0]*station_pre[0][2]+lbda[1]*station_pre[1][2]+lbda[2]*station_pre[2][2];//反距离权重法计算降水量
                for(int m=0;m<num_station;m++){
                    Pre[i][j]+=lbda[m]*Pro.get(m).get(2);
                }
                System.out.printf("%.2f ",Pre[i][j]);//以一定格式输出打印 控制打印两位小数(四舍五入)
                //System.out.print(precp[i][j]+" ");//以一定格式输出打印
                if(j==grid_col-1)
                    System.out.println("\n");
            }
        }
    }

    public static void main(String[] args)throws SQLException, ClassNotFoundException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");//加载驱动
            System.out.println("数据库引擎加载成功");
        }catch(ClassNotFoundException e){
            e.printStackTrace();
        }
        //try {
        //用户信息与url
        String url = "jdbc:mysql://localhost:3306/grid?useUnicode=true&characterEncoding=utf8&useSSL=true";//url包含数据库名称
        String username = "root";
        String password = "111111";

        //建立与数据库grid的连接
        Connection con=DriverManager.getConnection(url, username, password);
        System.out.println("连接数据库grid成功");
        Statement stat=con.createStatement();//创建statement接口
        System.out.println("statement对象连接成功");

        //通过数据库查询降雨站数量与记录降水值以及网格相关参数
        ResultSet rs;
        rs=stat.executeQuery("select count(*) as count from StationProperty");
        rs.next();
        int num_station=rs.getInt(1);//记录降水站数量 索引从1开始 必须指定类型
        System.out.println(num_station);
        rs=stat.executeQuery("select sum(huanglongdai)as hld,sum(lianxing)as lx,sum(fengmulang)as fml from rain");
        rs.next();
        int hld=rs.getInt(1);
        int lx=rs.getInt(2);
        int fml=rs.getInt(3);
        System.out.println(hld);
        rs=stat.executeQuery("select x as x,y as y from StationProperty where enname='huanglongdai'");
        rs.next();
        double hld_x=rs.getDouble(1);
        double hld_y= rs.getDouble(2);
        rs=stat.executeQuery("select x as x,y as y from StationProperty where enname='lianxing'");
        rs.next();
        double lx_x=rs.getDouble(1);
        double lx_y= rs.getDouble(2);
        rs=stat.executeQuery("select x as x,y as y from StationProperty where enname='fengmulang'");
        rs.next();
        double fml_x=rs.getDouble(1);
        double fml_y= rs.getDouble(2);
        System.out.println(fml_y);
        rs=stat.executeQuery("select* from dem_para");
        rs.next();
        int col=rs.getInt(1);
        int row= rs.getInt(2);
        double xllcorner=rs.getDouble(3);
        double yllcorner=rs.getDouble(4);
        int cellsize=rs.getInt(5);
        //int NODATA=rs.getInt(6);

        double[] corner={xllcorner,yllcorner};
        ArrayList<List<Double>> Pro=new ArrayList<>();

        //将三个测站坐标及降水量存为ArrayList
        Pro.add(Arrays.asList(hld_x,hld_y,(double)hld));
        Pro.add(Arrays.asList(lx_x,lx_y,(double)lx));
        Pro.add(Arrays.asList(fml_x,fml_y,(double)fml));

        //存储插值结果
        double[][] Pre_V=new double[row][col];
        double[][] Pre_I=new double[row][col];

        //克里金插值
        interpolation_Vorono(num_station, Pro, Pre_V,row,col,cellsize,corner);

        //新建克里金插值结果表
        stat.executeUpdate("create table Vorono(drow int,dcol int,value double)");
        System.out.println("数据表Vorono创建成功");

        //将插值结果导入数据库
        for (int ii = 0; ii < row; ii++) {
            for (int jj = 0; jj < col; jj++) {
                stat.executeUpdate("insert into Vorono(drow,dcol,value)values('"+ii+"','"+jj+"','"+Pre_V[ii][jj]+"')");
            }
        }
        System.out.println("克里金插值结果插入成功");

        //反距离权重插值
        interpolation_InvstDistWght(num_station, Pro, Pre_I,row,col,cellsize,corner);

        //新建反距离权重插值结果表
        stat.executeUpdate("create table Invst(drow int,dcol int,value double)");
        System.out.println("数据表Invst创建成功");

        //将插值结果导入数据库
        for (int i = 0; i < row; i++) {
            for (int j = 0; j< col; j++) {
                stat.executeUpdate("insert into Invst(drow,dcol,value)values('"+i+"','"+j+"','"+Pre_I[i][j]+"')");
            }
        }
        System.out.println("反距离权重插值结果插入成功");
    }
}
