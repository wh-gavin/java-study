package springcloud.eurekaserver;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.EurekaClientConfig;
import com.netflix.eureka.EurekaServerConfig;
import com.netflix.eureka.resources.ServerCodecs;

import lombok.extern.slf4j.Slf4j;

/**
* 配置注册需要的bean
*/
@Component
@Slf4j
public class FactoryConfig implements BeanPostProcessor, ApplicationContextAware {

    private ApplicationContext ac;

eq

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ac = applicationContext;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if ("peerAwareInstanceRegistry".equals(beanName)) {
            System.out.println(bean + "," + beanName);
            log.info(bean + "," + beanName);
            CustomInstanceRegistryDecorator decorator = new CustomInstanceRegistryDecorator(
                    ac.getBean(EurekaServerConfig.class),
                    ac.getBean(EurekaClientConfig.class),
                    ac.getBean(ServerCodecs.class),
                    ac.getBean(EurekaClient.class),
                    ac.getBean(WhiteListThird.class)
            );
            decorator.setWhiteListService(profiles);
            decorator.setApplicationContext(ac);
            return decorator;
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }
}

