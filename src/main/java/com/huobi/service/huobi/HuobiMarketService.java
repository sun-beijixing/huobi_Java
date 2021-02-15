package com.huobi.service.huobi;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huobi.client.MarketClient;
import com.huobi.client.req.market.CandlestickRequest;
import com.huobi.client.req.market.ReqCandlestickRequest;
import com.huobi.client.req.market.SubCandlestickRequest;
import com.huobi.constant.Options;
import com.huobi.constant.WebSocketConstants;
import com.huobi.model.market.Candlestick;
import com.huobi.model.market.CandlestickEvent;
import com.huobi.model.market.CandlestickReq;
import com.huobi.service.huobi.connection.HuobiRestConnection;
import com.huobi.service.huobi.connection.HuobiWebSocketConnection;
import com.huobi.service.huobi.parser.market.CandlestickEventParser;
import com.huobi.service.huobi.parser.market.CandlestickParser;
import com.huobi.service.huobi.parser.market.CandlestickReqParser;
import com.huobi.service.huobi.signature.UrlParamsBuilder;
import com.huobi.utils.InputChecker;
import com.huobi.utils.ResponseCallback;
import com.huobi.utils.SymbolUtils;

import java.util.ArrayList;
import java.util.List;

public class HuobiMarketService implements MarketClient {

  private Options options;

  private HuobiRestConnection restConnection;

  public HuobiMarketService(Options options) {
    this.options = options;
    restConnection = new HuobiRestConnection(options);
  }

    //K 线数据（蜡烛图）
    public static final String REST_CANDLESTICK_PATH = "/market/history/kline";

    //一旦K线数据产生，Websocket服务器将通过此订阅主题接口推送至客户端
    public static final String WEBSOCKET_CANDLESTICK_TOPIC = "market.$symbol$.kline.$period$";

  @Override
  public List<Candlestick> getCandlestick(CandlestickRequest request) {

    // 参数检查
    InputChecker.checker()
        .checkSymbol(request.getSymbol())
        .checkRange(request.getSize(), 1, 2000, "size")
        .shouldNotNull(request.getInterval(), "CandlestickInterval");

    // 参数构建
    UrlParamsBuilder paramBuilder = UrlParamsBuilder.build()
        .putToUrl("symbol", request.getSymbol())
        .putToUrl("period", request.getInterval().getCode())
        .putToUrl("size", request.getSize());

    JSONObject json = restConnection.executeGet(REST_CANDLESTICK_PATH, paramBuilder);
    JSONArray data = json.getJSONArray("data");
    return new CandlestickParser().parseArray(data);
  }

  @Override
  public void subCandlestick(SubCandlestickRequest request, ResponseCallback<CandlestickEvent> callback) {

    // 检查参数
    InputChecker.checker()
        .shouldNotNull(request.getSymbol(), "symbol")
        .shouldNotNull(request.getInterval(), "interval");
    // 格式化symbol为数组
    List<String> symbolList = SymbolUtils.parseSymbols(request.getSymbol());

    // 检查数组
    InputChecker.checker()
        .checkSymbolList(symbolList);

    List<String> commandList = new ArrayList<>(symbolList.size());
    symbolList.forEach(symbol -> {

      String topic = WEBSOCKET_CANDLESTICK_TOPIC
          .replace("$symbol$", symbol)
          .replace("$period$", request.getInterval().getCode());

      JSONObject command = new JSONObject();
      command.put("sub", topic);
      command.put("id", System.nanoTime());
      commandList.add(command.toJSONString());
    });

    HuobiWebSocketConnection.createMarketConnection(options, commandList, new CandlestickEventParser(), callback, false);
  }

  /**
   * 历史数据订阅，含有时间段
   * @Description TODO
   * @Author wangwei.0822@163.com
   * @Date 2020/11/23 15:02
   * @Param
   * @return
   **/
  public void reqCandlestick(ReqCandlestickRequest request, ResponseCallback<CandlestickReq> callback) {

    // 检查参数
    InputChecker.checker()
        .shouldNotNull(request.getSymbol(), "symbol")
        .shouldNotNull(request.getInterval(), "interval");

    String topic = WEBSOCKET_CANDLESTICK_TOPIC
        .replace("$symbol$", request.getSymbol())
        .replace("$period$", request.getInterval().getCode());

    JSONObject command = new JSONObject();
    command.put(WebSocketConstants.OP_REQ, topic);
    command.put("id", System.nanoTime());
    if (request.getFrom() != null) {
      command.put("from", request.getFrom());
    }
    if (request.getTo() != null) {
      command.put("to", request.getTo());
    }
    List<String> commandList = new ArrayList<>(1);
    commandList.add(command.toJSONString());

    HuobiWebSocketConnection.createMarketConnection(options, commandList, new CandlestickReqParser(), callback, true);
  }


}
