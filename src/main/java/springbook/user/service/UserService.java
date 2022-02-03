package springbook.user.service;

import java.util.List;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public interface UserService {
    public void add(User user);

    public User get(String id);

    public List<User> getAll();

    public void deleteAll();

    public void update(User user);

    public void upgradeLevels();

    public void setUserLevelPolicy(UserLevelPolicy userLevelPolicy);

    public void setUserDao(UserDao userDao);
}
