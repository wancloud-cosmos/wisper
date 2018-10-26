package io.wancloud.wisper.service.impl;

import io.wancloud.wisper.model.NotificationElement;
import io.wancloud.wisper.service.SenderService;
import io.wancloud.wisper.utils.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service("mailSenderService")
public class MailSenderServiceImpl implements SenderService {

    private static final Logger logger = LoggerFactory.getLogger(MailSenderServiceImpl.class);

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private TaskExecutor taskExecutor;

    private static final String LF = "\r\n";

    @Value("${wisper.mail.from}")
    private String from;

    @Value("${wisper.mail.targets}")
    private String targets;

    @Override
    public void send(NotificationElement element) {
        StringBuilder text = new StringBuilder("通知如下：").append(LF);
        text.append(element.toString()).append(LF);
        text.append(DateUtil.formatDate(new Date(), DateUtil.SIMPLE)).append(LF);
        text.append("（本邮件自动发送 请不要回复）");

        if (StringUtils.isBlank(targets)) {
            logger.error("邮件接收人列表为空");
            return;
        }

        String[] receivers = targets.split("\\|");
        for (String receiver : receivers) {
            taskExecutor.execute(new MailSender(text.toString(), receiver));
        }
    }

    private class MailSender implements Runnable {

        private String text;

        private String to;

        public MailSender(String text, String to) {
            this.text = text;
            this.to = to;
        }

        @Override
        public void run() {
            try {
                sendMail();
            } catch (Exception e) {
                logger.error("Mail failed to send, receiver: " + to, e);
                sendMail();
            }
        }

        private void sendMail() {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject("[WanCloud] Wisper 自动通知");
            message.setText(text);
            message.setSentDate(new Date());
            mailSender.send(message);

            logger.info("Mail sent successfully, receiver: " + to);
        }

    }

}
