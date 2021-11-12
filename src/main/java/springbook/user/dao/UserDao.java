package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;

import springbook.user.domain.User;

public class UserDao {
    //private ConnectionMaker connectionMaker;
    private DataSource dataSource;
    private JdbcTemplate jdbcTemplate;
    // public void setConnectionMaker(ConnectionMaker connectionMaker) {
    //     this.connectionMaker  = connectionMaker;
    // }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(User user) {
        //Connection c = connectionMaker.makeConnection();
        // Connection c = dataSource.getConnection();
        // PreparedStatement ps = c.prepareStatement("insert into users(id, name, password) values (?,?,?)");
        
        // ps.setString(1, user.getId());
        // ps.setString(2, user.getName());
        // ps.setString(3, user.getPassword());
        
        // ps.executeUpdate();

        // ps.close();
        // c.close();
        this.jdbcTemplate.update("insert into users(id, name, password) values (?,?,?)", user.getId(), user.getName(), user.getPassword());
    }

    public User get(String id) throws SQLException {
        // Connection c = connectionMaker.makeConnection();
        Connection c = dataSource.getConnection();
        PreparedStatement ps = c.prepareStatement("select * from users where id = ?");

        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();
        
        User user = null;
        if (rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPassword(rs.getString("password"));
        }
        
        rs.close();
        ps.close();
        c.close();
        
        if (user == null) throw new EmptyResultDataAccessException(1);

        return user;
    }

    public void deleteAll() {
        // Connection c = dataSource.getConnection();
        // PreparedStatement ps = c.prepareStatement("delete from users");

        // ps.executeUpdate();

        // ps.close();
        // c.close();
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException{
                return connection.prepareStatement("delete from users");
            }
        });
    }

    public int getCount() {
        // Connection c = dataSource.getConnection();
        // PreparedStatement ps = c.prepareStatement("select count(id) as cnt from users");

        // ResultSet rSet = ps.executeQuery();

        // rSet.next();
        // int userCount = rSet.getInt("cnt");
        // rSet.close();
        // ps.close();
        // c.close();
        // return userCount;
        return this.jdbcTemplate.query(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                return connection.prepareStatement("select count(id) as cnt from users");
            }
        }, new ResultSetExtractor<Integer>()  {
            public Integer extractData(ResultSet rSet) throws SQLException, DataAccessException {
                rSet.next();
                return rSet.getInt(1);
            }
        });
    }
}
