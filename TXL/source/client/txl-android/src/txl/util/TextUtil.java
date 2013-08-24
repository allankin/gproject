package txl.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TextUtil
{
    
    
    
    public static List<String> getTextByTagName(String content,String tagName){
        List<String> list = new ArrayList<String>();
        Pattern p = Pattern.compile("<"+tagName+">(.*?)</"+tagName+">"); 
        Matcher m = p.matcher(content); 
        while (m.find()) {
            list.add(m.group(1));
        }
        return list;
    }
    
    /** 
     * 根据网址返回网页的源码 
     * @param htmlUrl 
     * @return 
     */  
    public String getHtmlSource(String htmlUrl){  
        URL url;      
        StringBuffer sb = new StringBuffer();  
        try{  
            url = new URL(htmlUrl);  
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8"));//读取网页全部内容  
            String temp;  
            while ((temp = in.readLine()) != null)  
            {             
                sb.append(temp);  
            }  
            in.close();  
        }catch (MalformedURLException e) {  
            System.out.println("你输入的URL格式有问题！请仔细输入");  
        }catch (IOException e) {  
            e.printStackTrace();  
        }     
        return sb.toString();  
    } 
    
    /** 
     * 去掉html源码中的标签 
     * @param s 
     * @return 
     */  
    public String outTag(String s)  
    {  
        return s.replaceAll("<.*?>", "");  
    }
}
