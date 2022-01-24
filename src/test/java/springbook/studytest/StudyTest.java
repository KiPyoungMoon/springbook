package springbook.studytest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "../../test-applicationContext.xml")
public class StudyTest {
    
    @Test
    public void pointCutAdvisor() {
        ProxyFactoryBean pFactoryBean = new ProxyFactoryBean();
        pFactoryBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut nameMatchMethodPointcut = new NameMatchMethodPointcut();
        nameMatchMethodPointcut.setMappedName("sayH*");

        pFactoryBean.addAdvisor(new DefaultPointcutAdvisor(nameMatchMethodPointcut, new UppercaseAdvise()));

        Hello proxiedHello = (Hello) pFactoryBean.getObject(); 

        assertThat(proxiedHello.sayHello("Dennis"), is("HELLO DENNIS"));
        assertThat(proxiedHello.sayHi("Dennis"), is("HI DENNIS"));
        assertThat(proxiedHello.sayThankYou("Dennis"), is("Thank you Dennis"));
    }

    public class HelloTarget implements Hello {

        @Override
        public String sayHello(String string) {
            return "Hello " + string;
        }

        @Override
        public String sayHi(String string) {
            return "Hi " + string;
        }

        @Override
        public String sayThankYou(String string) {
            return "Thank you " + string;
        }
        
    }

    public class UppercaseAdvise implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed();
            return ret.toUpperCase();
        }

    }
}



