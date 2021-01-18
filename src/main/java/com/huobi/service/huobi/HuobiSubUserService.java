package com.huobi.service.huobi;

import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.huobi.client.SubUserClient;
import com.huobi.client.req.account.TransferSubuserRequest;
import com.huobi.client.req.subuser.GetApiKeyListRequest;
import com.huobi.client.req.subuser.GetSubUserAccountListRequest;
import com.huobi.client.req.subuser.GetSubUserDepositRequest;
import com.huobi.client.req.subuser.GetSubUserListRequest;
import com.huobi.client.req.subuser.SubUserApiKeyDeletionRequest;
import com.huobi.client.req.subuser.SubUserApiKeyGenerationRequest;
import com.huobi.client.req.subuser.SubUserApiKeyModificationRequest;
import com.huobi.client.req.subuser.SubUserCreationRequest;
import com.huobi.client.req.subuser.SubUserManagementRequest;
import com.huobi.client.req.subuser.SubUserTradableMarketRequest;
import com.huobi.client.req.subuser.SubUserTransferabilityRequest;
import com.huobi.constant.Options;
import com.huobi.model.account.AccountBalance;
import com.huobi.model.account.SubuserAggregateBalance;
import com.huobi.model.subuser.GetApiKeyListResult;
import com.huobi.model.subuser.GetSubUserAccountListResult;
import com.huobi.model.subuser.GetSubUserDepositResult;
import com.huobi.model.subuser.GetSubUserListResult;
import com.huobi.model.subuser.SubUserApiKeyGenerationResult;
import com.huobi.model.subuser.SubUserApiKeyModificationResult;
import com.huobi.model.subuser.SubUserCreationInfo;
import com.huobi.model.subuser.SubUserManagementResult;
import com.huobi.model.subuser.SubUserState;
import com.huobi.model.subuser.SubUserTradableMarketResult;
import com.huobi.model.subuser.SubUserTransferabilityResult;
import com.huobi.model.wallet.DepositAddress;
import com.huobi.service.huobi.connection.HuobiRestConnection;
import com.huobi.service.huobi.parser.account.AccountBalanceParser;
import com.huobi.service.huobi.parser.account.SubuserAggregateBalanceParser;
import com.huobi.service.huobi.parser.subuser.GetApiKeyListResultParser;
import com.huobi.service.huobi.parser.subuser.GetSubUserAccountListResultParser;
import com.huobi.service.huobi.parser.subuser.GetSubUserDepositResultParser;
import com.huobi.service.huobi.parser.subuser.GetSubUserListResultParser;
import com.huobi.service.huobi.parser.subuser.SubUserApiKeyGenerationResultParser;
import com.huobi.service.huobi.parser.subuser.SubUserApiKeyModificationResultParser;
import com.huobi.service.huobi.parser.subuser.SubUserCreationInfoParser;
import com.huobi.service.huobi.parser.subuser.SubUserManagementResultParser;
import com.huobi.service.huobi.parser.subuser.SubUserStateParser;
import com.huobi.service.huobi.parser.subuser.SubUserTradableMarketResultParser;
import com.huobi.service.huobi.parser.subuser.SubUserTransferabilityResultParser;
import com.huobi.service.huobi.parser.wallet.DepositAddressParser;
import com.huobi.service.huobi.signature.UrlParamsBuilder;
import com.huobi.utils.InputChecker;

public class HuobiSubUserService implements SubUserClient {

