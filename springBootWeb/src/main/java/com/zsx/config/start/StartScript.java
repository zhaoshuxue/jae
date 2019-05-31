package com.zsx.config.start;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * Created by highness on 2017/10/12 0012.
 */
@Component
public class StartScript implements InitializingBean{

    /**
     * 然后执行这个
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("1111111");
    }

    /**
     * 先执行这个
     */
    @PostConstruct
    public void init(){
        System.out.println("2222222");
    }
}
