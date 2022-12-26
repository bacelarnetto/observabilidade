package br.com.ms.custom.loggers;

import org.springframework.beans.BeansException;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class BuildPropertiesInitializer implements ApplicationContextAware {
    public static BuildProperties BUILDE_PROPERTIES;

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException{
        if(applicationContext.containsBean("buildProperties")){
            BUILDE_PROPERTIES = (BuildProperties) applicationContext.getBean(BuildProperties.class);
        }
    }
}
