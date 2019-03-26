package org.tree.commons.support.service.alipay;

import com.alibaba.fastjson.JSON;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.*;
import com.alipay.api.response.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.tree.commons.support.BaseConfig;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;

/**
 * @author er_dong_chen
 * @date 18-10-26
 */
@Lazy
@Component
public class AliPayCore extends BaseConfig {

    private AlipayClient alipayClient;
    public final static String DEV = "https://openapi.alipaydev.com/gateway.do";
    public final static String PROD = "https://openapi.alipay.com/gateway.do";

    @Value("${aliPay.appId:}")
    private String appId;
    @Value("${aliPay.aliPublicKey:}")
    private String aliPublicKey;
    @Value("${aliPay.privateKey:}")
    private String privateKey;
    @Value("${aliPay.url:}")
    private String url;

    public AliPayCore() {
    }

    public AliPayCore(String appId, String private_key, String ali_public_key, String url) {
        alipayClient = new DefaultAlipayClient(url, appId, private_key, "json", "utf-8", ali_public_key, "RSA2");
    }

    @PostConstruct
    public void init() {
        if (url.length() == 0)
            url = debugEnable ? AliPayCore.DEV : AliPayCore.PROD;

        if (appId.length() == 0 || aliPublicKey.length() == 0 || privateKey.length() == 0)
            return;

        alipayClient = new DefaultAlipayClient(url, appId, privateKey,
                "json", "utf-8", aliPublicKey, "RSA2");
    }

    /* 支付 */
    public String pay(String out_trade_no, String subject, BigDecimal total_amount,
                      String return_url, String notify_url, Object... body) throws Exception {
        String content = "{'product_code':'FAST_INSTANT_TRADE_PAY','out_trade_no':'" + out_trade_no + "'," +
                "'subject':'" + subject + "','total_amount':" + total_amount +
                (body.length > 0 ? ",'body':'" + body[0] + "'" : "") + "}";

        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        request.setNotifyUrl(notify_url);
        request.setReturnUrl(return_url);
        request.setBizContent(content);
        AlipayTradePagePayResponse response = alipayClient.pageExecute(request, "GET"); // 必须是 GET 模式
        return response.getBody();
    }

    /* 查询订单 */
    public String query(String out_trade_no) throws Exception {
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        request.setBizContent("{'out_trade_no':'" + out_trade_no + "'}");
        AlipayTradeQueryResponse response = alipayClient.execute(request);
        System.out.println("\n" + JSON.toJSONString(response));
        return response.getTradeStatus();
    }

    /* 关闭订单 */
    public boolean close(String out_trade_no) throws Exception {
        AlipayTradeCloseRequest request = new AlipayTradeCloseRequest();
        request.setBizContent("{'out_trade_no':'" + out_trade_no + "'}");
        AlipayTradeCloseResponse response = alipayClient.execute(request);
        System.out.println("\n" + JSON.toJSONString(response));
        return response.isSuccess();
    }

    /* 退款（必须与支付金额一致）*/
    public boolean refund(String out_trade_no, BigDecimal refund_amount) throws Exception {
        AlipayTradeRefundRequest request = new AlipayTradeRefundRequest();
        request.setBizContent("{'out_trade_no':'" + out_trade_no + "','refund_amount':" + refund_amount + "}");
        AlipayTradeRefundResponse response = alipayClient.execute(request);
        System.out.println("\n" + JSON.toJSONString(response));
        return response.isSuccess();
    }

    /* 查询退款记录 */
    public AlipayTradeFastpayRefundQueryResponse refundQuery(String out_trade_no) throws Exception {
        AlipayTradeFastpayRefundQueryRequest request = new AlipayTradeFastpayRefundQueryRequest();
        request.setBizContent("{'out_trade_no':'" + out_trade_no + "','out_request_no':'" + out_trade_no + "'}");
        AlipayTradeFastpayRefundQueryResponse response = alipayClient.execute(request);
        System.out.println("\n" + JSON.toJSONString(response));
        return response;
    }

}
