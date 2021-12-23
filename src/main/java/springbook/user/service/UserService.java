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
        Boolean isChange = null;
        for (User user : userList) {
            isChange = false;
            
            if ( user.getLogin() >= 50 && user.getLevel() == Level.BASIC ) {
                user.setLevel(Level.SILVER);
                isChange = true;
            } else if ( user.getRecommand() >= 30 && user.getLevel() == Level.SILVER ) {
                user.setLevel(Level.GOLD);
                isChange = true;
            }

            if ( Boolean.TRUE.equals(isChange) ) {
                userDao.update(user);
            }
        }
    }
}
