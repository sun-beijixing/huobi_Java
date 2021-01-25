package com.huobi.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * buy-market, sell-market, buy-limit, buy-ioc, sell-ioc,
 * buy-limit-maker, sell-limit-maker, buy-stop-limit, sell-stop-limit.
 *
 * 方向：
 *
 * buy: 买
 * sell: 卖
 * 行为类型：
 *
 * market：市价单，该类型订单仅需指定下单金额或下单数量，不需要指定价格，订单在进入撮合时，会直接与对手方进行成交，直至金额或数量低于最小成交金额或成交数量为止。
 * limit：限价单，该类型订单需指定下单价格，下单数量。
 * limit-maker：只做maker单，即限价挂单，该订单在进入撮合时，只能作为maker进入市场深度，若订单会被成交，则撮合会直接拒绝该订单。
 * ioc：立即成交或取消（immediately or cancel），该订单在进入撮合后，若不能直接成交，则会被直接取消（部分成交后，剩余部分也会被取消）。
 * limit-fok： 立即完全成交否则完全取消（fill or kill），该订单在进入撮合后，若不能立即完全成交，则会被完全取消。
 * market-grid：网格交易市价单（暂不支持API下单）。
 * limit-grid：网格交易限价单（暂不支持API下单）。
 * stop-limit：止盈止损单，设置高于或低于市场价格的订单，当订单到达触发价格后，才会正式的进入撮合队列。该类型已被策略委托订单代替，请使用策略委托订单。
 */
@Getter
@AllArgsConstructor
public enum OrderTypeEnum {
  BUY_MARKET("buy-market"),
  SELL_MARKET("sell-market"),
  BUY_LIMIT("buy-limit"),
  SELL_LIMIT("sell-limit"),
  BUY_IOC("buy-ioc"),
  SELL_IOC("sell-ioc"),
  BUY_LIMIT_MAKER("buy-limit-maker"),
  SELL_LIMIT_MAKER("sell-limit-maker"),
  BUY_STOP_LIMIT("buy-stop-limit"),
  SELL_STOP_LIMIT("sell-stop-limit"),

  INVALID("invalid");

  private final String code;


  public static OrderTypeEnum find(String code) {
    for (OrderTypeEnum typeEnum : OrderTypeEnum.values()) {
      if (typeEnum.getCode().equals(code)) {
        return typeEnum;
      }
    }
    return null;
  }

}
