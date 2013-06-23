package txl.contact.po;

import java.util.Date;

public class SyncLog {
	private String syncId;
	private int userId;
	private Date createTime;
	private Boolean status;
	private int action;
	private int backupSuccessCount;
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public Date getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	public String getSyncId() {
		return syncId;
	}
	public void setSyncId(String syncId) {
		this.syncId = syncId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getBackupSuccessCount() {
		return backupSuccessCount;
	}
	public void setBackupSuccessCount(int backupSuccessCount) {
		this.backupSuccessCount = backupSuccessCount;
	}
	
}