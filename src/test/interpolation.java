package test;
import java.util.Scanner;

public class interpolation {
    static double interplate(double a,double a1,double a2,double b1,double b2){
        double r=0.0;
        r=b1+(a-a1)/(a2-a1)*(b2-b1);
        return r;
    }

    public static void main(String []args){
        /*double z=36.4;
        if(z<36){
            System.out.println("z越界");
        }
        else if(z>=36 && z<36.5){
            double v=36+(0-0)/(22.5-0)*(4800-4330);
        }*/
        //double z_test=35;
        System.out.println("please input Z value:");
        Scanner s=new Scanner(System.in);
        double z_test=0.0;
        if(s.hasNextDouble()) {
            z_test=s.nextDouble();
            s.close();
        }else{
            System.out.print("ERROR!");
            System.exit(0);
        }

        double z[]={36,36.5,37,37.5,38,38.5,39,39.5,40,40.5};
        double v[]={4330,4800,5310,5860,6450,7080,7760,8540,9420,10250};
        double q[]={0,22.5,55,105,173.9,267.2,378.3,501.9,638.9,786.1};
        if(z_test<z[0]||z_test>z[9]){
            System.out.println("The input of your z is illegal!");
        }
        else {
            for (int i = 0; i < 9; i++) {
                if (z_test >= z[i] && z_test < z[i + 1]) {
                    double v_test = interplate(z_test, z[i], z[i + 1], v[i], v[i + 1]);
                    double q_test = interplate(z_test, z[i], z[i + 1], q[i], q[i + 1]);
                    System.out.println("The value of v is " + v_test);
                    System.out.println("The value of q is " + q_test);
                }
            }
        }
    }
}
