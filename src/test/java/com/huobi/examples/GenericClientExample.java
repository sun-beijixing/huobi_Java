package com.huobi.examples;

import java.util.List;

import com.huobi.client.GenericClient;
import com.huobi.client.req.generic.CurrencyChainsRequest;
import com.huobi.constant.HuobiOptions;
import com.huobi.model.generic.*;

public class GenericClientExample {

  public static void main(String[] args) {
    GenericClient genericService = GenericClient.create(HuobiOptions.builder().build());

    //获取系统状态
    String systemStatusJSONString = genericService.getSystemStatus();
    System.out.println(systemStatusJSONString);

    System.out.println("----------------------------------------------------");
    //或是市场状态
    MarketStatus marketStatus = genericService.getMarketStatus();
    System.out.println("market status :: "+marketStatus.getMarketStatus());

    System.out.println("----------------------------------------------------");
    //获取所有交易对
    List<Symbol> symbolList = genericService.getSymbols();
    symbolList.forEach(symbol -> {
      System.out.println(symbol.toString());
    });

    System.out.println("----------------------------------------------------");
    //获取所有币种
    List<String> currencyList = genericService.getCurrencys();
    currencyList.forEach(currency -> {
      System.out.println(currency);
    });

    System.out.println("----------------------------------------------------");
    //获取当前系统时间戳
    Long serverTime = genericService.getTimestamp();
    System.out.println("server time:" + serverTime);
    //APIv2 币链参考信息
    List<CurrencyChain> currencyChainList = genericService.getCurrencyChains(CurrencyChainsRequest.builder()
        .currency("usdt")
        .build());

    currencyChainList.forEach(currencyChain -> {
      System.out.println("***************************************");
      System.out.println("currrency chain:"+currencyChain);
      currencyChain.getChains().forEach(chain -> {
        System.out.println("chain:::"+chain.toString());
      });
    });
  }

}
