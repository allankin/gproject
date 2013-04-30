import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.log4j.Logger;


/**
 * @ClassName:  Test.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-17 下午6:51:27
 * @Copyright: 版权由 HundSun 拥有
 */
public class Test
{
    
    public static void main(String[] args)
    {
    	System.out.println("ssssssssssss");
        String ss=null;
        try{
            ss.toString();
        }catch(Exception e){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
        }
    }
    
    public static String getExceptionTrace(Throwable e) {
        if (e != null) {
               StringWriter sw = new StringWriter();
               PrintWriter pw = new PrintWriter(sw);
               e.printStackTrace(pw);
               return sw.toString();
        }
        return "No Exception";
    }

}
