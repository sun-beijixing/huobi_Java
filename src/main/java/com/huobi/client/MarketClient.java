package com.huobi.client;

import java.util.List;

import com.huobi.client.req.market.CandlestickRequest;
import com.huobi.client.req.market.ReqCandlestickRequest;
import com.huobi.client.req.market.SubCandlestickRequest;
import com.huobi.constant.Options;
import com.huobi.constant.enums.ExchangeEnum;
import com.huobi.exception.SDKException;
import com.huobi.model.market.Candlestick;
import com.huobi.model.market.CandlestickEvent;
import com.huobi.model.market.CandlestickReq;
import com.huobi.service.huobi.HuobiMarketService;
import com.huobi.utils.ResponseCallback;

public interface MarketClient {

  List<Candlestick> getCandlestick(CandlestickRequest request);


  void subCandlestick(SubCandlestickRequest request, ResponseCallback<CandlestickEvent> callback);

    /**
     * 历史数据订阅，含有时间段
     * @Description TODO
     * @Author wangwei.0822@163.com
     * @Date 2020/11/23 15:06
     * @Param
     * @return
     **/
    void reqCandlestick(ReqCandlestickRequest request, ResponseCallback<CandlestickReq> callback);


  static MarketClient create(Options options) {

    if (options.getExchange().equals(ExchangeEnum.HUOBI)) {
      return new HuobiMarketService(options);
    }
    throw new SDKException(SDKException.INPUT_ERROR, "Unsupport Exchange.");
  }


}
