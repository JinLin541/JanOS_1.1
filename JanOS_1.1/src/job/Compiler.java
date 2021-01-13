package job;

import memory.Page;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
/*
出现BUG。。。。。
 */
public class Compiler {
    public static void compiler(File file) throws IOException {
        int priority = 0;
        int countFunctionNumber = 0;
        Map<String,Integer> functionSignature = new LinkedHashMap<>();
        //数据从数据区的起始部分开始写
        int countDataLocation = 0;
        ArrayList<Number> arrayListNumber = new ArrayList<>();
        Map<String,LinkedList<String>> areaContent = new LinkedHashMap<>();
        FileWriter fileWriter = new FileWriter(file);
        StringBuffer stringBuffer = new StringBuffer();
        Scanner scanner = new Scanner(file);
        boolean entry = false;
        if(scanner.hasNext()){
            String buff = scanner.nextLine();
            if(buff.contains("priority")){
                String[] buff2 = buff.split(" ");
                priority = Integer.parseInt(buff2[1]);
            }else if(buff.contains("main") || buff.contains("function")){
                String[] buff2 = buff.split(" ");
                String name = buff2[1];
                entry = true;
                //分配方法签名
                functionSignature.put(name,countFunctionNumber);
                countFunctionNumber++;
                areaContent.put(name,new LinkedList<>());
            }else if (buff.contains("}")){
                entry = false;
            }

        }
    }

}
