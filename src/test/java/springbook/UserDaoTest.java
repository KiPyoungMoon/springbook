package springbook;

import java.sql.SQLException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.DuplicateUserIdException;
import springbook.user.dao.UserDaoJdbc;
import springbook.user.domain.User;

/**
 * UserDao
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "../test-applicationContext.xml")
public class UserDaoTest {
    
    @Autowired
    private ApplicationContext context;
    
    @Autowired
    private UserDaoJdbc userDao;

    @Before
    public void setUp() {
        /**
         * RunWith와 ContextConfiguration 어노테이션을 사용하고 ApplicationContext를 Autowired로 자동 주입하면서 주석처리.
         */
        //ApplicationContext context = new GenericXmlApplicationContext("./springbook/user/dao/applicationContext.xml"); // 경로 설정에 유의
        //this.userDao = context.getBean("userDao", UserDao.class);
        
        user1 = new User("kpMoon1", "문기평", "1234");
        user2 = new User("kpMoon2", "퐁2", "1234");
        user3 = new User("kpMoon3", "강연3", "1234");
    }

    User user1;
    User user2;
    User user3;

    @Test
    public void addAndGet() throws SQLException, ClassNotFoundException {
        
        
        userDao.deleteAll();
        
        assertThat(userDao.getCount(), is(0));

        // User user1 = new User("kpMoon1", "문기평", "1234");
        // User user2 = new User("kpMoon2", "퐁2", "1234");
        // User user3 = new User("kpMoon3", "강연3", "1234");

        userDao.add(user1);
        userDao.add(user2);
        userDao.add(user3);

        assertThat(userDao.getCount(), is(3));
        
        User getUser1 = userDao.get(user1.getId());
        assertThat(user1.getName(), is(getUser1.getName()));
        assertThat(user1.getPassword(), is(getUser1.getPassword()));
        
        User getUser2 = userDao.get(user2.getId());
        assertThat(user2.getName(), is(getUser2.getName()));
        assertThat(user2.getPassword(), is(getUser2.getPassword()));

        User getUser3 = userDao.get(user3.getId());
        assertThat(user3.getName(), is(getUser3.getName()));
        assertThat(user3.getPassword(), is(getUser3.getPassword()));

        List<User>  getUsers = userDao.getAll();
        User getAllUser1 = getUsers.get(0);
        User getAllUser2 = getUsers.get(1);
        User getAllUser3 = getUsers.get(2);

        assertThat(user1.getName(), is(getAllUser2.getName()));
        assertThat(user1.getPassword(), is(getAllUser2.getPassword()));
        assertThat(user2.getName(), is(getAllUser3.getName()));
        assertThat(user2.getPassword(), is(getAllUser3.getPassword()));
        assertThat(user3.getName(), is(getAllUser1.getName()));
        assertThat(user3.getPassword(), is(getAllUser1.getPassword()));
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
}