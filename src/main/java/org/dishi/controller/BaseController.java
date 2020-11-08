package org.dishi.controller;

import org.dishi.service.EmailService;
import org.dishi.service.MemoService;
import org.dishi.service.UserService;
import org.quartz.Scheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController {
    @Autowired
    protected UserService userService;

    @Autowired
    protected EmailService emailService;

    @Autowired
    protected MemoService memoService;

    @Autowired
    protected Scheduler scheduler;
}
