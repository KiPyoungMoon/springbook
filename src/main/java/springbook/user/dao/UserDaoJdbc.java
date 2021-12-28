package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;

import springbook.user.domain.User;
import springbook.user.domain.Level;

public class UserDaoJdbc implements UserDao {
    
    private JdbcTemplate jdbcTemplate;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    private RowMapper<User> userMapper = new RowMapper<User>() {
        public User mapRow(ResultSet rSet, int rowNum) throws SQLException {
            User user = new User();

            user.setId(rSet.getString("id"));
            user.setName(rSet.getString("name"));
            user.setPassword(rSet.getString("password"));
            user.setLevel(Level.valueOf(rSet.getInt("level")));
            user.setLogin(rSet.getInt("login"));
            user.setRecommand(rSet.getInt("recommand"));

            return user;
        }
    };

    public void add(User user) throws DuplicateUserIdException {
        try {
            this.jdbcTemplate.update("insert into users(id, name, password, level, login, recommand) values (?,?,?,?,?,?)", user.getId(), user.getName(), user.getPassword(), 
            user.getLevel().intValue(), user.getLogin(), user.getRecommand());
        } catch (DuplicateKeyException e) {
            throw new DuplicateUserIdException(e);
        }
    }

    public User get(String id)  {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?", new Object[] {id}, this.userMapper);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by name", this.userMapper);
    }

    public void deleteAll() {
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException{
                return connection.prepareStatement("delete from users");
            }
        });
    }

    public int getCount() {
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

    public void update(User user) {
        this.jdbcTemplate.update("update users set name = ?, password = ?, level = ?, login = ?, recommand = ? where id = ?", 
                                                user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommand(), user.getId());
    }
}
