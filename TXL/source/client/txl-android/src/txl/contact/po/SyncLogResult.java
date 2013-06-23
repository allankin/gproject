package txl.contact.po;

import java.util.List;

public class SyncLogResult {
	private int status;
	private List<SyncLog> logs;
	public List<SyncLog> getLogs() {
		return logs;
	}
	public void setLogs(List<SyncLog> logs) {
		this.logs = logs;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
}