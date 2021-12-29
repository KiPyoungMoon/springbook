package springbook.user.service;

import java.sql.Connection;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;
import springbook.user.domain.Level;

public class UserService {
    
    public static final int MIN_LOGIN_COUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMAND_COUNT_FOR_GOLD = 30;

    protected UserDao userDao;
    protected UserLevelPolicy userLevelPolicy;
    protected DataSource dataSource;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelPolicy(UserLevelPolicy userLevelPolicy) {
        this.userLevelPolicy = userLevelPolicy;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void upgradeLevels() throws Exception {
        TransactionSynchronizationManager.initSynchronization();
        Connection c = DataSourceUtils.getConnection(dataSource);

        c.setAutoCommit(false);

        try {
            List<User> userList = userDao.getAll();
    
            for (User user : userList) {
                Boolean canUpgrade = userLevelPolicy.canUpgradeLevel(user);
                if ( Boolean.TRUE.equals(canUpgrade) ) {
                    userLevelPolicy.upgradeLevel(user);
                }
            }
            c.commit();
        } catch (Exception e) {
            c.rollback();
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(c, dataSource);
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }
    }

    public void add(User user) {
        if ( user.getLevel() == null ) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}
