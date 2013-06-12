import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import org.apache.log4j.Logger;
import org.apache.log4j.MDC;


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
        try {
        	System.out.println("服务器消息内容".getBytes("UTF-8").length);
			System.out.println("{\"b\":6,\"c\":\"服务器消息内容....44\",\"m\":\"44\",\"sn\":\"sendName44\",\"s\":44}".getBytes("UTF-8").length);
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
        float d = 34/45;
        String md5 = MD5Util.md5(String.valueOf(1207114));

        System.out.println(Integer.parseInt("43%".substring(0,"43%".length()-1)));
        
        System.out.println("uuid:"+UUID.randomUUID().toString().replaceAll("-", "").length());
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
