package springbook.user.service;

import springbook.user.domain.User;

public interface UserLevelPolicy {
    boolean canUpgradeLevel(User user);
    void upgradeLevel(User user);
}
