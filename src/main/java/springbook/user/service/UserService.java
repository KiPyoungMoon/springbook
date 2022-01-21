package springbook.user.service;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public interface UserService {
    public void add(User user);
    public void upgradeLevels() throws Exception;
    public void setUserLevelPolicy(UserLevelPolicy userLevelPolicy);
    public void setUserDao(UserDao userDao);
}