    //母用户查询子用户各币种账户余额
    public static final String GET_SUBUSER_ACCOUNT_BALANCE_PATH = "/v1/account/accounts/{sub-uid}";
    //母用户查询其下所有子用户的各币种汇总余额
    public static final String GET_SUBUSER_AGGREGATE_BALANCE_PATH = "/v1/subuser/aggregate-balance";
    //母用户执行母子用户之间的划转
    public static final String TRANSFER_SUBUSER_PATH = "/v1/subuser/transfer";
    //此接口用于母用户进行子用户创建，单次最多50个
    public static final String SUBUSER_CREATION_PATH = "/v2/sub-user/creation";
    //母用户通过此接口可获取所有子用户的UID列表及各用户状态
    public static final String GET_SUBUSER_LIST_PATH = "/v2/sub-user/user-list";
    //母用户通过此接口可获取特定子用户的用户状态
    public static final String GET_SUBUSER_STATE_PATH = "/v2/sub-user/user-state";
    //此接口用于母用户对其下一个子用户进行冻结和解冻操作
    public static final String SUBUSER_MANAGEMENT_PATH = "/v2/sub-user/management";
    //母用户通过此接口可获取特定子用户的Account ID列表及各账户状态
    public static final String GET_SUBUSER_ACCOUNT_LIST_PATH = "/v2/sub-user/account-list";
    //此接口用于母用户批量设置子用户的资产转出权限。 子用户的资金转出权限包括： - 由子用户现货（spot）账户转出至同一母用户下另一子用户的现货（spot）账户； - 由子用户现货（spot）账户转出至母用户现货（spot）账户（无须设置，默认开通）。
    public static final String SUBUSER_TRANSFERABILITY_PATH = "/v2/sub-user/transferability";
    //此接口用于母用户批量设置子用户的交易权限。 子用户的现货交易权限默认开通无须设置。
    public static final String SUBUSER_TRADABLE_MARKET_PATH = "/v2/sub-user/tradable-market";
    //此接口用于母用户创建子用户的API key
    public static final String SUBUSER_APIKEY_GENERATION_PATH = "/v2/sub-user/api-key-generation";
    //此接口用于母用户修改子用户的API key
    public static final String SUBUSER_APIKEY_MODIFICATION_PATH = "/v2/sub-user/api-key-modification";
    //此接口用于母用户删除子用户的API key
    public static final String SUBUSER_APIKEY_DELETION_PATH = "/v2/sub-user/api-key-deletion";
    //此接口用于母用户查询自身的API key信息，以及母用户查询子用户的API key信息
    public static final String GET_SUBUSER_APIKEY_PATH = "/v2/user/api-key";
    //此节点用于母用户查询子用户特定币种（IOTA除外）在其所在区块链中的充币地址，限母用户可用
    public static final String GET_SUBUSER_DEPOSIT_ADDRESS_PATH = "/v2/sub-user/deposit-address";
    //此节点用于母用户查询子用户充值记录，限母用户可用
    public static final String GET_SUBUSER_DEPOSIT_PATH = "/v2/sub-user/query-deposit";
    //此接口用于母子用户查询本用户UID
    public static final String GET_UID_PATH = "/v2/user/uid";


  private Options options;

  private HuobiRestConnection restConnection;

  public HuobiSubUserService(Options options) {
    this.options = options;
    this.restConnection = new HuobiRestConnection(options);
  }

  @Override
  public List<SubUserCreationInfo> subuserCreation(SubUserCreationRequest request) {

    InputChecker checker = InputChecker.checker().checkList(request.getUserList(), 1, 50, "userList");
    request.getUserList().forEach(param -> {
      checker.shouldNotNull(param.getUserName(), "userName");
    });

    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToPost("userList", request.getUserList());

    JSONObject jsonObject = restConnection.executePostWithSignature(SUBUSER_CREATION_PATH, builder);
    JSONArray jsonArray = jsonObject.getJSONArray("data");
    return new SubUserCreationInfoParser().parseArray(jsonArray);
  }

  @Override
  public GetSubUserListResult getSubUserList(GetSubUserListRequest request) {

    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToUrl("fromId", request.getFromId());

    JSONObject jsonObject = restConnection.executeGetWithSignature(GET_SUBUSER_LIST_PATH, builder);
    return new GetSubUserListResultParser().parse(jsonObject);
  }

  public SubUserState getSubuserState(Long subUid) {

    InputChecker.checker().shouldNotNull(subUid, "subUid");
    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToUrl("subUid", subUid);

    JSONObject jsonObject = restConnection.executeGetWithSignature(GET_SUBUSER_STATE_PATH, builder);
    JSONObject data = jsonObject.getJSONObject("data");
    return new SubUserStateParser().parse(data);
  }

