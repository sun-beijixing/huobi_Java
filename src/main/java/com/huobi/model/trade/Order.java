package com.huobi.model.trade;

import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.huobi.constant.enums.StopOrderOperatorEnum;

/**
 * @ClassName Order
 * @Description TODO
 * @Author wangwei.0822@163.com
 * @Data 2021/1/25 17:20
 *
 * order-id : 订单的唯一编号
 * client-order-id : 客户自定义ID，该ID在下单时传入，与下单成功后返回的order-id对应，该ID 24小时内有效。 允许的字符包括字母(大小写敏感)、数字、下划线 (_)和横线(-)，最长64位
 * match-id : 订单在撮合中的顺序编号
 * trade-id : 成交的唯一编号
 **/
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Order {

  private Long id;

  private String symbol;

  private Long accountId;

  private BigDecimal amount;

  private BigDecimal price;

  private String type;

  private BigDecimal filledAmount;

  private BigDecimal filledCashAmount;

  private BigDecimal filledFees;

  private String source;

  private String state;

  private Long createdAt;

  private Long canceledAt;

  private Long finishedAt;

  private BigDecimal stopPrice;

  private String clientOrderId;

  @JSONField(deserialize = false)
  private StopOrderOperatorEnum operator;

}
