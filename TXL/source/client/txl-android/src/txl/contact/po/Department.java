package txl.contact.po;

import java.io.Serializable;
import java.util.ArrayList;

import txl.test.tree.TreeElement;

/**
 * @ClassName:  Depart.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-4-10 下午3:09:54
 */
public class Department implements Serializable
{
    private static final long serialVersionUID = 1L;
    public int depId;
    public String depName;
    public int depParentId;
    public int compId;
    public int employeeNum;
    
    
    
    /*********************** 树形显示 变量 *****************************/
    public boolean mhasParent;  
    public boolean mhasChild;
    public Department parent;  
    public int level;  
    public ArrayList<Department> childList = new ArrayList<Department>();
    public boolean expanded;  
    public void addChild(Department c) {  
        this.childList.add(c);  
        this.mhasParent = false;  
        this.mhasChild = true;  
        c.parent = this;  
        c.level = this.level + 1;  
  
    }
    public Department(){
        
    }
    public Department(int depId, String depName) {  
        super();  
        this.depId = depId;  
        this.depName = depName;  
        this.level = 0;  
        this.mhasParent = true;  
        this.mhasChild = false;  
        this.parent = null;  
    }  
}
