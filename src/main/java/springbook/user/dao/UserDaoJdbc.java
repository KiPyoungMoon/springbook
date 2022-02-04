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
import springbook.user.exception.DuplicateUserIdException;
import springbook.sql.SqlService;
import springbook.user.domain.Level;

public class UserDaoJdbc implements UserDao {

    private JdbcTemplate jdbcTemplate;
    private SqlService sqlService;

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setSqlService(SqlService sqlService) {
        this.sqlService = sqlService;
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
            user.setEmail(rSet.getString("email"));

            return user;
        }
    };

    public void add(User user) throws DuplicateUserIdException {
        try {
            this.jdbcTemplate.update(this.sqlService.getSql("userAdd"),
                    user.getId(), user.getName(), user.getPassword(),
                    user.getLevel().intValue(), user.getLogin(), user.getRecommand(), user.getEmail());
        } catch (DuplicateKeyException e) {
            throw new DuplicateUserIdException(e);
        }
    }

    public User get(String id) {
        return this.jdbcTemplate.queryForObject(this.sqlService.getSql("userGet"), new Object[] { id },
                this.userMapper);
    }

    public List<User> getAll() {
        return this.jdbcTemplate.query(this.sqlService.getSql("userGetAll"), this.userMapper);
    }

    public void deleteAll() {
        this.jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                return connection.prepareStatement(sqlService.getSql("userDelete"));
            }
        });
    }

    public int getCount() {
        return this.jdbcTemplate.query(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                return connection.prepareStatement(sqlService.getSql("userCount"));
            }
        }, new ResultSetExtractor<Integer>() {
            public Integer extractData(ResultSet rSet) throws SQLException, DataAccessException {
                rSet.next();
                return rSet.getInt(1);
            }
        });
    }

    public void update(User user) {
        this.jdbcTemplate.update(this.sqlService.getSql("userUpdate"),
                user.getName(), user.getPassword(), user.getLevel().intValue(), user.getLogin(), user.getRecommand(),
                user.getEmail(), user.getId());
    }
}
