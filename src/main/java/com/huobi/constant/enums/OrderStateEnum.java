package com.huobi.constant.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SUBMITTED, PARTIALFILLED, CANCELLING. PARTIALCANCELED FILLED CANCELED CREATED
 * created：已创建，该状态订单尚未进入撮合队列。
 * submitted : 已挂单等待成交，该状态订单已进入撮合队列当中。
 * partial-filled : 部分成交，该状态订单在撮合队列当中，订单的部分数量已经被市场成交，等待剩余部分成交。
 * filled : 已成交。该状态订单不在撮合队列中，订单的全部数量已经被市场成交。
 * partial-canceled : 部分成交撤销。该状态订单不在撮合队列中，此状态由partial-filled转化而来，订单数量有部分被成交，但是被撤销。
 * canceling : 撤销中。该状态订单正在被撤销的过程中，因订单最终需在撮合队列中剔除才会被真正撤销，所以此状态为中间过渡态。
 * canceled : 已撤销。该状态订单不在撮合订单中，此状态订单没有任何成交数量，且被成功撤销
 */
@Getter
@AllArgsConstructor
public enum OrderStateEnum {
  SUBMITTED("submitted"),
  CREATED("created"),
  PARTIALFILLED("partial-filled"),
  CANCELLING("cancelling"),
  PARTIALCANCELED("partial-canceled"),
  FILLED("filled"),
  CANCELED("canceled");

  private final String code;

  public static OrderStateEnum find(String code) {
    for (OrderStateEnum stateEnum : OrderStateEnum.values()) {
      if (stateEnum.getCode().equals(code)) {
        return stateEnum;
      }
    }
    return null;
  }
}
