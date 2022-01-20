package springbook.user.dao;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.domain.User;
import springbook.user.exception.DuplicateUserIdException;
import springbook.user.domain.Level;

/**
 * UserDao
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "../../../test-applicationContext.xml")
public class UserDaoTest {
    
    // @Autowired
    // private ApplicationContext context;
    
    @Autowired
    private UserDao userDao;

    @Before
    public void setUp() {
        /**
         * RunWith와 ContextConfiguration 어노테이션을 사용하고 ApplicationContext를 Autowired로 자동 주입하면서 주석처리.
         */
        //ApplicationContext context = new GenericXmlApplicationContext("./springbook/user/dao/applicationContext.xml"); // 경로 설정에 유의
        //this.userDao = context.getBean("userDao", UserDao.class);
        
        user1 = new User("kpMoon1", "문기평", "1234", Level.BASIC, 1, 0, "thefates82@gmail.com");
        user2 = new User("kpMoon2", "퐁2", "1234", Level.SILVER, 55, 10, "requiem-1@hanmail.net");
        user3 = new User("kpMoon3", "강연3", "1234", Level.GOLD, 100, 40, "mkpong0212@gmail.com");
    }

    User user1;
    User user2;
    User user3;

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        
        userDao.deleteAll();
        
        assertThat(userDao.getCount(), is(0));

        userDao.add(user1);
        assertThat(userDao.getCount(), is(1));

        userDao.add(user2);
        assertThat(userDao.getCount(), is(2));

        userDao.add(user3);
        assertThat(userDao.getCount(), is(3));
        
        User getUser1 = userDao.get(user1.getId());
        this.checkSameUser(user1, getUser1);
        
        User getUser2 = userDao.get(user2.getId());
        this.checkSameUser(user2, getUser2);

        User getUser3 = userDao.get(user3.getId());
        this.checkSameUser(user3, getUser3);

        List<User>  getUsers = userDao.getAll();
        User getAllUser1 = getUsers.get(0);
        User getAllUser2 = getUsers.get(1);
        User getAllUser3 = getUsers.get(2);
        
        this.checkSameUser(user1, getAllUser2);
        this.checkSameUser(user2, getAllUser3);
        this.checkSameUser(user3, getAllUser1);
    }
    
    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPassword(), is(user2.getPassword()));
        assertThat(user1.getLevel(), is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommand(), is(user2.getRecommand()));
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void getUserFailture() throws SQLException {
        userDao.get("UnKnownId");
    }

    @Test(expected = DuplicateUserIdException.class)
    public void duplicateKey() {
        
        userDao.deleteAll();

        userDao.add(user1);
        userDao.add(user1);
    }

    @Test
    public void updateUserInfo() throws SQLException, ClassNotFoundException {

        userDao.deleteAll();
        userDao.add(user1);
        userDao.add(user2);
        User targetUser = userDao.get(user1.getId());

        targetUser.setPassword("1212");
        targetUser.setName("수정자 이름");
        targetUser.setLevel(Level.SILVER);
        targetUser.setLogin(55);
        targetUser.setRecommand(22);
        userDao.update(targetUser);

        User updatedUser = userDao.get(user1.getId());
        User checkUser2 = userDao.get(user2.getId());
        this.checkSameUser(targetUser, updatedUser);
        this.checkSameUser(user2, checkUser2);
    }

}