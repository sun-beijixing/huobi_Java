package com.huobi;

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

    static int aaa=0;

    public static void main(String[] args) {
//        newDatas();
//        sub();
        sub3();
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

    /**
     * socket订阅，获取kline数据
     * @Description TODO
     * @Author wangwei.0822@163.com
     * @Date 2020/11/30 19:50
     * @Param
     * @return
     **/
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
     * 利用socket订阅方式获取历史数据,时间范围
     * @Description TODO
     * @Author wangwei.0822@163.com
     * @Date 2020/11/23 15:09
     * @Param
     * @return
     **/
    private static void sub3(){
        String symbol = "btcusdt";
        long from=1604160000;
        long to=1606735800;

        MarketClient marketClient = MarketClient.create(new HuobiOptions());
        marketClient.reqCandlestick(ReqCandlestickRequest.builder()
                .symbol(symbol)
//                .interval(CandlestickIntervalEnum.MIN60)//每次最多返回300条,以此类推时间范围为（60m*300）=18000分钟时间范围，超出则报异常from: 1601481600 to: 1604160000, out of range
//                .interval(CandlestickIntervalEnum.HOUR4)//每次最多返回300条,以此类推时间范围为（4h*300）=1200小时时间范围，超出则报异常from: 1601481600 to: 1604160000, out of range
                .interval(CandlestickIntervalEnum.DAY1)//每次最多返回300条,以此类推时间范围为（1d*300）=300天时间范围，超出则报异常from: 1601481600 to: 1604160000, out of range
                .from(from)//2020-10-01 00:00:00
                .to(to)
                .build(), candlestickReq -> {

            aaa++;

            System.err.println(aaa+"========================================================================");

            System.out.println(candlestickReq.toString());
            candlestickReq.getCandlestickList().forEach(candlestick -> {
                System.out.println("candlestick:" + candlestick.toString());
            });
        });


    }

}
