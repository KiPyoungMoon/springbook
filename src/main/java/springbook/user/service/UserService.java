package springbook.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;
import springbook.user.domain.User.Level;

public class UserService {
    
    @Autowired
    private UserDao userDao;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void upgradeLevels() {
        List<User> userList = userDao.getAll();

        for (User user : userList) {
            Boolean canUpgrade = this.canUpgradeLevel(user);
            if ( Boolean.TRUE.equals(canUpgrade) ) {
                this.upgradeLevel(user);
            }
        }
    }

    private void upgradeLevel(User user) {
        user.setLevel(user.getLevel().nextLevel());
        userDao.update(user);
    }

    private boolean canUpgradeLevel(User user) {
        Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC: return (user.getLogin() >= 50);
            case SILVER: return (user.getRecommand() >= 30);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
        }
    }

    public void add(User user) {
        if ( user.getLevel() == null ) {
            user.setLevel(Level.BASIC);
        }
        userDao.add(user);
    }
}
