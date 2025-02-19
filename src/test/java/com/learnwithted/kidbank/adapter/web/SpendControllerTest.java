package com.learnwithted.kidbank.adapter.web;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import com.google.common.collect.ImmutableList;
import com.learnwithted.kidbank.domain.Account;
import com.learnwithted.kidbank.domain.Goal;
import com.learnwithted.kidbank.domain.Transaction;
import com.learnwithted.kidbank.domain.UserProfile;

public class SpendControllerTest {

  // DummyAccount to satisfy the SpendController constructor.
  private static class DummyAccount implements Account {
    @Override
    public int balance() {
      return 0;
    }

    @Override
    public int interestEarned() {
      return 0;
    }

    @Override
    public void deposit(LocalDateTime transactionDateTime, int amount, String source, UserProfile userProfile) {
      // No-op.
    }

    @Override
    public void spend(LocalDateTime transactionDateTime, int amount, String description, UserProfile userProfile) {
      // No-op.
    }

    @Override
    public ImmutableList<Transaction> transactions() {
      return ImmutableList.of();
    }

    @Override
    public void load(List<Transaction> transactionsToLoad) {
      // No-op.
    }

    @Override
    public int balanceUpTo(LocalDateTime localDateTime) {
      return 0;
    }

    @Override
    public Set<Goal> goals() {
      return new HashSet<>();
    }

    @Override
    public void createGoal(String description, int targetAmount) {
      // No-op.
    }
  }

  @Test
  public void spendForm_AddsSpendCommandToModel_AndReturnsSpendView() {
    // Arrange: Instantiate SpendController with a dummy Account and create a Model.
    Account dummyAccount = new DummyAccount();
    SpendController controller = new SpendController(dummyAccount);
    Model model = new ExtendedModelMap();

    // Act: Invoke the spendForm() method.
    String viewName = controller.spendForm(model);

    // Assert:
    // The view name should be "spend".
    assertEquals("spend", viewName);
    // The model should contain a "spendCommand" attribute.
    Object spendCommandObj = model.asMap().get("spendCommand");
    assertNotNull("Model should contain a spendCommand attribute", spendCommandObj);
    // Optionally, cast and check that the spendCommand has a valid date.
    TransactionCommand spendCommand = (TransactionCommand) spendCommandObj;
    assertNotNull("The TransactionCommand should have a non-null date", spendCommand.getDateAsLocalDateTime());
  }
}