  public SubUserManagementResult subuserManagement(SubUserManagementRequest request) {

    InputChecker.checker()
        .shouldNotNull(request.getSubUid(), "subUid")
        .shouldNotNull(request.getAction(), "action");

    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToPost("subUid", request.getSubUid())
        .putToPost("action", request.getAction().getAction());

    JSONObject jsonObject = restConnection.executePostWithSignature(SUBUSER_MANAGEMENT_PATH, builder);
    JSONObject data = jsonObject.getJSONObject("data");
    return new SubUserManagementResultParser().parse(data);
  }

  public GetSubUserAccountListResult getSubuserAccountList(GetSubUserAccountListRequest request) {
    InputChecker.checker()
        .shouldNotNull(request.getSubUid(), "subUid");

    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToUrl("subUid", request.getSubUid());

    JSONObject jsonObject = restConnection.executeGetWithSignature(GET_SUBUSER_ACCOUNT_LIST_PATH, builder);
    JSONObject data = jsonObject.getJSONObject("data");
    return new GetSubUserAccountListResultParser().parse(data);
  }

  public SubUserTransferabilityResult subuserTransferability(SubUserTransferabilityRequest request) {
    InputChecker.checker()
        .shouldNotNull(request.getSubUids(), "subUids")
        .shouldNotNull(request.getTransferrable(), "transferrable");

    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToPost("subUids", request.getSubUids())
        .putToPost("accountType", request.getAccountType().getAccountType())
        .putToPost("transferrable", request.getTransferrable());

    JSONObject jsonObject = restConnection.executePostWithSignature(SUBUSER_TRANSFERABILITY_PATH, builder);
    return new SubUserTransferabilityResultParser().parse(jsonObject);
  }

  public SubUserTradableMarketResult subuserTradableMarket(SubUserTradableMarketRequest request) {
    InputChecker.checker()
        .shouldNotNull(request.getSubUids(), "subUids")
        .shouldNotNull(request.getAccountType(), "accountType")
        .shouldNotNull(request.getActivation(), "activation");

    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToPost("subUids", request.getSubUids())
        .putToPost("accountType", request.getAccountType().getAccountType())
        .putToPost("activation", request.getActivation().getActivation());

    JSONObject jsonObject = restConnection.executePostWithSignature(SUBUSER_TRADABLE_MARKET_PATH, builder);
    return new SubUserTradableMarketResultParser().parse(jsonObject);
  }

  public SubUserApiKeyGenerationResult subuserApiKeyGeneration(SubUserApiKeyGenerationRequest request) {
    InputChecker.checker()
        .shouldNotNull(request.getOtpToken(), "otpToken")
        .shouldNotNull(request.getSubUid(), "subUid")
        .shouldNotNull(request.getNote(), "note")
        .shouldNotNull(request.getPermission(), "permission");

    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToPost("otpToken", request.getOtpToken())
        .putToPost("subUid", request.getSubUid())
        .putToPost("note", request.getNote())
        .putToPost("permission", request.getPermission())
        .putToPost("ipAddresses", request.getIpAddresses());

    JSONObject jsonObject = restConnection.executePostWithSignature(SUBUSER_APIKEY_GENERATION_PATH, builder);
    JSONObject data = jsonObject.getJSONObject("data");
    return new SubUserApiKeyGenerationResultParser().parse(data);
  }

  public SubUserApiKeyModificationResult subuserApiKeyModification(SubUserApiKeyModificationRequest request) {
    InputChecker.checker()
        .shouldNotNull(request.getAccessKey(), "accessKey")
        .shouldNotNull(request.getSubUid(), "subUid");

    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToPost("accessKey", request.getAccessKey())
        .putToPost("subUid", request.getSubUid())
        .putToPost("note", request.getNote())
        .putToPost("permission", request.getPermission())
        .putToPost("ipAddresses", request.getIpAddresses());

    JSONObject jsonObject = restConnection.executePostWithSignature(SUBUSER_APIKEY_MODIFICATION_PATH, builder);
    JSONObject data = jsonObject.getJSONObject("data");
    return new SubUserApiKeyModificationResultParser().parse(data);
  }

