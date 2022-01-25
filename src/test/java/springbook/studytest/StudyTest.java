package springbook.studytest;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
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

        // NameMatchMethodPointcut nameMatchMethodPointcut = new
        // NameMatchMethodPointcut();
        NameMatchMethodPointcut classMatchMethodPointcut = new NameMatchMethodPointcut() {
            public ClassFilter getClassFilter() {
                return new ClassFilter() {
                    public boolean matches(Class<?> clazz) {
                        return clazz.getSimpleName().startsWith("HelloT");
                    }
                };
            }
        };
        classMatchMethodPointcut.setMappedName("sayH*");

        pFactoryBean.addAdvisor(new DefaultPointcutAdvisor(classMatchMethodPointcut, new UppercaseAdvise()));

        // Hello proxiedHello = (Hello) pFactoryBean.getObject();

        // assertThat(proxiedHello.sayHello("Dennis"), is("HELLO DENNIS"));
        // assertThat(proxiedHello.sayHi("Dennis"), is("HI DENNIS"));
        // assertThat(proxiedHello.sayThankYou("Dennis"), is("Thank you Dennis"));
        this.checkAdviced(new HelloTarget(), classMatchMethodPointcut, true);
        class HelloWolrd extends HelloTarget {
        }
        ;
        this.checkAdviced(new HelloWolrd(), classMatchMethodPointcut, false);
        class HelloTalk extends HelloTarget {
        }
        ;
        this.checkAdviced(new HelloTalk(), classMatchMethodPointcut, true);
    }

    private void checkAdviced(Object target, Pointcut pointcut, boolean adviced) {
        ProxyFactoryBean proxyFactoryBean = new ProxyFactoryBean();
        proxyFactoryBean.setTarget(target);
        proxyFactoryBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvise()));
        Hello proxiedHello = (Hello) proxyFactoryBean.getObject();

        if (adviced) {
            assertThat(proxiedHello.sayHello("Dennis"), is("HELLO DENNIS"));
            assertThat(proxiedHello.sayHi("Dennis"), is("HI DENNIS"));
            assertThat(proxiedHello.sayThankYou("Dennis"), is("Thank you Dennis"));
        } else {
            assertThat(proxiedHello.sayHello("Dennis"), is("Hello Dennis"));
            assertThat(proxiedHello.sayHi("Dennis"), is("Hi Dennis"));
            assertThat(proxiedHello.sayThankYou("Dennis"), is("Thank you Dennis"));
        }
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
