package springbook.user.service.impl;

import java.util.List;

import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.user.domain.User;
import springbook.user.service.UserLevelPolicy;

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
}
