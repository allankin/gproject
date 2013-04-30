package txl.socket.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

/**
 * @ClassName:  Tool.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-17 下午7:09:53
 */
public class Tool
{
    public static String getExceptionTrace(Throwable e) {
        if (e != null) {
               StringWriter sw = new StringWriter();
               PrintWriter pw = new PrintWriter(sw);
               e.printStackTrace(pw);
               return sw.toString();
        }
        return "No Exception";
    }
    
    public static String genUUID(){
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
    
}
