package txl.socket.dao;

import javax.annotation.Resource;

import org.springframework.jdbc.core.JdbcTemplate;

public abstract class BaseDao {
	@Resource
	protected JdbcTemplate jdbcTemplate;

	/*public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}*/
}
