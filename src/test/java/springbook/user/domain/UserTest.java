package springbook.user.domain;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import org.junit.Before;
import org.junit.Test;

public class UserTest {
    
    User user;

    @Before
    public void setUser() {
        this.user = new User();
    }

    @Test
    public void canUpgradeLevel() {
        Level[] levels = Level.values();

        for (Level level : levels) {
            if (level.nextLevel() == null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            assertThat(user.getLevel(), is(level.nextLevel()));
        }

    }

    @Test(expected = IllegalStateException.class)
    public void canNotUpgradeMaxLevel() {
        Level[] levels = Level.values();

        for (Level level : levels) {
            if (level.nextLevel() != null) continue;
            user.setLevel(level);
            user.upgradeLevel();
        }
    }
}
