class showThread extends Thread{
    public void run(){
        System.out.println("执行两个不同功能的多线程程序");
    }
}
public class createThread extends Thread{
    public void run(){
            System.out.println("第一个多线程程序");
    }
    public static void main(String[] args){
            createThread myThread1=new createThread();
            showThread myThread2=new showThread();
            myThread1.start();
            myThread2.start();
    }
}

