package io.wancloud.wisper.service.impl;

import io.wancloud.wisper.model.NotificationElement;
import io.wancloud.wisper.service.SenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("smsSenderService")
public class SMSSenderServiceImpl implements SenderService {

    private static final Logger logger = LoggerFactory.getLogger(SMSSenderServiceImpl.class);

    @Override
    public void send(NotificationElement element) {
        logger.info("SMS sender is an empty implementation.");

    }
}
