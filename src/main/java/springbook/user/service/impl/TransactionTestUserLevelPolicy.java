package springbook.user.service.impl;

import springbook.user.domain.User;
import springbook.user.exception.TestUserServiceException;

public class TransactionTestUserLevelPolicy extends CurrentUserLevelPolicy {
    
    private String id;

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void upgradeLevel(User user) {
        if (user.getId().equals(this.id)) throw new TestUserServiceException();
        user.upgradeLevel();
        userDao.update(user);
    }
}
