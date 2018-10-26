package io.wancloud.wisper.service;

import io.wancloud.wisper.model.NotificationElement;

public interface SenderService {

    public void send(NotificationElement element);

}
