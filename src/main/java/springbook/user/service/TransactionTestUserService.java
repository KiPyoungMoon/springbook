package springbook.user.service;

import java.util.List;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

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
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);
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
            System.out.println(e);
            transactionManager.rollback(transactionStatus);
            throw e;
        }
    }
}
