package txl.socket.po;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class User implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
    public Integer getUserId()
    {
        return userId;
    }
    
    public void setUserId(Integer userId)
    {
        this.userId = userId;
    }
    
    public String getUserName()
    {
        return userName;
    }
    
    public void setUserName(String userName)
    {
        this.userName = userName;
    }
    
    public String getUserPhone()
    {
        return userPhone;
    }
    
    public void setUserPhone(String userPhone)
    {
        this.userPhone = userPhone;
    }
    
    public Integer getCompId()
    {
        return compId;
    }
    
    public void setCompId(Integer compId)
    {
        this.compId = compId;
    }
    private Integer userId;
	private String userName;
	private String userPhone;
	private Integer compId;
	  
	
}
