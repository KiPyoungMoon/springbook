package springbook.user.dao;

//import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
//import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;

import springbook.user.domain.User;

public class UserDaoTest {
    public static void main(String[] args) {
        
        //ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
        ApplicationContext context = new GenericXmlApplicationContext("springbook/user/dao/applicationContext.xml");
        UserDaoJdbc userDao = context.getBean("userDao", UserDaoJdbc.class); //getBean("이름", 리턴타입);
        //UserDao userDao = new DaoFactory().UserDao(); 팩토리 패턴 생성자를 위의 빈 주입 방식으로 변경.
        //((ConfigurableApplicationContext)context).close(); // 'context' is never closed 경고로 추가
        
        User user = new User();
        user.setId("kpMoon");
        user.setName("문기평");
        user.setPassword("1234");

        userDao.add(user);

        System.out.println(user.getId() + " 등록 성공!");

        User user2 = userDao.get(user.getId());

        System.out.println(user2.getId() + " 조회 성공!");

        ((GenericXmlApplicationContext)context).close();
    }
}
