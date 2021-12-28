package springbook.user.service;

import static springbook.user.service.UserService.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECOMMAND_COUNT_FOR_GOLD;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;
import springbook.user.domain.Level;

public class CurrentUserLevelPolicy implements UserLevelPolicy {
    
    UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    
    @Override
    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
    }
    
    @Override
    public boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC: return (user.getLogin() >= MIN_LOGIN_COUNT_FOR_SILVER);
            case SILVER: return (user.getRecommand() >= MIN_RECOMMAND_COUNT_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }
}
