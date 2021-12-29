package springbook.user.service;

import java.sql.Connection;
import java.util.List;

import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import springbook.user.domain.User;

public class TransactionTestUserService extends UserService {
    
    private TransactionTestUserLevelPolicy userLevelPolicy;

    @Override
    public void setUserLevelPolicy(UserLevelPolicy transactionTestUserLevelPolicy) {
        this.userLevelPolicy = (TransactionTestUserLevelPolicy)transactionTestUserLevelPolicy;
    }

    public void setId(String id) {
        userLevelPolicy.setId(id);
    }

    @Override
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
            System.out.println(e);
            c.rollback();
            throw e;
        } finally {
            DataSourceUtils.releaseConnection(c, dataSource);
            TransactionSynchronizationManager.unbindResource(this.dataSource);
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
}
