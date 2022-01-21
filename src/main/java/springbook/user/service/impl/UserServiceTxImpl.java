package springbook.user.service.impl;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;
import springbook.user.service.UserLevelPolicy;
import springbook.user.service.UserService;

public class UserServiceTxImpl implements UserService {

    protected UserService userService;
    protected PlatformTransactionManager transactionManager;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public void add(User user) {
        this.userService.add(user);
    }

    @Override
    public void upgradeLevels() throws Exception {
        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            this.userService.upgradeLevels();
            this.transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            this.transactionManager.rollback(transactionStatus);
            throw e;
        }

    }

    @Override
    public void setUserLevelPolicy(UserLevelPolicy userLevelPolicy) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setUserDao(UserDao userDao) {
        throw new UnsupportedOperationException();
    }

    
}
