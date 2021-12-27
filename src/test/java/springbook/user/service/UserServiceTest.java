package springbook.user.service;

import static org.junit.Assert.assertNotNull;
import static springbook.user.service.UserService.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECOMMAND_COUNT_FOR_GOLD;
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
            new User("mkp", "문기평", "1234", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER - 1, 0),
            new User("mkp2", "문기평2", "1234", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER, 0),
            new User("mkp3", "문기평3", "1234", Level.SILVER, MIN_LOGIN_COUNT_FOR_SILVER + 10, MIN_RECOMMAND_COUNT_FOR_GOLD - 1),
            new User("mkp4", "문기평4", "1234", Level.SILVER, MIN_LOGIN_COUNT_FOR_SILVER + 10, MIN_RECOMMAND_COUNT_FOR_GOLD),
            new User("mkp5", "문기평5", "1234", Level.GOLD, MIN_LOGIN_COUNT_FOR_SILVER + 50, Integer.MAX_VALUE)
        );
    }

    @Test
    public void bean() {
        assertNotNull(userService);
    }

    @Test
    public void upgradeUserLevel() {
        userDao.deleteAll();

        for (User user : userList) userDao.add(user);

        userService.upgradeLevels();

        this.checkUserUpgraded(userList.get(0), false);
        this.checkUserUpgraded(userList.get(1), true);
        this.checkUserUpgraded(userList.get(2), false);
        this.checkUserUpgraded(userList.get(3), true);
        this.checkUserUpgraded(userList.get(4), false);
    }

    private void checkUserUpgraded(User user, Boolean result) {
        User targetUser = userDao.get(user.getId());
        if ( Boolean.TRUE.equals( result )) assertThat(targetUser.getLevel(), is(user.getLevel().nextLevel()));
        else assertThat(targetUser.getLevel(), is(user.getLevel()));
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userAlreadyGetLevel = userList.get(4);
        User userNoLevel = userList.get(0);
        userNoLevel.setLevel(null);

        userService.add(userAlreadyGetLevel);
        userService.add(userNoLevel);

        this.checkUserUpgraded(userAlreadyGetLevel, false);
        assertThat(userNoLevel.getLevel(), is(Level.BASIC));
    }

    @Test(expected = IllegalStateException.class)
    public void cannotUpgradeMaxLevel() {
        User targetUser = userList.get(4);
        targetUser.upgradeLevel();
    }
}
