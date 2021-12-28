package springbook.user.service;

import java.util.List;

import springbook.user.domain.User;

public class TransactionTestUserService extends UserService {
    
    TransactionTestUserLevelPolicy transactionTestUserLevelPolicy;

    public void setTransactionTestUserLevelPolicy(TransactionTestUserLevelPolicy transactionTestUserLevelPolicy) {
        this.transactionTestUserLevelPolicy = transactionTestUserLevelPolicy;
    }

    public void setId(String id) {
        transactionTestUserLevelPolicy.setId(id);
    }

    @Override
    public void upgradeLevels() {
        List<User> userList = userDao.getAll();

        for (User user : userList) {
            Boolean canUpgrade = transactionTestUserLevelPolicy.canUpgradeLevel(user);
            if ( Boolean.TRUE.equals(canUpgrade) ) {
                transactionTestUserLevelPolicy.upgradeLevel(user);
            }
        }
    }
}
