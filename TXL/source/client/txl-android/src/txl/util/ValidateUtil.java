package txl.util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @ClassName:  ValidateUtil.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-9 上午11:53:51
 */
public class ValidateUtil
{
    public static boolean isPhoneNumber(String phone){
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$"); 
        Matcher m = p.matcher(phone); 
        return m.matches(); 
    }
    
}