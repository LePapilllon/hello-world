package test;

import java.util.Scanner;

import static java.lang.Math.sqrt;
import static java.lang.Math.round;

public class Vorono {
    static double Dist(int i,int j,double a,double b){//计算单元中心点与站点距离
        double Dist;
        Dist=(120*(6-i)-a)*(120*(6-i)-a)+(120*(j+1)-b)*(120*(j+1)-b);//单元中心点坐标通过索引转换计算
        Dist = sqrt(Dist);
        return Dist;//
    }
    static int minDist(double[] l){//返回距离最近站点的索引
        int k=0;
        //double[] sort={l1,l2,l3};
        for(int i=0;i<3;i++){
            if(l[i]<=l[0]){
                k=i;
            }
        }
        return k;//根据站点索引取站点降水量值
    }

    public static void main(String []args) {
        /*
        double[] station1= {2450,3540,400.23};//x,y,precipitation
        double[] station2= {4389,790,230.48};
        double[] station3= {320,4467,179.52};*/
        double[][] station= {{245,354,400.23},{438,590,230.48},{320,447,179.52}};//设置三个站点x,y,precipitation

        double[][] precp=new double[6][6];//存储插值结果降水量
        for (int i = 0; i < 6; i++) {//利用克里金多边形插值单元降水量23
            for(int j=0;j<6;j++){
                double[] l=new double [3];
                for(int n=0;n<3;n++){
                    l[n]=Dist(i,j,station[n][0],station[n][1]);//求取任意单元中心坐标到三个站点的距离
                }
                int k=minDist(l);//求取距离最近的站点的索引
                precp[i][j]=station[k][2];//根据返回索引取对应站点的降水量值
                System.out.print(precp[i][j]+" ");//以一定格式输出打印
                if(j==5)
                    System.out.println("\n");
            }
        }
        //实现输入坐标输出所插降水量值
        System.out.println("Please input the location by the format of x y:");
        Scanner s=new Scanner(System.in);
        double x,y;
        if(s.hasNextLine()) {
            String str = s.nextLine();
            String[] strs = str.split(" ");
            x=Double.parseDouble(strs[0]);//通过用户键入得到坐标值
            y=Double.parseDouble(strs[1]);
            int m=(int)round(6-(x/120));//将坐标值转换为对应数组索引
            int n=(int)round((y/120)-1);//此处进行四舍五入 即处于共边界的坐标的降水量会偏向坐标值更大处的单元的降水量值
            System.out.println("The precipitation of the location is "+precp[m][n]);//根据索引取对应的降水量值
        }else{
            System.out.print("ERROR!");
            System.exit(0);
        }
    }
}
