package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static springbook.user.service.impl.UserServiceImpl.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.user.service.impl.UserServiceImpl.MIN_RECOMMAND_COUNT_FOR_GOLD;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.oxm.Unmarshaller;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.exception.TestUserServiceException;
import springbook.user.service.impl.CurrentUserLevelPolicy;
import springbook.user.service.impl.UserServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "../../../test-applicationContext.xml")
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserService testUserService;

    @Autowired
    UserDao userDao;

    @Autowired
    CurrentUserLevelPolicy userLevelPolicy;

    List<User> userList;

    @Before
    public void setUp() {
        this.setTestUsersInfo();
    }

    private void setTestUsersInfo() {
        this.userList = Arrays.asList(
                new User("mkp", "?????????", "1234", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER - 1, 0, "thefates82@gmail.com"),
                new User("mkp2", "?????????2", "1234", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER, 0, "requiem-1@hanmail.net"),
                new User("mkp3", "?????????3", "1234", Level.SILVER, MIN_LOGIN_COUNT_FOR_SILVER + 10,
                        MIN_RECOMMAND_COUNT_FOR_GOLD - 1, "mkpong0212@gmail.com"),
                new User("mkp4", "?????????4", "1234", Level.SILVER, MIN_LOGIN_COUNT_FOR_SILVER + 10,
                        MIN_RECOMMAND_COUNT_FOR_GOLD, null),
                new User("mkp5", "?????????5", "1234", Level.GOLD, MIN_LOGIN_COUNT_FOR_SILVER + 50, Integer.MAX_VALUE, ""));
    }

    @Test
    public void bean() {
        assertNotNull(userService);
    }

    @Test
    public void bean2() {
        assertNotNull(userDao);
    }

    @Test
    @DirtiesContext
    public void upgradeUserLevel() throws Exception {
        UserDao mockUserDao = mock(UserDao.class);
        MailSender mockMailSender = mock(MailSender.class);
        when(mockUserDao.getAll()).thenReturn(this.userList);
        this.userLevelPolicy.setMailSender(mockMailSender);

        userService.setUserDao(mockUserDao);
        this.userLevelPolicy.setUserDao(mockUserDao);
        userService.setUserLevelPolicy(this.userLevelPolicy);

        userService.upgradeLevels();

        verify(mockUserDao, times(2)).update(any(User.class));
        verify(mockUserDao).update(this.userList.get(1));
        assertThat(this.userList.get(1).getLevel(), is(Level.SILVER));
        verify(mockUserDao).update(this.userList.get(3));
        assertThat(this.userList.get(3).getLevel(), is(Level.GOLD));

        ArgumentCaptor<SimpleMailMessage> mailMessageArgumentCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        verify(mockMailSender, times(2)).send(mailMessageArgumentCaptor.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArgumentCaptor.getAllValues();

        assertThat(mailMessages.get(0).getTo()[0], is(this.userList.get(1).getEmail()));
        assertThat(mailMessages.get(1).getTo()[0], is(this.userList.get(3).getEmail()));
    }

    @Test
    public void add() {
        userDao.deleteAll();

        User userAlreadyGetLevel = userList.get(4);
        User userNoLevel = userList.get(0);
        userNoLevel.setLevel(null);

        this.userService.add(userAlreadyGetLevel);
        this.userService.add(userNoLevel);

        this.checkUserUpgraded(userAlreadyGetLevel, false);

        assertThat(userNoLevel.getLevel(), is(Level.BASIC));
    }

    private void checkUserUpgraded(User user, Boolean result) {
        User targetUser = userDao.get(user.getId());
        if (Boolean.TRUE.equals(result))
            assertThat(targetUser.getLevel(), is(user.getLevel().nextLevel()));
        else
            assertThat(targetUser.getLevel(), is(user.getLevel()));
    }

    @Test
    @DirtiesContext
    public void upgradeAllOrNothing() throws Exception {
        TransactionTestUserLevelPolicy userLevelPolicy = new TransactionTestUserLevelPolicy();
        userLevelPolicy.setId(userList.get(3).getId());
        userLevelPolicy.setUserDao(userDao);
        userService.setUserLevelPolicy(userLevelPolicy);

        userDao.deleteAll();
        for (User user : userList)
            userDao.add(user);

        try {
            userService.upgradeLevels();
            fail("TestUserServiceException Expected.");
        } catch (TestUserServiceException e) {
        }
        this.checkUserUpgraded(userList.get(1), false);
    }

    @Test(expected = TransientDataAccessResourceException.class)
    public void readOnlyTransactionAttribute() {
        testUserService.getAll();
    }

    static public class TransactionTestUserLevelPolicy extends CurrentUserLevelPolicy {

        private String id;

        public void setId(String id) {
            this.id = id;
        }

        @Override
        public void upgradeLevel(User user) {
            if (user.getId().equals(this.id))
                throw new TestUserServiceException();
            user.upgradeLevel();
            this.userDao.update(user);
        }
    }

    static public class TestUserService extends UserServiceImpl {
        public List<User> getAll() {
            List<User> uList = super.getAll();

            for (User user : uList) {
                super.update(user);
            }
            return null;
        }
    }

}