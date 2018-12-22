package org.tree.commons.support.service.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tree.commons.annotation.Comment;
import org.tree.commons.utils.RandomUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author er_dong_chen
 * @date 2018/12/17
 */
@Comment("验证码服务")
@Service
public class AuthCodeService {
    private Map<String, String> cache = new HashMap<>(32);

    @Autowired
    private EmailSender emailSender;
    @Autowired
    private ShortMessageSender shortMessageSender;

    public AuthCodeService() {
    }

    public AuthCodeService(EmailSender emailSender, ShortMessageSender shortMessageSender) {
        this.emailSender = emailSender;
        this.shortMessageSender = shortMessageSender;
    }

    @Comment("发送验证码")
    public boolean sendAuthCode(String to) {
        String code = RandomUtils.number(4);
        boolean success = true;
        if (to.matches("\\w+@.*\\.com"))
            emailSender.send(to, "验证码", code);
        else if (to.matches("\\d+"))
            shortMessageSender.send(to, String.format("{'code':'%s'}", code));
        else
            success = false;

        if (success)
            cache.put(to, code);
        return success;
    }

    @Comment("校对验证码")
    public boolean confirmAuthCode(String to, String code) {
        boolean success = true;
        if (to == null || code == null || !code.equals(cache.get(to)))
            success = false;

        if (success)
            cache.remove(to);

        return success;
    }
}
