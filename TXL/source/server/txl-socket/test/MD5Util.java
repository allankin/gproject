

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName:  MD5Util.java
 * @Description: 
 * @Author JinChao
 * @Date 2012-3-1 下午3:25:20
 * @Copyright: 版权由 HundSun 拥有
 */
public class MD5Util
{
    public static String md5(String content)
    {
        return md5(content, "UTF-8");
    }
    
    public static String md5(String content,String charset){
        MessageDigest messageDigest = null; 
        try { 
            messageDigest = MessageDigest.getInstance("MD5"); 
            messageDigest.reset(); 
            messageDigest.update(content.getBytes(charset)); 
        } catch (NoSuchAlgorithmException e) { 
            System.out.println("NoSuchAlgorithmException caught!"); 
            System.exit(-1); 
        } catch (UnsupportedEncodingException e) { 
            e.printStackTrace(); 
        } 
        byte[] byteArray = messageDigest.digest(); 
        StringBuffer md5StrBuff = new StringBuffer(); 
        for (int i = 0; i < byteArray.length; i++) {             
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1) 
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i])); 
            else 
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i])); 
        } 
        return md5StrBuff.toString();
    }
}
