package com.huobi.examples;

import com.huobi.client.MarketClient;
import com.huobi.client.req.market.*;
import com.huobi.constant.HuobiOptions;
import com.huobi.constant.enums.CandlestickIntervalEnum;
import com.huobi.constant.enums.DepthLevels;
import com.huobi.constant.enums.DepthSizeEnum;
import com.huobi.constant.enums.DepthStepEnum;
import com.huobi.model.market.*;

import java.util.List;

public class MyMarketClientExampleTest {

    public static void main(String[] args) {
        newDatas();
//        historyDatas();
//        sub();
//        sub2();
    }

    private static void newDatas() {
        MarketClient marketClient = MarketClient.create(new HuobiOptions());

//    String symbol = "btcusdt";
        String symbol = "uniusdt";
//    String symbol = "linkusdt";

        List<Candlestick> list = marketClient.getCandlestick(CandlestickRequest.builder()
                .symbol(symbol)
                .interval(CandlestickIntervalEnum.MIN30)
                .size(2000)
                .build());

        list.forEach(candlestick -> {
            System.out.println(candlestick.toString());
        });
    }

    private static void historyDatas() {
        String symbol = "btcusdt";
        MarketClient marketClient = MarketClient.create(new HuobiOptions());
        List<MarketTrade> marketHistoryTradeList = marketClient.getMarketHistoryTrade(MarketHistoryTradeRequest
                .builder()
                .symbol(symbol).size(10000)
                .build());
        marketHistoryTradeList.forEach(marketTrade -> {
            System.out.println(marketTrade.toString());
        });
    }

    private static void sub(){
        String symbol = "btcusdt";
        MarketClient marketClient = MarketClient.create(new HuobiOptions());
        marketClient.subCandlestick(SubCandlestickRequest.builder()
                .symbol(symbol)
                .interval(CandlestickIntervalEnum.MIN15)
                .build(), (candlestick) -> {

            System.out.println(candlestick.toString());
        });
    }

    /**
     * 利用socket订阅方式获取历史数据
     * @Description TODO
     * @Author wangwei.0822@163.com
     * @Date 2020/11/23 15:09
     * @Param
     * @return
     **/
    private static void sub2(){
        String symbol = "btcusdt";
        MarketClient marketClient = MarketClient.create(new HuobiOptions());
        marketClient.reqCandlestick(ReqCandlestickRequest.builder()
                .symbol(symbol)
                .interval(CandlestickIntervalEnum.MIN15)
                .from(1606048200l)
                .to(1606015800l)
                .build(), candlestickReq -> {

            System.out.println(candlestickReq.toString());
            candlestickReq.getCandlestickList().forEach(candlestick -> {
                System.out.println("candlestick:" + candlestick.toString());
            });
        });

//        ReqCandlestickRequest request, ResponseCallback<CandlestickReq> callback



    }

}
