package txl.socket.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.stereotype.Repository;

import txl.socket.po.PushMessage;

@Repository
public class ContactMessageDao extends BaseDao {
	private final Logger log = Logger.getLogger(ContactMessageDao.class);

	public boolean save(final PushMessage pushMsg, final int compId,
			final String recPhone, final String recName, final int status) {

		String sql = "insert into messag_contact_log("
				+ "user_id,comp_id,content,rec_user_id,"
				+ "rec_phone,rec_name,create_time,status,send_name) "
				+ "values(?,?,?,?," + "?,?,sysdate(),?,?)";

		int count = this.jdbcTemplate.update(sql,
				new PreparedStatementSetter() {

					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setInt(1, pushMsg.getSendUserId());
						ps.setInt(2, compId);
						ps.setString(3, pushMsg.getContent());
						ps.setInt(4, pushMsg.getRecUserId());
						ps.setString(5, recPhone);
						ps.setString(6, recName);
						ps.setInt(7, status);
						ps.setString(8, pushMsg.getSendName());
					}

				});
		/*
		sql = "select max(msg_id) as comp_id from messag_contact_log ";
		int msgId = this.jdbcTemplate.queryForInt(sql);
		*/
		
		return count == 1 ? true : false;
	}
	
	public boolean delete(int msgId){
		String sql = "delete from messag_contact_log where msg_id = ?";
		int count = this.jdbcTemplate.update(sql,msgId);
		return count==1;
	}
	
}
