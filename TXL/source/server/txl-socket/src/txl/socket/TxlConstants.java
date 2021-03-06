package txl.socket;

/**
 * @ClassName:  TxlConstants.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-19 下午6:33:01
 */
public class TxlConstants
{
	/**
	 * 
	 * 192.168.92.61
	 * 192.168.2.100
	 * 
	 * 
	 */
    public static final String HOST = "localhost";
    
    public static final int SOCKET_PORT = 8888;
    
    public static final int RMI_PORT = 6667;
    
    
    public static final int SEND_STATUS_SUCCESS = 1;
    public static final int SEND_STATUS_USER_NOT_EXIST = 2;
    public static final int SEND_STATUS_FAIL = 3;
    
    public static final int MESSAGE_TYPE_PLAIN_TEXT  = -1;
}
