package txl.socket.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Repository;

import txl.socket.TxlConstants;
import txl.socket.po.PushMessage;

@Repository
public class ContactMessageDao extends BaseDao {
	private final Logger log = Logger.getLogger(ContactMessageDao.class);

	public boolean save(final PushMessage pushMsg, final int compId,
			final String recPhone, final String recName, final int status) {

		String sql = "insert into message_contact_log("
				+ "user_id,comp_id,content,rec_user_id,"
				+ "rec_phone,rec_name,create_time,status,send_name," +
				"msg_uuid) "
				+ "values(?,?,?,?," + "?,?,sysdate(),?,?,?)";

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
						ps.setString(9, pushMsg.getMsgId());
					}

				});
		/*
		sql = "select max(msg_id) as comp_id from messag_contact_log ";
		int msgId = this.jdbcTemplate.queryForInt(sql);
		*/
		
		return count == 1 ? true : false;
	}
	
	public boolean delete(int msgId){
		String sql = "delete from message_contact_log where msg_id = ?";
		int count = this.jdbcTemplate.update(sql,msgId);
		return count==1;
	}
	
	public List<PushMessage> queryPushMessagesByRecUserId(final int recUserId){
	    String sql = "select * from message_contact_log where rec_user_id = "+recUserId;
	    final List<PushMessage> pushMessageList = new ArrayList<PushMessage>();
	    this.jdbcTemplate.query(sql, new RowCallbackHandler(){

            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                PushMessage pm = new PushMessage();
                pm.setContent(rs.getString("content"));
                pm.setMsgId(rs.getString("msg_uuid"));
                pm.setRecUserId(recUserId);
                pm.setSendName(rs.getString("send_name"));
                pm.setSendUserId(rs.getInt("user_id"));
                pm.setPushMsgType(TxlConstants.MESSAGE_TYPE_PLAIN_TEXT);
                pm.setMsgIntId(rs.getInt("msg_id"));
                pushMessageList.add(pm);
            }
	    });
	    return pushMessageList;
	}
	
	
	/**
	 * 批量删除发送成功的消息
	 * @param msgIdList
	 */
	public void delete(final List<Integer> msgIdList){
	    String sql = "delete from message_contact_log where msg_id=?";
	    this.jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter(){

            @Override
            public int getBatchSize()
            {
                return msgIdList.size();
            }

            @Override
            public void setValues(PreparedStatement ps, int index) throws SQLException
            {
                ps.setInt(1, msgIdList.get(index));
            }
	        
	    });
	    
	}
}
