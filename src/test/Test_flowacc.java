package test;

public class Test_flowacc {
    static public double[][] Accumulation(int[][] Direction,double[][] weight)
    {
        int i, j;
        double[][] result = new double[Direction.length][Direction[0].length];
        boolean[][] Origin = CalOrigin(Direction);//计算是否是水流源点
        boolean[][] OutFlow = CalOut(Direction);//计算是否是水流出点
        for (i = 0; i < Direction.length; i++)
        {
            for (j = 0; j < Direction[0].length; j++)
            {
                if (OutFlow[i][j])
                {
                    Add(Direction, Origin, result, i, j,weight);//累加函数，采用回溯法
                }
            }
        }
        return result;
    }
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
        /*System.out.print("CalOut flag\n");
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
    static private double Add(int[][] Direction, boolean[][] Origin, double[][] Result, int i, int j,double[][] weight)//利用回溯法计算累积汇流量
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
                Result[i][j] += Add(Direction, Origin, Result, i, j - 1, weight);//求取流入本单元邻域单元的初始累积量
                Result[i][j]+=weight[i][j-1];//每一个符合条件的单元格汇聚到洼地均需加上自身的单位流量1
            }
        }
        if (i - 1 >= 0 && j - 1 >= 0)
        {
            if (Direction[i - 1][j - 1] == 2)
            {
                Result[i][j] += Add(Direction, Origin, Result, i - 1, j - 1,weight);
                Result[i][j]+=weight[i-1][j-1];
            }
        }
        if (i - 1 >= 0)
        {
            if (Direction[i - 1][j] == 4)
            {

                Result[i][j] += Add(Direction, Origin, Result, i - 1, j,weight);
                Result[i][j]+=weight[i-1][j];
            }
        }
        if (i - 1 >= 0 && j + 1 < Direction[0].length)
        {
            if (Direction[i - 1][j + 1] == 8)
            {
                Result[i][j] += Add(Direction, Origin, Result, i - 1, j + 1,weight);
                Result[i][j]+=weight[i-1][j+1];
            }
        }
        if (j + 1 < Direction[0].length)
        {
            if (Direction[i][j + 1] == 16)
            {
                Result[i][j] += Add(Direction, Origin, Result, i, j + 1, weight);
                Result[i][j]+=weight[i][j+1];
            }
        }
        if (i + 1 < Direction.length && j + 1 < Direction[0].length)
        {
            if (Direction[i + 1][j + 1] == 32)
            {
                Result[i][j] += Add(Direction, Origin, Result, i + 1, j + 1, weight);
                Result[i][j]+=weight[i+1][j+1];
            }
        }
        if (i + 1 < Direction.length)
        {
            if (Direction[i + 1][j] == 64)
            {
                Result[i][j] += Add(Direction, Origin, Result, i + 1, j,weight);
                Result[i][j]+=weight[i+1][j];
            }
        }
        if (i + 1 < Direction.length && j - 1 >= 0)
        {
            if (Direction[i + 1][j - 1] == 128)
            {
                Result[i][j] += Add(Direction, Origin, Result, i + 1, j - 1,weight);
                Result[i][j]+=weight[i+1][j-1];
            }
        }
        return Result[i][j];
    }
    public static void main(String []args) {
        //int[][]direction={{78,72,69,71,58,49},{74,67,56,49,46,50},{69,53,44,37,38,48},{64,58,55,22,31,24},{68,61,47,21,16,19},{74,53,34,12,11,12}};
        int[][]direction={{2,2,2,4,4,8},{2,2,2,4,4,8},{1,1,2,4,8,4},{128,128,1,2,4,8},{2,2,1,4,4,4},{1,1,1,1,4,16}};
        //int[][]direction={{4,2,2,2,4},{2,1,1,2,4},{2,2,2,2,4},{1,1,1,2,4},{128,128,128,1,2}};
        double[][]weight={{1,1,1,1,1,1},{1,1,1,1,1,1},{1,1,1,1,1,1},{1,1,1,2,1,1},{1,1,1,1,1,1},{1,1,1,1,1,1}};
        double[][] result=Accumulation(direction,weight);
        //System.out.println(direction.length);
        //System.out.println(direction[0].length);
        for(int i=0;i<6;i++) {
            for(int j=0;j<6;j++) {
                System.out.printf("%3.0f", result[i][j]);//.前表示占的位数 后表示小数位数
                if(j==5){
                    System.out.print("\n");
                }
            }
        }
    }
}
