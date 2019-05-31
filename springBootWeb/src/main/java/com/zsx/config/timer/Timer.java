package com.zsx.config.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by highness on 2017/8/27 0027.
 */
//@Component
public class Timer {

    private static Logger logger = LoggerFactory.getLogger(Timer.class);


//                    秒  分  时  天  月 星期 年
    @Scheduled(cron = "0 0/1 * * * ?") // 1分钟一次
//    @Scheduled(cron = "0/30 * * * * ?") // 30秒一次
    public void test(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(new Date());
        logger.info(time + " 我是info");
        logger.debug(time + " 我是debug");
        logger.error(time + " 我是error");
        logger.warn(time + " 我是warn");
    }


}
/*
一个cron表达式有至少6个（也可能7个）有空格分隔的时间元素。
按顺序依次为
秒（0~59）
分钟（0~59）
小时（0~23）
天（月）（0~31，但是你需要考虑你月的天数）
月（0~11）
天（星期）（1~7 1=SUN 或 SUN，MON，TUE，WED，THU，FRI，SAT）
7.年份（1970－2099）
 */

/**
 cron表达式：
 // 每天早八点到晚八点，间隔2分钟执行任务
 @Scheduled(cron="0 0/2 8-20 * * ?")

 每两秒执行一次
 0/2 * * * * ?
*/