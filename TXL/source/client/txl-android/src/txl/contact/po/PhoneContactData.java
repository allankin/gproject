package txl.contact.po;

public class PhoneContactData {
	private int dataId;
	private int contactId;
	private int dataCategoryId;
	private String dataValue;
	private String dataCustomLabel;
	private int dataTotalType;
	private int syncId;
	
	
	public int getContactId() {
		return contactId;
	}
	public void setContactId(int contactId) {
		this.contactId = contactId;
	}
	public int getDataCategoryId() {
		return dataCategoryId;
	}
	public void setDataCategoryId(int dataCategoryId) {
		this.dataCategoryId = dataCategoryId;
	}
	public String getDataCustomLabel() {
		return dataCustomLabel;
	}
	public void setDataCustomLabel(String dataCustomLabel) {
		this.dataCustomLabel = dataCustomLabel;
	}
	public int getDataId() {
		return dataId;
	}
	public void setDataId(int dataId) {
		this.dataId = dataId;
	}
	public int getDataTotalType() {
		return dataTotalType;
	}
	public void setDataTotalType(int dataTotalType) {
		this.dataTotalType = dataTotalType;
	}
	public String getDataValue() {
		return dataValue;
	}
	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}
	public int getSyncId() {
		return syncId;
	}
	public void setSyncId(int syncId) {
		this.syncId = syncId;
	}
}
