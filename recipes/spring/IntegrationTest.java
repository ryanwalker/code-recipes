package com.kubra.prepay.api.resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubra.prepay.database.ArticleType;
import com.kubra.prepay.database.entity.Account;
import com.kubra.prepay.database.entity.ArticleKey;
import com.kubra.prepay.generated.model.AccountTransactionResource;
import com.kubra.prepay.generated.model.TransactionTypeResource;
import com.kubra.prepay.service.AccountsService;
import com.kubra.prepay.utils.AccountTestUtils;
import com.kubra.prepay.utils.AccountTransactionTestUtils;
import com.kubra.security.oidc.rs.context.User;
import com.kubra.security.oidc.rs.context.UserContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
public class AccountTransactionControllerIntegrationTest {

  public static final String TRANSACTIONS_URL =
      "/v1/instances/{instanceId}/articles/{articleId}/transactions";

  @Autowired private MockMvc mockMvc;

  @Autowired private AccountsService accountsService;

  @Autowired private ObjectMapper objectMapper;

  private Account account;
  private final UUID tenantId = UUID.randomUUID();
  private ArticleKey articleKey;
  private UUID instanceId;
  private String articleId;

  @Before
  public void setup() {
    User user = new User(tenantId, null, new ArrayList<>());
    UserContext.setTestUser(user);

    account = AccountTestUtils.createEntity();
    accountsService.saveAccount(account);

    articleKey = new ArticleKey("externalId", "ownerId", ArticleType.ACCOUNT.toString());
    articleId = articleKey.toString();
    instanceId = account.getInstanceId();
  }

  @After
  public void tearDown() {
    accountsService.delete(account);
  }

  @Test
  @WithMockUser(authorities = {"pb:tran:c"})
  public void testApplyTransaction_Payment() throws Exception {
    AccountTransactionResource transactionResource =
        AccountTransactionTestUtils.createTransactionResource(TransactionTypeResource.PAYMENT);

    String requestBody = objectMapper.writeValueAsString(transactionResource);

    MockHttpServletResponse response =
        mockMvc
            .perform(
                post(TRANSACTIONS_URL, instanceId, articleId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andDo(MockMvcResultHandlers.print())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse();

    String responseBodyString = response.getContentAsString();

    AccountTransactionResource responseResource =
        objectMapper.readValue(responseBodyString, AccountTransactionResource.class);

    BigDecimal expectedNewCreditBalance =
        account.getAccountBalance().getCreditBalance().add(transactionResource.getAmount());

    Assertions.assertEquals(
        expectedNewCreditBalance, responseResource.getBalance().getNewCreditBalance());
  }

  @Test
  @WithMockUser(authorities = {})
  public void testApplyTransaction_NoPermission() throws Exception {
    AccountTransactionResource transactionResource =
        AccountTransactionTestUtils.createTransactionResource(TransactionTypeResource.PAYMENT);

    String requestBody = objectMapper.writeValueAsString(transactionResource);

    mockMvc
        .perform(
            post(TRANSACTIONS_URL, instanceId, articleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isForbidden());
  }

  @Test
  public void testApplyTransaction_NoUser() throws Exception {
    AccountTransactionResource transactionResource =
        AccountTransactionTestUtils.createTransactionResource(TransactionTypeResource.PAYMENT);

    String requestBody = objectMapper.writeValueAsString(transactionResource);

    mockMvc
        .perform(
            post(TRANSACTIONS_URL, instanceId, articleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(authorities = {"pb:tran:c"})
  public void testApplyTransaction_AccountNotFound() throws Exception {
    AccountTransactionResource transactionResource =
        AccountTransactionTestUtils.createTransactionResource(TransactionTypeResource.PAYMENT);

    String requestBody = objectMapper.writeValueAsString(transactionResource);

    String notFoundArticleId = "nope" + articleId;
    mockMvc
        .perform(
            post(TRANSACTIONS_URL, instanceId, notFoundArticleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isNotFound());
  }
}

