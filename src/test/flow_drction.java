/*
学号：20307011
姓名：林浩媚
班级：20级地信班
作业简述：本次Java编程作业要求基于地形输入数据确定地形单元的流向情况。本作业实现了利用文件输入输出流导入地形数据，并且允许输入数据为非标准矩形数据，
即输入数据边界可存在部分不规则NA无效数据点，利用边缘填充的方法以及使用ArrayList在不事先知道数据的情况下实现上述流向确定功能。
注：在文件读取函数中，本代码使用包内相对路径，在作业压缩包中附上数据，如有必要须更改数据路径。
 */
package test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class flow_drction {
    //该函数实现通过输入文件读取地形信息
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
                //将读取的str按照","分割，用字符串数组来接收
                String[] strs = str.split("\\,");
                for(int i = 0; i < columnNum; i++) {
                    //array[count][i] = Integer.valueOf(strs[i]);
                    temp.add(Integer.valueOf(strs[i]));
                }
                data.add(temp);
                //将行数 + 1
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
    //该函数作用是输入当前单元 计算八邻域最小值并返回中心单元对应流向数值
    static int flowDirect(ArrayList<List<Integer>> l,int i,int j){//i,j为中心单元坐标
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
    public static void main(String []args) {
        ArrayList<List<Integer>> DEM = new ArrayList<>();//利用不需预先知道尺寸的ArrayList存储数据
        readArray(DEM,"src\\test.csv");//数据中的-9999为无效数据//此处输入 src\test.csv 为包内相对路径

        //此处-9999为输入数据中的无效数据
        /*
        DEM.add(Arrays.asList(-9999,382,358,338,281));//测试数据 更改数据只需新增DEM.add行或者在已有数据中直接更改
        DEM.add(Arrays.asList(360,384,329,298,276));
        DEM.add(Arrays.asList(369,355,332,318,-9999));
        DEM.add(Arrays.asList(358,350,312,274,270));
        DEM.add(Arrays.asList(374,384,364,350,235));*/

        //对数据进行NoData值(-1)的填充，以保证每一个数据单元周围均有八邻域
        ArrayList<List<Integer>> DEMPad = new ArrayList<List<Integer>>();//存储填充数据，同样使用不需预先知道尺寸的ArrayList
        for(int i=0;i<DEM.size()+2;i++){//基于输入DEM数据的行尺寸进行循环和填充。对已有数据最上和最下行进行填充故+2
            List temp= new ArrayList();
            //int[] temp=new int[DEM.get(0).size()+2];
            for(int j=0;j<DEM.get(0).size()+2;j++){//基于输入DEM数据的列尺寸进行循环和填充。对已有数据最左和最右列进行填充故+2
                if(i==0||j==0||i==DEM.size()+1||j==DEM.get(0).size()+1){
                    temp.add(-1);//在数据外围加上-1 padding
                }
                else{
                    temp.add(DEM.get(i-1).get(j-1));//非填充部分进行原始数据的复制
                    //temp[j]=0;
                }
            }
            //List templist=Arrays.asList(temp);
            DEMPad.add(temp);//以行为单位构成填充后ArrayList
        }
        //int[] Index=new int[2];//记录八单元最小值索引
        int[][] FlowDirction=new int[DEM.size()][DEM.get(0).size()];//根据输入数据尺寸初始化 流向数据数组
        for(int a=0;a<DEM.size()+2;a++){
            for(int b=0;b<DEM.get(0).size()+2;b++){//调用函数赋值数据流向
                /*System.out.printf("%6d",DEMPad.get(a).get(b));
                if(b==DEMPad.get(0).size()-1)
                    System.out.print("\n");*/
                if(DEMPad.get(a).get(b)!=-1){//此处避开了填充的NA
                    if(DEMPad.get(a).get(b)==-9999){//此处将输入数据的无效数据(-9999)处流向值赋值为-1
                        FlowDirction[a-1][b-1]=-1;
                    }
                    else {
                        FlowDirction[a - 1][b - 1] = flowDirect(DEMPad, a, b);
                    }
                }
            }
        }
        //以一定格式输出输入数据
        System.out.println("输入数据为");
        for(int x=0;x<DEM.size();x++){
            for(int y=0;y<DEM.get(0).size();y++) {
                System.out.printf("%5d ",DEM.get(x).get(y));
                if(y==DEM.get(0).size()-1)
                    System.out.print("\n");
            }
        }
        //以一定格式输出填充后数据
        System.out.println("填充后输入数据为");
        for(int xx=0;xx<DEMPad.size();xx++){
            for(int yy=0;yy<DEMPad.get(0).size();yy++) {
                System.out.printf("%5d ",DEMPad.get(xx).get(yy));
                if(yy==DEMPad.get(0).size()-1)
                    System.out.print("\n");
            }
        }
        //以一定格式输出流向结果
        System.out.println("流向数据为");
        for(int m=0;m<DEM.size();m++){
            for(int n=0;n<DEM.get(0).size();n++) {
                System.out.printf("%4d ",FlowDirction[m][n]);
                if(n==DEM.get(0).size()-1)
                    System.out.print("\n");
            }
        }
    }
}
