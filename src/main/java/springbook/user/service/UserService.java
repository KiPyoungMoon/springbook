package springbook.user.service;

import java.util.List;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;
import springbook.user.domain.Level;

public class UserService {
    
    public static final int MIN_LOGIN_COUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMAND_COUNT_FOR_GOLD = 30;

    private UserDao userDao;
    private UserLevelPolicy userLevelPolicy;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setUserLevelPolicy(UserLevelPolicy userLevelPolicy) {
        this.userLevelPolicy = userLevelPolicy;
    }

    public void upgradeLevels() {
        List<User> userList = userDao.getAll();

        for (User user : userList) {
            Boolean canUpgrade = userLevelPolicy.canUpgradeLevel(user);
            if ( Boolean.TRUE.equals(canUpgrade) ) {
                userLevelPolicy.upgradeLevel(user);
            }
        }
    }

    public void add(User user) {
        if ( user.getLevel() == null ) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}
