package test;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

public class InvsDistWght {
    static double Dist(int i, int j, double a, double b) {//计算单元中心点与站点距离
        double Dist;
        Dist = (1000 * (5 - i) - a) * (1000 * (5 - i) - a) + (1000 * (j + 1) - b) * (1000 * (j + 1) - b);//单元中心点坐标通过索引转换计算
        Dist = sqrt(Dist);
        return Dist;//
    }
    static void Weight(double[] l,int p,double[] lbda){
        double sumDist=pow(l[0],-p)+pow(l[1],-p)+pow(l[2],-p);//调用pow函数进行幂指数运算
        for(int n=0;n<3;n++){
        lbda[n]=pow(l[n],-p)/sumDist;//反距离权重法计算权重
        }
    }

    public static void main(String []args) {
        double[][] station= {{2450,3540,400.23},{4389,790,230.48},{320,4467,179.52}};//设置三个站点x,y,precipitation

        double[][] precp=new double[5][5];//存储插值结果降水量
        double[] lbda=new double[3];//存储权重
        int p=2;//p为指数值，可修改 此处选取2为反距离平方法
        for (int i = 0; i < 5; i++) {
            for(int j = 0;j < 5;j++){
                double[] l=new double [3];//存储任意单元中心点到站点距离
                for(int n=0;n<3;n++){
                    l[n]=Dist(i,j,station[n][0],station[n][1]);//求取任意单元中心坐标到三个站点的距离
                }
                Weight(l,p,lbda);//进行反距离权重计算
                precp[i][j]=lbda[0]*station[0][2]+lbda[1]*station[1][2]+lbda[2]*station[2][2];//反距离权重法计算降水量
                System.out.printf("%.2f ",precp[i][j]);//以一定格式输出打印 控制打印两位小数(四舍五入)
                //System.out.print(precp[i][j]+" ");//以一定格式输出打印
                if(j==4)
                    System.out.println("\n");
            }
        }
    }
}

