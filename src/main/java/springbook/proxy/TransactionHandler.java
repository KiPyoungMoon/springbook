package springbook.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionHandler implements InvocationHandler {
    private Object target;
    private PlatformTransactionManager transactionManager;
    private String pattern;

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTransactionManager(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getName().startsWith(this.pattern)) {
            return this.invokeTransaction(method, args);
        } else {
            return method.invoke(target, args);
        }
    }

    private Object invokeTransaction(Method method, Object[] args) throws Throwable {
        TransactionStatus transactionStatus = transactionManager.getTransaction(new DefaultTransactionDefinition());

        try {
            Object ret = method.invoke(this.target, args);
            this.transactionManager.commit(transactionStatus);
            return ret;
        } catch (InvocationTargetException e) {
            this.transactionManager.rollback(transactionStatus);
            throw e.getTargetException();
        }
    }
    
}
