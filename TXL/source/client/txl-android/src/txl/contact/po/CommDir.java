package txl.contact.po;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName:  CommDir.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-10 下午2:59:31
 */
public class CommDir
{
    public int dirId; 
    /*通讯录名字,若type为1，则为公司名称。*/
    public String name;
    /*通讯录类型，1：公司  2：共享  3：个人*/
    public int type;
    
    /*联系人人数*/
    public int userCount;
    
    /*公司通讯录，有部门list*/
    public List<Department> departList;
    
    public int accessRight;
    public int joinRight; 
}
