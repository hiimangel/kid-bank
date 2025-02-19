package com.learnwithted.kidbank.domain;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.learnwithted.kidbank.app.TextMessageSender;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;


public class DomainCoverageTest {
    
    @Test 
    public void goalIsCreated() {
        InterestEarningAccount account = TestAccountBuilder.builder().buildAsInterestEarning(2000, 1, 1);
        account.createGoal("description", 65_00);
        Goal expectedGoal = new Goal("description", 65_00);
        assertThat(account.goals())
            .usingElementComparatorIgnoringFields("id")
            .containsOnly(expectedGoal);
    }

    @Test
    public void transactionIsEqual() {
        Account account = TestAccountBuilder.builder().buildAsCore(); 
        UserProfile userProfile = new DummyUserProfile(); 

        account.deposit(LocalDateTime.now(), 123, "source", userProfile);

        Transaction expectedTransaction = 
            new Transaction(0L, LocalDateTime.now(), Action.DEPOSIT, 123, "source", userProfile);


        ImmutableList<Transaction> transactions = account.transactions(); 
    
        assertThat(transactions)
            .hasSize(1);
        
        Transaction actualTransaction = transactions.get(0);

        // Test equal transaction returns true 
        Assert.assertTrue(actualTransaction.equals(expectedTransaction));

        // Test null comparison returns false 
        Assert.assertTrue(actualTransaction.equals(null) == false);

        // Test class mismatch comparison returns false 
        Assert.assertTrue(actualTransaction.equals(account) == false);

        // Test unequal transaction returns false 
        Transaction unexpectedTransaction = 
            new Transaction(1L, LocalDateTime.now(), Action.DEPOSIT, 234, "source", userProfile);
        Assert.assertTrue(actualTransaction.equals(unexpectedTransaction) == false); 
    }

    @Test
    public void transactionHashCodeReturnZeroWhenIdIsNull() {
        UserProfile userProfile = new DummyUserProfile(); 
        Transaction transaction = 
            new Transaction(null, LocalDateTime.now(), Action.DEPOSIT, 123, "source", userProfile);
        
        Assert.assertTrue(transaction.hashCode() == 0);

    }

    @Test
    public void userProfileIsEqual(){
        UserProfile userProfile1 = new DummyUserProfile(); 
        UserProfile userProfile2 = new DummyUserProfile(); 

        // test comparison of same object returns true 
        Assert.assertTrue(userProfile1.equals(userProfile1));

        // test comparison of null returns false 
        Assert.assertTrue(userProfile1.equals(null) == false);

        // test comparison of different class returns false
        Assert.assertTrue(userProfile1.equals(new Object()) == false);

        // test comparison of different UserProfile 
        userProfile1.setId(1L);
        Assert.assertTrue(userProfile1.equals(userProfile2) == false); 

        // test comparison of same id UserProfile 
        userProfile2.setId(userProfile1.getId());
        Assert.assertTrue(userProfile1.equals(userProfile2)); 

        // test comparison of null id UserProfile
        userProfile1.setId(null);
        Assert.assertTrue(userProfile1.equals(userProfile2) == false); 

        // test comparison of both null id UserProfile 
        userProfile2.setId(null);
        Assert.assertTrue(userProfile1.equals(userProfile2)); 

    }

    @Test
    public void userProfileHashCode(){
        UserProfile userProfile = new DummyUserProfile(); 
        
        // null id returns 0
        Assert.assertTrue(userProfile.hashCode() == 0);

        // id returns hash 
        userProfile.setId(0L);
        Long expectedId = 0L; 
        Assert.assertTrue(userProfile.hashCode() == expectedId.hashCode());
    }

    @Test
    public void welcomerServiceUserProfileNotPresent() {

        String toPhoneNumber = "+16509871234";
        UserProfile userProfile = new UserProfile("Ted", new PhoneNumber(toPhoneNumber), "", Role.PARENT);
        userProfile.setId(0L);
        UserProfileRepository fakeRepo = new DummyUserProfileRepository();
        TextMessageSender spy = spy(TextMessageSender.class);

        WelcomerService welcomerService = new WelcomerService(spy, fakeRepo);

        welcomerService.welcome(15L);
        String welcome = "Hi Ted, welcome to Kid Money Manager. You can use the commands: BALANCE, DEPOSIT, SPEND, and GOALS.";
        
        // verify message was not sent
        verify(spy, times(0)).send(toPhoneNumber, welcome);
    }

}
