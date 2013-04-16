package txl.message.pushmessage.po;

import org.json.JSONObject;


/**
 * @ClassName:  BillDataResponse.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-2-25 下午3:30:39
 * @Copyright: 版权由 HundSun 拥有
 */
public class BillData extends PushInfo
{
    
    public Bill bill=new Bill();
    
    public class Bill
    {
        public String billId;
        public String name;
        public String addr;
        public String phone;
        public String billType;
        public String billContent;
    }
    
    public void parseJSONObject(JSONObject jobj){
        JSONObject billJobj = jobj.optJSONObject("details");
        this.bill.billId = billJobj.optString("billId");
        this.bill.name = billJobj.optString("name");
        this.bill.addr = billJobj.optString("addr");
        this.bill.phone = billJobj.optString("phone");
        this.bill.billType = billJobj.optString("billType");
        this.bill.billContent = billJobj.optString("billContent");
        
        this.userName = jobj.optString("userName");
        this.url = jobj.optString("url");
        this.comId = jobj.optInt("comId");
        this.uuId = jobj.optString("uuId");
        StringBuilder sb = new StringBuilder();
        this.content = 
                sb
                .append("姓名:"+this.bill.name)
                .append("地址:"+this.bill.addr)
                .append("手机:"+this.bill.phone)
                .append("类型:"+this.bill.billType)
                .append("内容:"+this.bill.billContent)
                .toString();
        
    }
}
