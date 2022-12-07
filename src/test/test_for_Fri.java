package test;

public class test_for_Fri {
    public static void main(String []args){
        double[] h={1,2,3,4,5,6,7,8,9};
        double H=0;
        int i=0,m=9;
        do{
            H=H+h[i];
            i++;
        }
        while(i<m);
        H=H/(double)m;
        System.out.println(H);
    }
}
