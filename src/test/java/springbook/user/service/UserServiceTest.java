package springbook.user.service;

import static org.junit.Assert.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;
import springbook.user.domain.User.Level;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "../../../test-applicationContext.xml")
public class UserServiceTest {
    
    @Autowired
    UserService userService;

    @Autowired
    UserDao userDao;

    List<User> userList;

    @Before
    public void setUp() {
        userList = Arrays.asList(
            new User("mkp", "문기평", "1234", Level.BASIC, 49, 0),
            new User("mkp2", "문기평2", "1234", Level.BASIC, 50, 0),
            new User("mkp3", "문기평3", "1234", Level.SILVER, 60, 29),
            new User("mkp4", "문기평4", "1234", Level.SILVER, 60, 30),
            new User("mkp5", "문기평5", "1234", Level.GOLD, 100, 100)
        );
    }

    @Test
    public void bean() {
        assertNotNull(userService);
    }

    /**
     * 가입 후 로그인 50회 이상이면 SILVER
     * SELVER이고 추천 30회 이상이면 GOLD
     */
    @Test
    public void upgradeUserLevel() {
        userDao.deleteAll();

        for (User user : userList) {
            userDao.add(user);
        }

        userService.upgradeLevels();

        this.checkUserLevel(userList.get(0), Level.BASIC);
        this.checkUserLevel(userList.get(1), Level.SILVER);
        this.checkUserLevel(userList.get(2), Level.SILVER);
        this.checkUserLevel(userList.get(3), Level.GOLD);
        this.checkUserLevel(userList.get(4), Level.GOLD);
    }

    private void checkUserLevel(User user, Level level) {
        User targetUser = userDao.get(user.getId());
        assertThat(targetUser.getLevel(), is(level));
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userAlreadyGetLevel = userList.get(4);
        User userNoLevel = userList.get(0);
        userNoLevel.setLevel(null);

        userService.add(userAlreadyGetLevel);
        userService.add(userNoLevel);

        this.checkUserLevel(userList.get(4), userList.get(4).getLevel());
        this.checkUserLevel(userList.get(0), Level.BASIC);
    }
}
