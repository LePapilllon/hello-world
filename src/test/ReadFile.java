package test;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReadFile {
    private File file;
    private BufferedReader reader;
    private InputStream is;
    private InputStreamReader isReader;
    public ReadFile(){}
    public ReadFile(String filePath){
        file=new File(filePath);
        initReader();
    }
    private void initReader(){
        if(file!=null&&file.isFile()&&file.canRead()){
            try{
                is=new FileInputStream(file);
                isReader=new InputStreamReader(is);
                reader=new BufferedReader(isReader);
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    public List parse(){
        List lstNumber=null;
        String txtLine=null;
        String splitRegexp="([0-9]+)\\s?";
        Pattern pattern=Pattern.compile(splitRegexp);
        Matcher matcher=null;
        //读取文件
        if(reader!=null){
            lstNumber=new ArrayList();
            try{
                while((txtLine=reader.readLine())!=null){
                    matcher=pattern.matcher(txtLine);
                    while(matcher.find()){
                        lstNumber.add(Integer.valueOf(matcher.group(1)));
                    }
                }
            }catch(IOException e){
                e.printStackTrace();
            }finally {
                try{
                    reader.close();
                    isReader.close();
                    is.close();
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
            }
        return lstNumber;
        }
        public static void main(String[] args){
        ReadFile File=new ReadFile("D:\\test.txt");
        System.out.println(File.parse());
        }
    }

