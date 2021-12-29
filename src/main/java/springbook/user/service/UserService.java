package springbook.user.service;

import java.util.List;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;
import springbook.user.domain.Level;

public class UserService {
    
    public static final int MIN_LOGIN_COUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMAND_COUNT_FOR_GOLD = 30;

    protected UserDao userDao;
    protected UserLevelPolicy userLevelPolicy;
    protected PlatformTransactionManager transactionManager;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelPolicy(UserLevelPolicy userLevelPolicy) {
        this.userLevelPolicy = userLevelPolicy;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void upgradeLevels() throws Exception {
        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            List<User> userList = userDao.getAll();
    
            for (User user : userList) {
                Boolean canUpgrade = userLevelPolicy.canUpgradeLevel(user);
                if ( Boolean.TRUE.equals(canUpgrade) ) {
                    userLevelPolicy.upgradeLevel(user);
                }
            }
            transactionManager.commit(transactionStatus);
        } catch (Exception e) {
            transactionManager.rollback(transactionStatus);
            throw e;
        }
    }

    public void add(User user) {
        if ( user.getLevel() == null ) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}
