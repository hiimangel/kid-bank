package com.learnwithted.kidbank.adapter.web;

import com.learnwithted.kidbank.domain.Account;
import com.learnwithted.kidbank.domain.DummyUserProfile;
import com.learnwithted.kidbank.domain.TestAccountBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.validation.BindingResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class DepositControllerTest {

  @Mock(stubOnly = true)
  private BindingResult mockBindingResult;

  @Test
  public void depositCommandShouldAddAmountToAccount() {
    // Arrange
    TransactionCommand depositCommand = TransactionCommand.createWithTodayDate();
    depositCommand.setAmount(BigDecimal.valueOf(12.34));
    Account account = TestAccountBuilder.builder().buildAsCore();
    DepositController depositController = new DepositController(account);

    // Act
    Mockito.when(mockBindingResult.hasErrors()).thenReturn(false);
    depositController.processDepositCommand(depositCommand, mockBindingResult, new DummyUserProfile());

    // Assert
    assertThat(account.balance()).isEqualTo(1234);
  }

  @Test
  public void depositCommandShouldNotModifyBalanceIfErrorsExist() {
    // Arrange
    TransactionCommand depositCommand = TransactionCommand.createWithTodayDate();
    depositCommand.setAmount(BigDecimal.valueOf(10.00));
    Account account = TestAccountBuilder.builder().buildAsCore();
    DepositController depositController = new DepositController(account);

    // Act
    Mockito.when(mockBindingResult.hasErrors()).thenReturn(true);
    depositController.processDepositCommand(depositCommand, mockBindingResult, new DummyUserProfile());

    // Assert
    assertThat(account.balance()).isEqualTo(0); // Expect no changes
  }


}