  @Override
  public void subuserApiKeyDeletion(SubUserApiKeyDeletionRequest request) {
    InputChecker.checker()
        .shouldNotNull(request.getAccessKey(), "accessKey")
        .shouldNotNull(request.getSubUid(), "subUid");

    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToPost("accessKey", request.getAccessKey())
        .putToPost("subUid", request.getSubUid());

    restConnection.executePostWithSignature(SUBUSER_APIKEY_DELETION_PATH, builder);
  }

  public GetApiKeyListResult getApiKeyList(GetApiKeyListRequest request) {
    InputChecker.checker()
        .shouldNotNull(request.getUid(), "uid");

    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToUrl("uid", request.getUid())
        .putToUrl("accessKey", request.getAccessKey());

    JSONObject jsonObject = restConnection.executeGetWithSignature(GET_SUBUSER_APIKEY_PATH, builder);
    return new GetApiKeyListResultParser().parse(jsonObject);
  }

  @Override
  public List<DepositAddress> getSubUserDepositAddress(Long subUid, String currency) {

    InputChecker.checker()
        .shouldNotNull(subUid, "subUid")
        .checkCurrency(currency);
    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToUrl("subUid", subUid)
        .putToUrl("currency", currency);

    JSONObject jsonObject = restConnection.executeGetWithSignature(GET_SUBUSER_DEPOSIT_ADDRESS_PATH, builder);

    JSONArray array = jsonObject.getJSONArray("data");
    return new DepositAddressParser().parseArray(array);
  }

  public GetSubUserDepositResult getSubUserDeposit(GetSubUserDepositRequest request) {

    InputChecker.checker()
        .shouldNotNull(request.getSubUid(), "subUid");

    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToUrl("subUid", request.getSubUid())
        .putToUrl("currency", request.getCurrency())
        .putToUrl("startTime", request.getStartTime())
        .putToUrl("endTime", request.getEndTime())
        .putToUrl("sort", request.getSort() == null ? null : request.getSort().getSort())
        .putToUrl("limit", request.getLimit())
        .putToUrl("fromId", request.getFromId());

    JSONObject jsonObject = restConnection.executeGetWithSignature(GET_SUBUSER_DEPOSIT_PATH, builder);
    return new GetSubUserDepositResultParser().parse(jsonObject);
  }

  @Override
  public long transferSubuser(TransferSubuserRequest request) {

    InputChecker.checker()
        .shouldNotNull(request.getSubUid(), "sub-uid")
        .shouldNotNull(request.getCurrency(), "currency")
        .shouldNotNull(request.getAmount(), "amount")
        .shouldNotNull(request.getType(), "type");

    UrlParamsBuilder builder = UrlParamsBuilder.build()
        .putToPost("sub-uid", request.getSubUid())
        .putToPost("currency", request.getCurrency())
        .putToPost("amount", request.getAmount().toPlainString())
        .putToPost("type", request.getType().getCode());

    JSONObject jsonObject = restConnection.executePostWithSignature(TRANSFER_SUBUSER_PATH, builder);
    return jsonObject.getLong("data");
  }

  @Override
  public List<AccountBalance> getSubuserAccountBalance(Long subuserId) {

    InputChecker.checker()
        .shouldNotNull(subuserId, "sub-uid");

    String path = GET_SUBUSER_ACCOUNT_BALANCE_PATH.replace("{sub-uid}", subuserId + "");
    JSONObject jsonObject = restConnection.executeGetWithSignature(path, UrlParamsBuilder.build());
    JSONArray data = jsonObject.getJSONArray("data");
    return new AccountBalanceParser().parseArray(data);
  }

  @Override
  public List<SubuserAggregateBalance> getSubuserAggregateBalance() {

    JSONObject jsonObject = restConnection.executeGetWithSignature(GET_SUBUSER_AGGREGATE_BALANCE_PATH, UrlParamsBuilder.build());
    JSONArray data = jsonObject.getJSONArray("data");
    return new SubuserAggregateBalanceParser().parseArray(data);

  }

  @Override
  public long getUid() {
    JSONObject jsonObject = restConnection.executeGetWithSignature(GET_UID_PATH, UrlParamsBuilder.build());
    long data = jsonObject.getLong("data");
    return data;
  }

}
