package springbook.user.service.impl;

import static springbook.user.service.impl.UserService.MIN_LOGIN_COUNT_FOR_SILVER;
import static springbook.user.service.impl.UserService.MIN_RECOMMAND_COUNT_FOR_GOLD;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.UserLevelPolicy;

public class CurrentUserLevelPolicy implements UserLevelPolicy {
    
    UserDao userDao;
    private MailSender mailSender;

    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Override
    public void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEmail(user);
    }

    public void sendUpgradeEmail(User user) {

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setFrom("userAdmin@ksug.org");
        mailMessage.setSubject("회원등급 Upgrade 안내");
        mailMessage.setText(user.getName() + "님의 등급이" + user.getLevel() + "로 변경되었습니다.");

        this.mailSender.send(mailMessage);
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
