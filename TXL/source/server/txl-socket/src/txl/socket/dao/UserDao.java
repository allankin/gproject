package txl.socket.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;

import txl.socket.po.User;

/**
 * @ClassName:  UserDao.java
 * @Description: 
 * @Author JinChao
 * @Date 2013-5-31 上午10:26:10
 * @Copyright: 版权由 HundSun 拥有
 */
@Repository
public class UserDao extends BaseDao
{
    public User queryById(final Integer id) {
        String sql = "select * from user where user_id = ?";
        final User user = new User();
        this.jdbcTemplate.query(sql, new RowCallbackHandler()
        {
            @Override
            public void processRow(ResultSet rs) throws SQLException
            {
                user.setCompId(rs.getInt("comp_id"));
                user.setUserId(id);
                user.setUserName(rs.getString("user_name"));
                user.setUserPhone(rs.getString("user_phone"));
            }
        },id);
        if(user.getUserId()==null){
            return null;
        }
        return user;
    }
}
