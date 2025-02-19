package com.learnwithted.kidbank.adapter.web;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.google.common.collect.ImmutableList;
import com.learnwithted.kidbank.adapter.DateFormatting;
import com.learnwithted.kidbank.adapter.ScaledDecimals;
import com.learnwithted.kidbank.domain.Account;
import com.learnwithted.kidbank.domain.Action;
import com.learnwithted.kidbank.domain.Goal;
import com.learnwithted.kidbank.domain.Transaction;
import com.learnwithted.kidbank.domain.UserProfile;

public class AdditionalLLMTest {
  
  @Test
  public void viewBalance_redirectsWhenPrincipalIsNull() {
    // Arrange: Create a mock Account (behavior not relevant here) and instantiate controller.
    Account mockAccount = mock(Account.class);
    AccountController controller = new AccountController(mockAccount);
    Model model = new ExtendedModelMap();
    
    // Act: Call viewBalance() with a null Principal.
    String view = controller.viewBalance(model, null);
    
    // Assert: Expect a redirection to logout.
    assertEquals("redirect:/logout", view);
  }

  @Test
  public void transactionView_viewOf_withoutCreator() {
    // Create a transaction without a creator. Using createInterestCredit returns a transaction with Optional.empty() creator.
    LocalDateTime date = LocalDateTime.of(2021, 2, 1, 11, 0);
    int amount = 500;
    Transaction txn = Transaction.createInterestCredit(date, amount);
    
    // Define a running balance.
    int runningBalance = 500;
    
    // Call viewOf.
    TransactionView view = TransactionView.viewOf(txn, runningBalance);
    
    // Expected values.
    String expectedDate = DateFormatting.formatAsDate(date);
    String expectedAction = ActionFormatter.format(txn.action());
    String expectedAmount = ScaledDecimals.formatAsMoney(amount);
    String expectedRunningBalance = ScaledDecimals.formatAsMoney(runningBalance);
    String expectedCreator = "none";  // Because creator is absent.
    
    assertEquals(expectedDate, view.getDate());
    assertEquals(expectedAction, view.getAction());
    assertEquals(expectedAmount, view.getAmount());
    assertEquals(expectedRunningBalance, view.getRunningBalance());
    assertEquals(txn.source(), view.getSource());
    assertEquals(expectedCreator, view.getCreator());
  }

  @Test
  public void transactionController_getBalance_returnsFormattedMoney() {
    // Arrange: Create a DummyAccount that returns a known balance.
    int testBalance = 1234;
    Account dummyAccount = new DummyAccount(testBalance);
    // Instantiate TransactionController with our dummy account.
    TransactionController controller = new TransactionController(dummyAccount);
    
    // Act: Call getBalance() from the TransactionController.
    String formattedBalance = controller.getBalance();
    
    // Assert: Verify that the returned balance equals ScaledDecimals.formatAsMoney(testBalance).
    assertEquals(ScaledDecimals.formatAsMoney(testBalance), formattedBalance);
  }
  
  // Minimal DummyAccount implementation to support testing of getBalance().
  private static class DummyAccount implements Account {
    private final int balance;
    
    public DummyAccount(int balance) {
      this.balance = balance;
    }
    
    @Override
    public int balance() {
      return balance;
    }
    
    @Override
    public int interestEarned() {
      return 0;
    }
    
    @Override
    public void deposit(LocalDateTime transactionDateTime, int amount, String source, UserProfile userProfile) {
      // No-op for testing.
    }
    
    @Override
    public void spend(LocalDateTime transactionDateTime, int amount, String description, UserProfile userProfile) {
      // No-op for testing.
    }
    
    @Override
    public ImmutableList<Transaction> transactions() {
      return ImmutableList.of();
    }
    
    @Override
    public void load(List<Transaction> transactionsToLoad) {
      // No-op for testing.
    }
    
    @Override
    public int balanceUpTo(LocalDateTime localDateTime) {
      return balance;
    }
    
    @Override
    public Set<Goal> goals() {
      return new HashSet<>();
    }
    
    @Override
    public void createGoal(String description, int targetAmount) {
      // No-op for testing.
    }
  }

    @Test
  public void transactionView_equals_hashCode_toString_workCorrectly() {
    // Create two TransactionView instances with the same field values.
    String date = "2021-01-01";
    String action = "DEPOSIT";
    String amount = "$10.00";
    String runningBalance = "$20.00";
    String source = "Test Source";
    String creator = "Test User";
    
    TransactionView view1 = new TransactionView(date, action, amount, runningBalance, source, creator);
    TransactionView view2 = new TransactionView(date, action, amount, runningBalance, source, creator);
    
    // Test equals.
    assertTrue(view1.equals(view2));
    // Test hashCode equality.
    assertEquals(view1.hashCode(), view2.hashCode());
    // Test toString contains key fields.
    String str = view1.toString();
    assertTrue(str.contains(date));
    assertTrue(str.contains(action));
    assertTrue(str.contains(amount));
    assertTrue(str.contains(runningBalance));
    assertTrue(str.contains(source));
    assertTrue(str.contains(creator));
  }
  @Test
  public void format_shouldCapitalizeSingleWordAction() {
      // Arrange
      Action action = Action.DEPOSIT;

      // Act
      String formatted = ActionFormatter.format(action);

      // Assert
      assertEquals("Deposit", formatted);
  }
  @Test
  public void format_shouldCapitalizeMultiWordAction() {
      // Arrange
      Action action = Action.INTEREST_CREDIT;

      // Act
      String formatted = ActionFormatter.format(action);

      // Assert
      assertEquals("Interest Credit", formatted);
  }
  @Test
  public void format_shouldHandleLowercaseInput() {
      // Arrange
      Action action = Action.valueOf("spend".toUpperCase()); // Simulate unformatted input

      // Act
      String formatted = ActionFormatter.format(action);

      // Assert
      assertEquals("Spend", formatted);
  }
}
