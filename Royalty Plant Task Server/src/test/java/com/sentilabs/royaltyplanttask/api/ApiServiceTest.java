package com.sentilabs.royaltyplanttask.api;

import com.sentilabs.royaltyplanttask.APIClient;
import com.sentilabs.royaltyplanttask.RoyaltyPlantTaskServerApplication;
import com.sentilabs.royaltyplanttask.parser.Parsers;
import com.sentilabs.royaltyplanttask.response.AccountDetailResponse;
import com.sentilabs.royaltyplanttask.response.DocumentDetailResponse;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by AMachekhin on 08.07.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = RoyaltyPlantTaskServerApplication.class)
//@WebAppConfiguration
@WebIntegrationTest
@ActiveProfiles(profiles = "inmemory")
@FixMethodOrder(value = MethodSorters.NAME_ASCENDING)
public class ApiServiceTest extends TestCase {

    private String hostname = "localhost";
    private int port = 8080;
    private String protocol = "http";
    private static double allowedDeltaForDoubleCompare = 0.0001;

    BigDecimal borrowAmount = BigDecimal.valueOf(1000.47);
    BigDecimal transferAmount = BigDecimal.valueOf(47);

    private interface GenericResponseInfo {

        int getStatusCode();
        InputStream getInputStream();
    }

    private static ResponseHandler<Integer> statusCodeResponseHandler = response -> response.getStatusLine().getStatusCode();
    private static ResponseHandler<GenericResponseInfo> genericResponseHandler = (HttpResponse response) -> {
        final InputStream inputStream = IOUtils.toBufferedInputStream(response.getEntity().getContent());

        return new GenericResponseInfo() {

            @Override
            public int getStatusCode() {
                return response.getStatusLine().getStatusCode();
            }

            @Override
            public InputStream getInputStream() {
                return inputStream;
            }
        };
    };

    @Test
    public void test01RegisterUser() throws Exception {
        Integer result = new APIClient(protocol, hostname, port)
                .registerUser("user1", "password1", statusCodeResponseHandler);
        Assert.assertEquals(HttpStatus.CREATED.value(), result.intValue());
    }

