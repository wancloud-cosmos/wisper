package io.wancloud.wisper.task;

import io.wancloud.wisper.constants.ChannelEnum;
import io.wancloud.wisper.service.SenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class NotificationTask implements ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(NotificationTask.class);

    private ApplicationContext applicationContext;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TaskExecutor taskExecutor;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void notifying() {

        logger.info("Send encrypted messages 定时任务启动");


//        // send mail
//        NotificationElement element = new NotificationElement();
//        element.setBlocks(blocks);
//        element.setTestnet(testnet);
//        element.setEnabled(isEnabled);
//        element.setStaking(isStaking);
//        element.setBalance(balance);
//        element.setStake(stake);
//        element.setWeight(weight);
//        element.setNetstakeweight(netstakeweight);
//        double expect = (float) expectedtime / 60 / 60;
//        element.setExpectedtime(new BigDecimal(expect).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
//        element.setDatetime(DateUtil.formatDate(new Date(), DateUtil.SIMPLE));
//
//        List<SenderService> list = senderServiceList(ChannelEnum.CHANNEL_MAIL.getCode(), ChannelEnum.CHANNEL_SMS.getCode());//目前只发邮件, 短信空实现
//        for (SenderService senderService : list) {
//            try {
//                taskExecutor.execute(() -> senderService.send(element));
//            } catch (Exception e) {
//                logger.error("message sending failed, element: " + element.toString(), e);
//                return;
//            }
//        }
    }

    /**
     * 构造通知服务列表
     *
     * @param channels
     * @return
     */
    private List<SenderService> senderServiceList(String... channels) {
        if (channels == null || channels.length == 0) {
            return Collections.emptyList();
        }

        List<SenderService> list = new ArrayList<>();
        for (String channel : channels) {
            ChannelEnum c = ChannelEnum.findEnumByCode(channel);
            if (c != null) {
                SenderService sender = (SenderService) applicationContext.getBean(c.getBeanName());
                list.add(sender);
            }
        }
        return list;
    }

}
