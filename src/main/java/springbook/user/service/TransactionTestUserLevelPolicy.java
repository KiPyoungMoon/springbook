package springbook.user.service;

import springbook.user.domain.User;

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
