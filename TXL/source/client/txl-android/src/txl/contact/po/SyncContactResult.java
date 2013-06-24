package txl.contact.po;
public class SyncContactResult {
	private int status;
	private PhoneContact contact;
	 
	public PhoneContact getContact() {
		return contact;
	}
	public void setContact(PhoneContact contact) {
		this.contact = contact;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}