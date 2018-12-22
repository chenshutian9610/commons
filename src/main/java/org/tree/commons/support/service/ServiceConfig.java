package org.tree.commons.support.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tree.commons.support.Config;

/**
 * @author er_dong_chen
 * @date 2018/12/21
 */
@Component
public class ServiceConfig extends Config {

    /****************************** AliPayCore *******************************/

    @Value("${aliPay.appId:}")
    private String aliPayAppId;
    @Value("${aliPay.aliPublicKey:}")
    private String aliPublicKey;
    @Value("${aliPay.privateKey:}")
    private String aliPayPrivateKey;
    @Value("${aliPay.url:}")
    private String aliPayUrl;

    /****************************** EmailSender *******************************/

    @Value("${email.host:}")
    private String emailHost;
    @Value("${email.port:25}")  // 25 是 SMTP 标准端口
    private int emailPort;
    @Value("${email.username:}")
    private String emailUsername;
    @Value("${email.password:}")
    private String emailPassword;

    /****************************** ShortMessageSender *******************************/

    @Value("${ali.sms.accessKeyId:}")
    private String aliSMSAccessKeyId;
    @Value("${ali.sms.accessKeySecret:}")
    private String aliSMSAccessKeySecret;
    @Value("${ali.sms.signName:}")
    private String aliSMSSignName;
    @Value("${ali.sms.templateCode:}")
    private String aliSMSATemplateCode;

    /****************************** getter && setter *******************************/

    public String getAliPayAppId() {
        return aliPayAppId;
    }

    public void setAliPayAppId(String aliPayAppId) {
        this.aliPayAppId = aliPayAppId;
    }

    public String getAliPublicKey() {
        return aliPublicKey;
    }

    public void setAliPublicKey(String aliPublicKey) {
        this.aliPublicKey = aliPublicKey;
    }

    public String getAliPayPrivateKey() {
        return aliPayPrivateKey;
    }

    public void setAliPayPrivateKey(String aliPayPrivateKey) {
        this.aliPayPrivateKey = aliPayPrivateKey;
    }

    public String getAliPayUrl() {
        return aliPayUrl;
    }

    public void setAliPayUrl(String aliPayUrl) {
        this.aliPayUrl = aliPayUrl;
    }

    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public int getEmailPort() {
        return emailPort;
    }

    public void setEmailPort(int emailPort) {
        this.emailPort = emailPort;
    }

    public String getEmailUsername() {
        return emailUsername;
    }

    public void setEmailUsername(String emailUsername) {
        this.emailUsername = emailUsername;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public String getAliSMSAccessKeyId() {
        return aliSMSAccessKeyId;
    }

    public void setAliSMSAccessKeyId(String aliSMSAccessKeyId) {
        this.aliSMSAccessKeyId = aliSMSAccessKeyId;
    }

    public String getAliSMSAccessKeySecret() {
        return aliSMSAccessKeySecret;
    }

    public void setAliSMSAccessKeySecret(String aliSMSAccessKeySecret) {
        this.aliSMSAccessKeySecret = aliSMSAccessKeySecret;
    }

    public String getAliSMSSignName() {
        return aliSMSSignName;
    }

    public void setAliSMSSignName(String aliSMSSignName) {
        this.aliSMSSignName = aliSMSSignName;
    }

    public String getAliSMSATemplateCode() {
        return aliSMSATemplateCode;
    }

    public void setAliSMSATemplateCode(String aliSMSATemplateCode) {
        this.aliSMSATemplateCode = aliSMSATemplateCode;
    }
}
