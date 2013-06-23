package txl.contact.po;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import txl.config.TxlConstants;

public class PhoneContact {
	private int contactId;
	private String name;
	private String photo;
	private String note;
	private String website;
	private String birthday;
	private String nickname;
	private String customRingtone;
	private String syncId;
	private int storeType;
	private String groupIds;
	
	public String getGroupIds() {
		return groupIds;
	}


	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}


	private Map<Integer,ArrayList<PhoneContactData>> dataTotalTypeMap = new HashMap<Integer, ArrayList<PhoneContactData>>();
	
	/*用于备份*/
	private List<PhoneContactData>  phoneContactDatas= new ArrayList<PhoneContactData>();
	
	public PhoneContact(){
		dataTotalTypeMap.put(TxlConstants.CONTACT_DATA_TOATAL_TYPE_TEL, new ArrayList<PhoneContactData>());
		dataTotalTypeMap.put(TxlConstants.CONTACT_DATA_TOATAL_TYPE_EMAIL, new ArrayList<PhoneContactData>());
		dataTotalTypeMap.put(TxlConstants.CONTACT_DATA_TOATAL_TYPE_ORG, new ArrayList<PhoneContactData>());
		dataTotalTypeMap.put(TxlConstants.CONTACT_DATA_TOATAL_TYPE_ADDRESS, new ArrayList<PhoneContactData>());
		dataTotalTypeMap.put(TxlConstants.CONTACT_DATA_TOATAL_TYPE_IM, new ArrayList<PhoneContactData>());
	}
	/**
	 * 将联系人转换为json
	 * 除了头像
	 * @return
	 */
	public String toJSONStringForBackUp(){
		PhoneContact contact = this;
		JSONObject contactJobj = new JSONObject();
		try{
			contactJobj.put("name", contact.getName());
			//contactJobj.put("photo", contact.getPhoto());
			contactJobj.put("website", contact.getName());
			contactJobj.put("birthday", contact.getBirthday());
			contactJobj.put("nickname", contact.getNickname());
			contactJobj.put("customRingtone", contact.getCustomRingtone());
			contactJobj.put("groupIds", contact.getGroupIds());
			contactJobj.put("storeType", contact.getStoreType());
			contactJobj.put("syncId", contact.getSyncId());
			
			JSONArray dataArray = new JSONArray();
			
			for(PhoneContactData data:contact.getPhoneContactDatas()){
				JSONObject dataObj = new JSONObject();
				dataObj.put("dataCategoryId", data.getDataCategoryId());
				dataObj.put("dataValue", data.getDataValue());
				if(data.getDataCategoryId()==TxlConstants.CONTACT_DATA_TYPE_CUSTOM){
					dataObj.put("dataCustomLabel", data.getDataCustomLabel());
				}
				dataObj.put("dataTotalType", data.getDataTotalType());
				dataArray.put(dataObj);
			}
			contactJobj.put("datas", dataArray);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return contactJobj.toString();
	}
	
	public String getBirthday() {
		return birthday;
	}
	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}
	public int getContactId() {
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public String getCustomRingtone() {
		return customRingtone;
	}
	public void setCustomRingtone(String customRingtone) {
		this.customRingtone = customRingtone;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
	public String getSyncId() {
		return syncId;
	}
	public void setSyncId(String syncId) {
		this.syncId = syncId;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}
	public int getStoreType() {
		return storeType;
	}
	public void setStoreType(int storeType) {
		this.storeType = storeType;
	}
	public Map<Integer, ArrayList<PhoneContactData>> getDataTotalTypeMap() {
		return dataTotalTypeMap;
	}
	public void setDataTotalTypeMap(
			Map<Integer, ArrayList<PhoneContactData>> dataTotalTypeMap) {
		this.dataTotalTypeMap = dataTotalTypeMap;
	}


	public List<PhoneContactData> getPhoneContactDatas() {
		return phoneContactDatas;
	}


	public void setPhoneContactDatas(List<PhoneContactData> phoneContactDatas) {
		this.phoneContactDatas = phoneContactDatas;
	}
}
