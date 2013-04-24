package txl.contact.po;

import java.util.List;

public class ContactVo {
	/*联系人姓名*/
	public String displayName;
	public String phone;
	public long contactId;
	/*姓名大写首字母组合*/
	public String firstLetterSet;
	/*首字母*/
	public char firstLetter;
	public String sortKey;
	public List<String> phoneList;
	//public String firstChineseLetter;
}
