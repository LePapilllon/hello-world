/*
学号：20307011
姓名：林浩媚
班级：20级地信班
作业简述：本次Java编程作业实现了网格计算类的编写。实现了泰森多边形插值计算、反距离权重法插值计算、流向计算、累积流计算和流向图绘制等5个方法。
对于累积流，主是基于回溯法进行计算。对于流向图的绘制，利用swing库进行实现，利用箭头表示不同单元的流向，将地形最低点即水流汇点绘制为圆圈。
 */
package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Math.sqrt;
import static java.lang.Math.pow;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

public class Grid {
    //泰森多边形插值辅助函数
    //距离计算函数
    static double Dist(int i,int j,double a,double b,double grid_size,int[] cell_corner){//计算单元中心点与站点距离
        double Dist;//attention
        //j方向欧式距离+i方向欧氏距离
        Dist=pow(((cell_corner[1]+grid_size/2+i*grid_size)-a),2)+pow(((cell_corner[0]+grid_size/2+j*grid_size)-b),2);//单元中心点坐标通过索引转换计算
        Dist = sqrt(Dist);
        return Dist;
    }
    //求取最小距离并返回其索引
    static int minDist(double[] l,int num_station){//返回距离最近站点的索引
        int k=0;
        //double[] sort={l1,l2,l3};
        for(int i=0;i<num_station;i++){
            if(l[i]<=l[k]){//如果距离比原最小值小 更新索引
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
    static int flowDirect(ArrayList<List<Integer>> l, int i, int j){//i,j为中心单元坐标
        //将八邻域降水量存为数组进行最小值定位
        int[] unit_8={l.get(i-1).get(j-1),l.get(i-1).get(j),l.get(i-1).get(j+1),l.get(i).get(j-1),l.get(i).get(j),l.get(i).get(j+1),l.get(i+1).get(j-1),l.get(i+1).get(j),l.get(i+1).get(j+1)};
        int min=l.get(i).get(j);//默认中心点为初始最小值
        int Index=4;//将最小值索引初始化为单元中心对应索引
        for(int k=0;k<9;k++){//求八邻域最小值
            if(unit_8[k]!=-1&&unit_8[k]!=-9999){//剔除NoData
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

    //累积流计算函数
    static public int[][] Accumulation(int[][] Direction)
    {
        int i, j;
        int[][] result = new int[Direction.length][Direction[0].length];
        boolean[][] Origin = CalOrigin(Direction);//计算是否是水流源点
        boolean[][] OutFlow = CalOut(Direction);//计算是否是水流出点
        for (i = 0; i < Direction.length; i++)
        {
            for (j = 0; j < Direction[0].length; j++)
            {
                if (OutFlow[i][j])
                {
                    Add(Direction, Origin, result, i, j);//累加函数，采用回溯法
                }
            }
        }
        return result;
    }
    //计算河流源点
    static private boolean[][] CalOrigin(int[][] Direction)
    {
        int i, j;//所有等于0的地方极有可能是河流汇点
        boolean[][] flag = new boolean[Direction.length][Direction[0].length];
        for (j = 0; j < Direction[0].length; j++)
        {
            i = 0;
            flag[i][j] = Direction[i][j] >= 2 && Direction[i][j] <= 8;
            i = Direction.length - 1;
            flag[i][j] = Direction[i][j] >= 32;
        }
        for (i = 0; i < Direction.length; i++)
        {
            j = 0;
            flag[i][j] = Direction[i][j] == 1 || Direction[i][j] == 2 || Direction[i][j] == 128;
            j = Direction[0].length - 1;
            flag[i][j] = Direction[i][j] >= 8 && Direction[i][j] <= 32;
        }
        /*System.out.print("CalOrigin flag\n");
        for (boolean[] booleans : flag) {
            for (int b = 0; b < flag[0].length; b++) {
                System.out.print(booleans[b] + " ");
                if (b == 4) {
                    System.out.print("\n");
                }
            }
        }*/
        return flag;
    }
    //计算河流出点
    static private boolean[][] CalOut(int[][] Direction)
    {
        int i, j;
        boolean[][] flag = new boolean[Direction.length][Direction[0].length];
        for (j = 0; j < Direction[0].length; j++)
        {
            i = 0;
            flag[i][j] = Direction[i][j] >= 32||Direction[i][j]==0 ;
            i = Direction.length - 1;
            flag[i][j] = (Direction[i][j] >= 2 && Direction[i][j] <= 8)||Direction[i][j]==0;
        }
        for (i = 0; i < Direction.length; i++)
        {
            j = 0;
            flag[i][j] = (Direction[i][j] >= 8 && Direction[i][j] <= 32)||Direction[i][j]==0;
            j = Direction[0].length - 1;
            flag[i][j] = Direction[i][j] == 1 || Direction[i][j] == 2 || Direction[i][j] == 128||Direction[i][j]==0;
        }
        return flag;
    }
    //利用回溯法计算累积流
    static private int Add(int[][] Direction, boolean[][] Origin, int[][] Result, int i, int j)//利用回溯法计算累积汇流量
    {
        /*if (Origin[i][j])
        {
            Result[i][j] = 0;
            return 0;
        }*/
        if (j - 1 >= 0)//判断邻域单元是否存在
        {
            if (Direction[i][j - 1] == 1)//判断领域单元是否流入当前单元
            {
                Result[i][j] += Add(Direction, Origin, Result, i, j - 1);//求取流入本单元邻域单元的初始累积量
                Result[i][j]++;//每一个符合条件的单元格汇聚到洼地均需加上自身的单位流量1
            }
        }
        if (i - 1 >= 0 && j - 1 >= 0)
        {
            if (Direction[i - 1][j - 1] == 2)
            {
                Result[i][j] += Add(Direction, Origin, Result, i - 1, j - 1);
                Result[i][j]++;
            }
        }
        if (i - 1 >= 0)
        {
            if (Direction[i - 1][j] == 4)
            {

                Result[i][j] += Add(Direction, Origin, Result, i - 1, j);
                Result[i][j]++;
            }
        }
        if (i - 1 >= 0 && j + 1 < Direction[0].length)
        {
            if (Direction[i - 1][j + 1] == 8)
            {
                Result[i][j] += Add(Direction, Origin, Result, i - 1, j + 1);
                Result[i][j]++;
            }
        }
        if (j + 1 < Direction[0].length)
        {
            if (Direction[i][j + 1] == 16)
            {
                Result[i][j] += Add(Direction, Origin, Result, i, j + 1);
                Result[i][j]++;
            }
        }
        if (i + 1 < Direction.length && j + 1 < Direction[0].length)
        {
            if (Direction[i + 1][j + 1] == 32)
            {
                Result[i][j] += Add(Direction, Origin, Result, i + 1, j + 1);
                Result[i][j]++;
            }
        }
        if (i + 1 < Direction.length)
        {
            if (Direction[i + 1][j] == 64)
            {
                Result[i][j] += Add(Direction, Origin, Result, i + 1, j);
                Result[i][j]++;
            }
        }
        if (i + 1 < Direction.length && j - 1 >= 0)
        {
            if (Direction[i + 1][j - 1] == 128)
            {
                Result[i][j] += Add(Direction, Origin, Result, i + 1, j - 1);
                Result[i][j]++;
            }
        }
        return Result[i][j];
    }

    //流向图绘制辅助函数
    //该函数通过流向数据绘制对应方向和位置的箭头
    public static int[] head_tail(int i,int j,int d,int size,int[] cell_corner){
        int[] corrd=new int[4];//存储首尾坐标
        //计算该单元格四个顶点坐标
        //左上角
        int x1=cell_corner[0]+size*j;
        int y1=cell_corner[1]+size*i;
        //右上角
        int x2=cell_corner[0]+size*(j+1);
        int y2=cell_corner[1]+size*i;
        //左下角
        int x3=cell_corner[0]+size*j;
        int y3=cell_corner[1]+size*(i+1);
        //右下角
        int x4=cell_corner[0]+size*(j+1);
        int y4=cell_corner[1]+size*(i+1);
        switch(d) {//确定各流向对应箭头首尾坐标值
            case 1:
                corrd[0]=(x2+x4)/2;
                corrd[1]=(y2+y4)/2;
                corrd[2]=(x1+x3)/2;
                corrd[3]=(y1+y3)/2;
                break;
            case 2:
                corrd[0]=x4;
                corrd[1]=y4;
                corrd[2]=x1;
                corrd[3]=y1;
                break;
            case 4:
                corrd[0]=(x3+x4)/2;
                corrd[1]=(y3+y4)/2;
                corrd[2]=(x1+x2)/2;
                corrd[3]=(y1+y2)/2;
                break;
            case 8:
                corrd[0]=x3;
                corrd[1]=y3;
                corrd[2]=x2;
                corrd[3]=y2;
                break;
            case 16:
                corrd[0]=(x3+x1)/2;
                corrd[1]=(y3+y1)/2;
                corrd[2]=(x4+x2)/2;
                corrd[3]=(y4+y2)/2;
                break;
            case 32:
                corrd[0]=x1;
                corrd[1]=y1;
                corrd[2]=x4;
                corrd[3]=y4;
                break;
            case 64:
                corrd[0]=(x2+x1)/2;
                corrd[1]=(y2+y1)/2;
                corrd[2]=(x4+x3)/2;
                corrd[3]=(y4+y3)/2;
                break;
            case 128:
                corrd[0]=x2;
                corrd[1]=y2;
                corrd[2]=x3;
                corrd[3]=y3;
                break;
        }
        return corrd;
    }

    static public class grid{
        private int grid_col;//网格列数
        private int grid_row;//网格行数
        private int grid_size;//网格尺寸
        private int[] cell_corner;//网格左上角坐标
        private ArrayList<List<Integer>> DEM;//高程数据
        private int num_station;//站点数量
        private double[][] station_pre;//站点坐标及观测数据

        //无参构造函数
        /*public grid(){
            int grid_col=0;
            int gard_row=0;
            double grid_size=0;
            int num_station=0;
        };*/
        //全参数构造函数
        public grid(int col,int row,int size,int[] corner,ArrayList<List<Integer>> DDEM,int station,double[][] sta_pre){
            grid_col=col;
            grid_row=row;
            grid_size=size;
            //获取左上角坐标
            cell_corner=(int[]) Arrays.copyOf(corner,2);//数组复制
            num_station=station;
            //获取DEM数据
            DEM=new ArrayList<List<Integer>>();
            for (int i=0;i<row;i++){
                DEM.add(DDEM.get(i));
            }
            //获取站点坐标及观测数据
            station_pre=new double[station][3];
            for (int j=0;j<num_station;j++){
                station_pre[j]=(double[])Arrays.copyOf(sta_pre[j],3);//三个数据分别为x y 和降水量
            }
        }

        //泰森多边形插值方法
        public void interpolation_Vorono(){
            double[][] precp=new double[grid_row][grid_col];//存储插值结果降水量
            System.out.println("泰森多边形插值结果:");
            for (int i = 0; i <grid_row; i++) {//利用克里金多边形插值单元降水量
                for(int j=0;j<grid_col;j++){
                    double[] l=new double [num_station];
                    for(int n=0;n<num_station;n++){
                        l[n]=Dist(i,j,station_pre[n][0],station_pre[n][1],grid_size,cell_corner);//求取任意单元中心坐标到三个站点的距离
                    }
                    int k=minDist(l,num_station);//求取距离最近的站点的索引
                    precp[i][j]=station_pre[k][2];//根据返回索引取对应站点的降水量值
                    System.out.print(precp[i][j]+" ");//以一定格式输出打印
                    if(j==grid_col-1)
                        System.out.println("\n");
                }
            }
            System.out.println("\n");

        }

        //反距离权重法插值方法
        public void interpolation_InvstDistWght(){
            double[][] precp=new double[grid_row][grid_col];//存储插值结果降水量
            double[] lbda=new double[num_station];//存储权重
            int p=2;//p为指数值，可修改 此处选取2为反距离平方法
            System.out.println("反距离权重法插值结果:");
            for (int i = 0; i < grid_row; i++) {
                for(int j = 0;j < grid_col;j++){
                    double[] l=new double [num_station];//存储任意单元中心点到站点距离
                    for(int n=0;n<num_station;n++){
                        l[n]=Dist(i,j,station_pre[n][0],station_pre[n][1],grid_size,cell_corner);//求取任意单元中心坐标到三个站点的距离
                    }
                    Weight(l,p,lbda,num_station);//进行反距离权重计算
                    //precp[i][j]=lbda[0]*station_pre[0][2]+lbda[1]*station_pre[1][2]+lbda[2]*station_pre[2][2];//反距离权重法计算降水量
                    for(int m=0;m<num_station;m++){
                        precp[i][j]+=lbda[m]*station_pre[m][2];
                    }
                    System.out.printf("%.2f ",precp[i][j]);//以一定格式输出打印 控制打印两位小数(四舍五入)
                    //System.out.print(precp[i][j]+" ");//以一定格式输出打印
                    if(j==grid_col-1)
                        System.out.println("\n");
                }
            }

        }
        //流向计算方法
        public int[][] flow_direction(){
            ArrayList<List<Integer>> DEMPad = new ArrayList<List<Integer>>();//存储填充数据，同样使用不需预先知道尺寸的ArrayList
            //注释占位
            Padding(DEM,DEMPad);
            int[][] FlowDirction=new int[DEM.size()][DEM.get(0).size()];//根据输入数据尺寸初始化 流向数据数组
            //ArrayList<List<Integer>> FlowDirction= new ArrayList<List<Integer>>();//存储流向数据
            for(int a=0;a<DEM.size()+2;a++){
                if(a!=0&&a!=DEM.size()+1){
                   //List tempp= new ArrayList();//临时存储某一行的流向数据
                   for(int b=0;b<DEM.get(0).size()+2;b++){//调用函数赋值数据流向
                   /*System.out.printf("%6d",DEMPad.get(a).get(b));
                    if(b==DEMPad.get(0).size()-1)FlowDirction
                    System.out.print("\n");*/
                        if(DEMPad.get(a).get(b)!=-1){//此处避开了填充的NA
                           if(DEMPad.get(a).get(b)==-9999){//此处将输入数据的无效数据(-9999)处流向值赋值为-1
                               FlowDirction[a-1][b-1]=-1;
                           }
                            else {
                               FlowDirction[a-1][b-1]=(flowDirect(DEMPad, a, b));
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
            return FlowDirction;
        }
        //汇流累积量构造函数
        public int[][] flow_cumulative(int[][] direction){

            int[][]result=Accumulation(direction);
            System.out.println("累积流计算结果:");
            for (int[] ints : result) {
                for (int j = 0; j < result[0].length; j++) {
                    System.out.printf("%3d", ints[j]);
                    if (j == result[0].length - 1) {
                        System.out.print("\n");
                    }
                }
            }
            System.out.print("\n");
            return result;
        }

        //流向图绘制函数
        public void flow_diagram(int[][]direction){
        //绘制面板
        class DrawPanel extends JPanel {

            private BasicStroke lineStroke;//stroke属性控制线条的宽度、笔形样式、线段连接方式或短划线图案 属性设置需先创建BS对象

            public DrawPanel() {//构造函数
                    lineStroke = new BasicStroke(2.0f);
            }

            @Override//方法重写
            protected void paintComponent(Graphics g) {//使用Graphics2D类画图形 通过对pC方法重写 强制把对象g转换成Graphics2D
                draw((Graphics2D) g);
            }

           //主绘制函数
            private void draw(Graphics2D g2d) {

                Line2D.Double line2D = null;
                Test_Arrow.Arrow.Attributes arrowAttributes = null;

                //绘制网格
                for ( int x = cell_corner[0]; x <= cell_corner[0]+grid_size*(grid_col-1); x += grid_size ) {

                    for (int y = cell_corner[1]; y <= cell_corner[1] + grid_size * (grid_row - 1); y += grid_size){

                        g2d.drawRect(x, y, grid_size, grid_size);
                }
                }

                for(int i=0;i<direction.length;i++) {//根据流向绘制流向图
                    for (int j = 0; j < direction[0].length; j++) {
                        int[]coordinate=head_tail(i,j,direction[i][j],grid_size,cell_corner);
                        if(direction[i][j]==0){//汇点用圆圈替代
                            //int x0=cell_corner[0]+grid_size/2+j*grid_size;
                            int x0=cell_corner[0]+j*grid_size;
                            //int y0=cell_corner[0]+grid_size/2+i*grid_size;
                            int y0=cell_corner[0]+i*grid_size;
                            g2d.drawOval(x0,y0,grid_size,grid_size);//x0,y0为圆圈左上角坐标
                        }
                        else {
                            // 绘制线的“方向1”箭头
                            line2D = new Line2D.Double(coordinate[0], coordinate[1], coordinate[2], coordinate[3]);//起点坐标，终点坐标//西北
                            arrowAttributes = new Test_Arrow.Arrow.Attributes();
                            arrowAttributes.angle = 45;
                            arrowAttributes.height = 45;
                            drawLineArrowDirection1(g2d, arrowAttributes, line2D);
                        }
                    }
                }
                }

                //绘制箭头 arrowAttributes为箭头属性
                private void drawLineArrowDirection1(Graphics2D g2d, Test_Arrow.Arrow.Attributes arrowAttributes, Line2D.Double line2D) {
                    drawLine(g2d, line2D);
                    drawArrow(g2d, arrowAttributes, line2D.getP1(), line2D.getP2());
                }

                //绘制线
                private void drawLine(Graphics2D g2d, Line2D.Double line2D) {
                    g2d.setColor(Color.BLACK);
                    g2d.setStroke(lineStroke);//通过setStroke()方法设置属性
                    g2d.draw(line2D);
                }

                //绘制箭头
                private void drawArrow(Graphics2D g2d, Test_Arrow.Arrow.Attributes arrowAttributes, Point2D point1, Point2D point2) {
                    // 获取Arrow实例
                    Test_Arrow.Arrow arrow = getArrow(arrowAttributes, point1, point2);

                    // 构建GeneralPath
                    GeneralPath arrow2D = new GeneralPath();
                    arrow2D.moveTo(arrow.point1.x, arrow.point1.y);
                    arrow2D.lineTo(arrow.point2.x, arrow.point2.y);
                    arrow2D.lineTo(arrow.point3.x, arrow.point3.y);
                    arrow2D.closePath();

                    // 绘制
                    g2d.setColor(arrow.attributes.color);
                    g2d.fill(arrow2D);
                }

                //获取箭头实体类
                private Test_Arrow.Arrow getArrow(Test_Arrow.Arrow.Attributes arrowAttributes, Point2D point1, Point2D point2) {
                    Test_Arrow.Arrow arrow = new Test_Arrow.Arrow(arrowAttributes);

                    // 计算斜边
                    double hypotenuse = arrow.attributes.height / Math.cos(Math.toRadians(arrow.attributes.angle / 2));

                    // 计算当前线所在的象限
                    int quadrant = -1;
                    if (point1.getX() > point2.getX() && point1.getY() < point2.getY()) {
                        quadrant = 1;
                    } else if (point1.getX() < point2.getX() && point1.getY() < point2.getY()) {
                        quadrant = 2;
                    } else if (point1.getX() < point2.getX() && point1.getY() > point2.getY()) {
                        quadrant = 3;
                    } else if (point1.getX() > point2.getX() && point1.getY() > point2.getY()) {
                        quadrant = 4;
                    }

                    // 计算线的夹角
                    double linAngle = getLineAngle(point1.getX(), point1.getY(), point2.getX(), point2.getY());
                    if (Double.isNaN(linAngle)) {
                        // 线与x轴垂直
                        if (point1.getX() == point2.getX()) {
                            if (point1.getY() < point2.getY()) {
                                linAngle = 90;
                            } else {
                                linAngle = 270;
                            }
                            quadrant = 2;
                        }
                    }
                    // 线与y轴垂直
                    else if (linAngle == 0) {
                        if (point1.getY() == point2.getY()) {
                            if (point1.getX() < point2.getX()) {
                                linAngle = 0;
                            } else {
                                linAngle = 180;
                            }
                            quadrant = 2;
                        }
                    }

                    // 上侧一半箭头
                    double xAngle = linAngle - arrow.attributes.angle / 2; // 与x轴夹角
                    double py0 = hypotenuse * Math.sin(Math.toRadians(xAngle)); // 计算y方向增量
                    double px0 = hypotenuse * Math.cos(Math.toRadians(xAngle)); // 计算x方向增量

                    // 下侧一半箭头
                    double yAngle = 90 - linAngle - arrow.attributes.angle / 2; // 与y轴夹角
                    double px1 = hypotenuse * Math.sin(Math.toRadians(yAngle));
                    double py1 = hypotenuse * Math.cos(Math.toRadians(yAngle));

                    // 第一象限
                    if (quadrant == 1) {
                        px0 = -px0;
                        px1 = -px1;

                    } else if (quadrant == 2) {
                        // do nothing
                    } else if (quadrant == 3) {
                        py0 = -py0;
                        py1 = -py1;

                    } else if (quadrant == 4) {
                        py0 = -py0;
                        px0 = -px0;

                        px1 = -px1;
                        py1 = -py1;
                    }

                    // build
                    arrow.point1 = new Point2D.Double();
                    arrow.point1.x = point1.getX();
                    arrow.point1.y = point1.getY();

                    arrow.point2 = new Point2D.Double();
                    arrow.point2.x = point1.getX() + px0;
                    arrow.point2.y = point1.getY() + py0;

                    arrow.point3 = new Point2D.Double();
                    arrow.point3.x = point1.getX() + px1;
                    arrow.point3.y = point1.getY() + py1;

                    return arrow;
                }

                //获取线与X轴夹角
                protected double getLineAngle(double x1, double y1, double x2, double y2) {
                    double k1 = (y2 - y1) / (x2 - x1);
                    double k2 = 0;
                    return Math.abs(Math.toDegrees(Math.atan((k2 - k1) / (1 + k1 * k2))));
                }
            }


            //箭头实体类
            class Arrow {
                Test_Arrow.Arrow.Attributes attributes;
                Point2D.Double point1;
                Point2D.Double point2;
                Point2D.Double point3;

                public Arrow(Test_Arrow.Arrow.Attributes attributes) {
                    this.attributes = attributes;
                }

                //箭头属性
                class Attributes {
                    double height; // 箭头的高度
                    double angle; // 箭头角度
                    Color color; // 箭头颜色

                    public Attributes() {
                        this.height = 60;
                        this.angle = 30;
                        this.color = Color.BLACK;
                    }
                }
            }
            JFrame frame = new JFrame();
            frame.setTitle("流向图");
            Dimension dimension = new Dimension(2*cell_corner[1]+grid_size*grid_col, 2*cell_corner[0]+grid_size*grid_row);
            frame.setSize(dimension);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setContentPane(new DrawPanel());
            frame.setVisible(true);
        }
    }
    public static void main(String []args) {//主函数
        ArrayList<List<Integer>> DEM = new ArrayList<>();//利用不需预先知道尺寸的ArrayList存储DEM数据
        //此处-9999为输入数据中的无效数据

        DEM.add(Arrays.asList(78,72,69,71,58,49));//测试数据 更改数据只需新增DEM.add行或者在已有数据中直接更改
        DEM.add(Arrays.asList(74,67,56,49,46,50));
        DEM.add(Arrays.asList(69,53,44,37,38,48));
        DEM.add(Arrays.asList(64,58,55,22,31,24));
        DEM.add(Arrays.asList(68,61,47,21,16,19));
        DEM.add(Arrays.asList(74,53,34,12,11,12));
        int[]corner={200,200};
        double[][] station= {{245,354,400.23},{438,590,230.48},{320,447,179.52}};//设置三个站点x,y,precipitation
        grid d= new grid(6,6,120,corner,DEM,3,station);
        //int[][]direction={{78,72,69,71,58,49},{74,67,56,49,46,50},{69,53,44,37,38,48},{64,58,55,22,31,24},{68,61,47,21,16,19},{74,53,34,12,11,12}};
        //int[][]direction={{2,2,2,4,4,8},{2,2,2,4,4,8},{1,1,2,4,8,4},{128,128,1,2,4,8},{2,2,1,4,4,4},{1,1,1,1,4,16}};
        //int[][]direction={{4,2,2,2,4},{2,1,1,2,4},{2,2,2,2,4},{1,1,1,2,4},{128,128,128,1,0}};
        //泰森多边形插值
        d.interpolation_Vorono();
        //反距离权重法插值
        d.interpolation_InvstDistWght();
        //计算流向
        int[][] D=d.flow_direction();//存储流向数据
        //计算累积流量
        int[][] result=d.flow_cumulative(D);
        //绘制流向图
        d.flow_diagram(D);
    }

}
