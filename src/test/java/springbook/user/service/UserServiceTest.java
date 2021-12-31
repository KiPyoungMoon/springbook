package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static springbook.user.service.impl.UserServiceImpl.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.user.service.impl.UserServiceImpl.MIN_RECOMMAND_COUNT_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.exception.TestUserServiceException;
import springbook.user.service.impl.CurrentUserLevelPolicy;
import springbook.user.service.impl.UserServiceImpl;
import springbook.user.service.impl.UserServiceTxImpl;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "../../../test-applicationContext.xml")
public class UserServiceTest {
    
    @Autowired
    UserServiceTxImpl userService;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    UserDao userDao;

    @Autowired
    CurrentUserLevelPolicy userLevelPolicy;

    MockMailSender mockMailSender;

    List<User> userList;

    @Before
    public void setUp() {
        this.setTestUsersInfo();
        this.setMockMailDependencyInjection();
        // 테스트는 실제 email을 발송할 필요가 없어 항상 mockObject를 사용하도록 DI 변경.
    }

    private void setTestUsersInfo() {
        this.userList = Arrays.asList(
            new User("mkp", "문기평", "1234", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER - 1, 0, "thefates82@gmail.com"),
            new User("mkp2", "문기평2", "1234", Level.BASIC, MIN_LOGIN_COUNT_FOR_SILVER, 0, "requiem-1@hanmail.net"),
            new User("mkp3", "문기평3", "1234", Level.SILVER, MIN_LOGIN_COUNT_FOR_SILVER + 10, MIN_RECOMMAND_COUNT_FOR_GOLD - 1, "mkpong0212@gmail.com"),
            new User("mkp4", "문기평4", "1234", Level.SILVER, MIN_LOGIN_COUNT_FOR_SILVER + 10, MIN_RECOMMAND_COUNT_FOR_GOLD, null),
            new User("mkp5", "문기평5", "1234", Level.GOLD, MIN_LOGIN_COUNT_FOR_SILVER + 50, Integer.MAX_VALUE, "")
        );
    }

    private void setMockMailDependencyInjection() {
        this.mockMailSender = new MockMailSender();
        this.userLevelPolicy.setMailSender(mockMailSender);
        this.userServiceImpl.setUserLevelPolicy(userLevelPolicy);
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
    public void upgradeUserLevel() throws Exception {
        userDao.deleteAll();

        for (User user : userList) userDao.add(user);

        
        userService.upgradeLevels();

        this.checkUserUpgraded(userList.get(0), false);
        this.checkUserUpgraded(userList.get(1), true);
        this.checkUserUpgraded(userList.get(2), false);
        this.checkUserUpgraded(userList.get(3), true);
        this.checkUserUpgraded(userList.get(4), false);

        List<String> requests = mockMailSender.getRequests();

        assertThat(requests.size(), is(2));
        assertThat(requests.get(0), is(userList.get(1).getEmail()));
        assertThat(requests.get(1), is(userList.get(3).getEmail()));
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

        this.userService.add(userAlreadyGetLevel);
        this.userService.add(userNoLevel);

        this.checkUserUpgraded(userAlreadyGetLevel, false);
        assertThat(userNoLevel.getLevel(), is(Level.BASIC));
    }

    @Test
    public void upgradeAllOrNothing() throws Exception {
        TransactionTestUserLevelPolicy userLevelPolicy = new TransactionTestUserLevelPolicy();
        userLevelPolicy.setId(userList.get(3).getId());
        userLevelPolicy.setUserDao(userDao);
        this.userServiceImpl.setUserLevelPolicy(userLevelPolicy);
        this.userService.setUserService(this.userServiceImpl);
        
        userDao.deleteAll();
        for (User user : userList) userDao.add(user);

        try {
            this.userService.upgradeLevels();
            fail("TestUserServiceException Expected.");
        } catch (TestUserServiceException e) {}
        this.checkUserUpgraded(userList.get(1), false);
    }

    static class MockMailSender implements MailSender {

        private List<String> request = new ArrayList<String>();

        public List<String> getRequests() {
            return this.request;
        }

        @Override
        public void send(SimpleMailMessage simpleMessage) throws MailException {
            this.request.add(simpleMessage.getTo()[0]);
        }

        @Override
        public void send(SimpleMailMessage... simpleMessages) throws MailException {
            for (SimpleMailMessage simpleMailMessage : simpleMessages) {
                this.request.add(simpleMailMessage.getTo()[0]);    
            }
        }

    }

    static public class TransactionTestUserLevelPolicy extends CurrentUserLevelPolicy {
    
        private String id;
    
        public void setId(String id) {
            this.id = id;
        }
    
        @Override
        public void upgradeLevel(User user) {
            if (user.getId().equals(this.id)) throw new TestUserServiceException();
            user.upgradeLevel();
            this.userDao.update(user);
        }
    }
}