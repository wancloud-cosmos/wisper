package io.wancloud.wisper.web.controller;

import io.wancloud.wisper.model.DemoElement;
import io.wancloud.wisper.service.SenderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/notification")
public class NotificationController {

    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    @Autowired
    private SenderService mailSenderService;

    /**
     * 邮件通知
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/mail", method = RequestMethod.POST)
    @ResponseBody
    public boolean send(HttpServletRequest request) {
        DemoElement element = new DemoElement("姚明", 226);
        mailSenderService.send(element);
        return true;
    }

}