    @Test
    public void test02LoginUser() throws Exception {
        String username = "user2", password = "password2";
        final APIClient apiClient = new APIClient(protocol, hostname, port);

        Integer result = apiClient.registerUser(username, password, statusCodeResponseHandler);
        Assert.assertEquals(HttpStatus.CREATED.value(), result.intValue());

        result = apiClient.loginUser(username, password, statusCodeResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), result.intValue());
    }

    @Test
    public void test03OpenAccount() throws Exception {
        String username = "user1", password = "password1";

        APIClient apiClient = new APIClient(protocol, hostname, port);
        Integer result = apiClient.loginUser(username, password, statusCodeResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), result.intValue());

        GenericResponseInfo genericResponseInfo1 = apiClient.openAccount(genericResponseHandler);
        Assert.assertEquals(HttpStatus.CREATED.value(), genericResponseInfo1.getStatusCode());

        AccountDetailResponse accountDetailResponse1 = Parsers.parseAccountDetailResponse(genericResponseInfo1.getInputStream());
        Assert.assertFalse(StringUtils.isEmpty(accountDetailResponse1.getAccountNumber()));

        APIClient apiClient2 = new APIClient(protocol, hostname, port);
        username = "user2"; password = "password2";
        result = apiClient2.loginUser(username, password, statusCodeResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), result.intValue());

        GenericResponseInfo genericResponseInfo2 = apiClient2.openAccount(genericResponseHandler);
        Assert.assertEquals(HttpStatus.CREATED.value(), genericResponseInfo2.getStatusCode());

        AccountDetailResponse accountDetailResponse2 = Parsers.parseAccountDetailResponse(genericResponseInfo2.getInputStream());
        Assert.assertFalse(StringUtils.isEmpty(accountDetailResponse2.getAccountNumber()));
    }

    @Test
    public void test04Borrow() throws Exception {
        String username = "user1", password = "password1";
        APIClient apiClient = new APIClient(protocol, hostname, port);

        Integer result = apiClient.loginUser(username, password, statusCodeResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), result.intValue());

        GenericResponseInfo genericResponseInfo = apiClient.getMyAccounts(genericResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), genericResponseInfo.getStatusCode());

        List<AccountDetailResponse> accountDetailResponses = Parsers.parseAccountDetailResponseList(genericResponseInfo.getInputStream());
        Assert.assertEquals(1, accountDetailResponses.size());

        genericResponseInfo = apiClient.borrowMoney(accountDetailResponses.get(0).getAccountNumber(), borrowAmount, genericResponseHandler);
        Assert.assertEquals(HttpStatus.CREATED.value(), genericResponseInfo.getStatusCode());

        DocumentDetailResponse documentDetailResponse = Parsers.parseDocumentDetailResponse(genericResponseInfo.getInputStream());
        Assert.assertEquals(accountDetailResponses.get(0).getAccountNumber(), documentDetailResponse.getAcc_kt());

        Assert.assertEquals(borrowAmount.doubleValue(), documentDetailResponse.getDoc_sum().doubleValue(), allowedDeltaForDoubleCompare);
    }

    @Test
    public void test05Transfer() throws Exception {
        String username = "user1", password = "password1";

        APIClient apiClient = new APIClient(protocol, hostname, port);

        Integer result = apiClient.loginUser(username, password, statusCodeResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), result.intValue());

        GenericResponseInfo genericResponseInfo1 = apiClient.getMyAccounts(genericResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), genericResponseInfo1.getStatusCode());

        List<AccountDetailResponse> accountDetailResponses = Parsers.parseAccountDetailResponseList(genericResponseInfo1.getInputStream());
        Assert.assertEquals(1, accountDetailResponses.size());
        String fromAccount = accountDetailResponses.get(0).getAccountNumber();

        username = "user2";
        password = "password2";
        APIClient apiClient2 = new APIClient(protocol, hostname, port);

        result = apiClient2.loginUser(username, password, statusCodeResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), result.intValue());

        GenericResponseInfo genericResponseInfo2 = apiClient2.getMyAccounts(genericResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), genericResponseInfo2.getStatusCode());

        List<AccountDetailResponse> accountDetailResponses2 = Parsers.parseAccountDetailResponseList(genericResponseInfo2.getInputStream());
        Assert.assertEquals(1, accountDetailResponses2.size());
        String toAccount = accountDetailResponses2.get(0).getAccountNumber();

        GenericResponseInfo transferMoneyGenericResponseInfo = apiClient.transferMoney(fromAccount, toAccount, transferAmount, genericResponseHandler);
        Assert.assertEquals(HttpStatus.CREATED.value(), transferMoneyGenericResponseInfo.getStatusCode());
        DocumentDetailResponse documentDetailResponse = Parsers.parseDocumentDetailResponse(transferMoneyGenericResponseInfo.getInputStream());

        Assert.assertEquals(fromAccount, documentDetailResponse.getAcc_dt());
        Assert.assertEquals(toAccount, documentDetailResponse.getAcc_kt());
        Assert.assertEquals(transferAmount.doubleValue(), documentDetailResponse.getDoc_sum().doubleValue(), allowedDeltaForDoubleCompare);
    }

    @Test
    public void test06OpenAnotherAccount() throws Exception {
        String username = "user1", password = "password1";

        APIClient apiClient = new APIClient(protocol, hostname, port);
        Integer result = apiClient.loginUser(username, password, statusCodeResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), result.intValue());

        GenericResponseInfo genericResponseInfo1 = apiClient.openAccount(genericResponseHandler);
        Assert.assertEquals(HttpStatus.CREATED.value(), genericResponseInfo1.getStatusCode());

        AccountDetailResponse accountDetailResponse1 = Parsers.parseAccountDetailResponse(genericResponseInfo1.getInputStream());
        Assert.assertFalse(StringUtils.isEmpty(accountDetailResponse1.getAccountNumber()));

        GenericResponseInfo myAccountsGenericResponseInfo = apiClient.getMyAccounts(genericResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), myAccountsGenericResponseInfo.getStatusCode());
        List<AccountDetailResponse> myAccounts = Parsers.parseAccountDetailResponseList(myAccountsGenericResponseInfo.getInputStream());
        Assert.assertEquals(2, myAccounts.size());
        Assert.assertFalse(myAccounts.get(0).getAccountNumber().equals(myAccounts.get(1).getAccountNumber()));
        Assert.assertTrue(myAccounts.get(0).getAccountNumber().equals(accountDetailResponse1.getAccountNumber())
                || myAccounts.get(1).getAccountNumber().equals(accountDetailResponse1.getAccountNumber()));
    }

    @Test
    public void test07DocumentExecuted() throws Exception {
        Thread.sleep(TimeUnit.SECONDS.toMillis(15));
        String username = "user1", password = "password1";

        APIClient apiClient = new APIClient(protocol, hostname, port);
        Integer result = apiClient.loginUser(username, password, statusCodeResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), result.intValue());

        GenericResponseInfo myAccountsGenericResponseInfo = apiClient.getMyAccounts(genericResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), myAccountsGenericResponseInfo.getStatusCode());

        List<AccountDetailResponse> myAccounts = Parsers.parseAccountDetailResponseList(myAccountsGenericResponseInfo.getInputStream());
        Assert.assertEquals(borrowAmount.subtract(transferAmount).doubleValue(), myAccounts.get(0).getBalance().doubleValue(), allowedDeltaForDoubleCompare);

        GenericResponseInfo documentsForAccountGenericResponseInfo = apiClient.getDocumentsForAccount(myAccounts.get(0).getAccountNumber(), genericResponseHandler);
        List<DocumentDetailResponse> documentDetailResponseList = Parsers.parseDocumentDetailResponseList(documentsForAccountGenericResponseInfo.getInputStream());
        for (DocumentDetailResponse documentDetailResponse : documentDetailResponseList) {
            Assert.assertEquals("PROV", documentDetailResponse.getStatus());
        }
    }

    @Test
    public void test08DocumentNotExecuted() throws Exception {
        String username = "user1", password = "password1";

        APIClient apiClient = new APIClient(protocol, hostname, port);
        Integer result = apiClient.loginUser(username, password, statusCodeResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), result.intValue());

        GenericResponseInfo myAccountsGenericResponseInfo = apiClient.getMyAccounts(genericResponseHandler);
        Assert.assertEquals(HttpStatus.OK.value(), myAccountsGenericResponseInfo.getStatusCode());

        List<AccountDetailResponse> myAccounts = Parsers.parseAccountDetailResponseList(myAccountsGenericResponseInfo.getInputStream());

        GenericResponseInfo transferMoneyGenericResponseInfo = apiClient.transferMoney(myAccounts.get(0).getAccountNumber(), myAccounts.get(1).getAccountNumber(), BigDecimal.valueOf(2000.00), genericResponseHandler);
        Assert.assertEquals(HttpStatus.CREATED.value(), transferMoneyGenericResponseInfo.getStatusCode());

        apiClient.getDocumentsForAccount(myAccounts.get(0).getAccountNumber(), genericResponseHandler);

        Thread.sleep(TimeUnit.SECONDS.toMillis(15));

        GenericResponseInfo documentsForAccountGenericResponseInfo = apiClient.getDocumentsForAccount(myAccounts.get(0).getAccountNumber(), genericResponseHandler);
        List<DocumentDetailResponse> documentDetailResponseList = Parsers.parseDocumentDetailResponseList(documentsForAccountGenericResponseInfo.getInputStream());
        DocumentDetailResponse documentDetailResponse = documentDetailResponseList.stream().filter(
                ddr -> Double.compare(ddr.getDoc_sum().doubleValue()
                        , 2000) == 0).findFirst().get();
        Assert.assertEquals("REJECTED", documentDetailResponse.getStatus());
    }
}