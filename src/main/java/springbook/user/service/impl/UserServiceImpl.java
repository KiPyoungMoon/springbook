package springbook.user.service.impl;

import java.util.List;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;
import springbook.user.service.UserLevelPolicy;
import springbook.user.service.UserService;
import springbook.user.domain.Level;

public class UserServiceImpl implements UserService {

    public static final int MIN_LOGIN_COUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMAND_COUNT_FOR_GOLD = 30;

    protected UserDao userDao;
    protected UserLevelPolicy userLevelPolicy;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelPolicy(UserLevelPolicy userLevelPolicy) {
        this.userLevelPolicy = userLevelPolicy;
    }


    @Override
    public void upgradeLevels() {
        List<User> userList = userDao.getAll();

        for (User user : userList) {
            Boolean canUpgrade = userLevelPolicy.canUpgradeLevel(user);
            if (Boolean.TRUE.equals(canUpgrade)) {
                userLevelPolicy.upgradeLevel(user);
            }
        }
    }

    @Override
    public void add(User user) {
        if (user.getLevel() == null) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}
