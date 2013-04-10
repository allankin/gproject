package txl.contact.po;

/**
 * @ClassName:  User.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-10 下午4:10:45
 */
public abstract class User
{
	/*
	 * userId， 
	 * 若实例为公司用户，则为后台系统中公司用户id。 
	 * 若实例为共享用户，则为共享系统中的用户id.
	 * */
    public int userId;
    public int dirId;
    public String name;
    public String userPhone;
    public int compId;    
}
