package org.dishi.service;

import org.dishi.message.MailMessage;

public interface EmailService {
    void send(MailMessage mm);
}
