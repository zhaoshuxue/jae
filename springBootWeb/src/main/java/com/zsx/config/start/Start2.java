package com.zsx.config.start;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Created by highness on 2017/10/12 0012.
 */
@Component
public class Start2 implements ApplicationListener<ContextRefreshedEvent> {
    /**
     *
     * @param contextRefreshedEvent
     *
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        System.out.println("33333");
    }
}
