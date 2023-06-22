package cn.xihoway;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        File dir = new File("");
        String path = dir.getAbsolutePath();
        System.out.println(path);
        String goalPath = path.substring(0,path.lastIndexOf("\\"));
        System.out.println(goalPath);
        Scanner in = new Scanner(System.in);
        System.out.print("输入项目名（默认untitled）:");
        String name = in.nextLine();
        if("".equals(name)){
            name = "untitled";
        }
        System.out.print("输入域（默认org）：");
        String field = in.nextLine();
        if("".equals(field)){
            field = "org";
        }
        System.out.print("输入公司名称（默认example）：");
        String company = in.nextLine();
        if("".equals(company)){
            company = "example";
        }
        //System.out.print("name:"+name+" filed:"+field+" company:"+company);
        String goalDir = goalPath + "\\" + name;
        File newDir = new File(goalDir);
        if(newDir.exists()){
            System.out.println("程序已存在！");
        }else {
            dir = new File(path);
            Main.readDir(dir,goalPath,name,field,company);
        }
        System.out.println("SUCCESS");
    }

    /**
     *
     * @param dir 当前目录文件
     * @param goalDir 目标copy地址
     * @param name 程序名
     */
    private static void readDir(File dir,String goalDir,String name,String filed,String company){
        String goalPath;
        if(dir.getName().contains("xhoway")){
            goalPath = goalDir + "\\" + dir.getName().replaceAll("xhoway",name);
        }else if("cn".equals(dir.getName())){
            goalPath = goalDir + "\\" + filed;
        }else if("xihoway".equals(dir.getName())){
            goalPath = goalDir + "\\" + company;
        } else {
            goalPath = goalDir + "\\" + dir.getName();
        }
        File goalFile = new File(goalPath);
        if(!goalFile.exists()){
            goalFile.mkdir();
        }
        File[] listFiles = dir.listFiles();
        assert listFiles != null;
        for (File f:listFiles){
            if(f.isDirectory()){
                if(".idea".equals(f.getName()) || "target".equals(f.getName()) || ".git".equals(f.getName())){
                    continue;
                }else{
                    readDir(f,goalPath,name,filed,company);
                }
            }else {
                if(!"Main.java".equals(f.getName())){
                    readFile(f,goalPath+"\\"+f.getName().replaceAll("xhoway",name),name,filed+"."+company);
                }
            }
        }
    }

    private static void readFile(File file,String goalDir,String name,String groupId){
        try {
            //创建一个输入流对象
            InputStream is = new FileInputStream(file.getAbsolutePath());
            FileOutputStream out = new FileOutputStream(goalDir);
            //定义一个缓冲区
            byte[] bytes = new byte[1024];// 1kb
            //通过输入流使用read方法读取数据
            int len = 0;
            len = is.read(bytes);
            //System.out.println("字节数:"+len);
            String str;
            while(len!=-1){
                //把数据转换为字符串
                str = new String(bytes, 0, len);
                str = str.replaceAll("xhoway",name);
                str = str.replaceAll("cn.xihoway",groupId);
                out.write(str.getBytes(StandardCharsets.UTF_8));
                //继续进行读取
                len = is.read(bytes);
                //释放资源
                //is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
