package com.learnwithted.kidbank.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class AdditionalDomainTests {
  @Test
  public void testEqualsSameInstance() {
    // A profile should be equal to itself.
    UserProfile profile = new DummyUserProfile();
    profile.setId(1L);
    assertTrue("Profile should equal itself", profile.equals(profile));
  }

  @Test
  public void outOfOrderTransactionLoadingMaintainsCorrectBalance() throws Exception {
    // Prepare a core account and a list of transactions in non-chronological order.
    Account account = TestAccountBuilder.builder().buildAsCore();
    
    // Create transactions with mixed dates.
    Transaction t1 = Transaction.createDeposit(LocalDateTime.of(2021, 6, 2, 10, 0), 1000, "Deposit1", new DummyUserProfile());
    Transaction t2 = Transaction.createSpend(LocalDateTime.of(2021, 6, 1, 10, 0), 200, "Spend1", new DummyUserProfile());
    Transaction t3 = Transaction.createDeposit(LocalDateTime.of(2021, 6, 3, 10, 0), 500, "Deposit2", new DummyUserProfile());
    
    // Load transactions out of order using a mutable list.
    List<Transaction> outOfOrder = new ArrayList<>(Arrays.asList(t1, t3, t2));
    account.load(outOfOrder);
    
    // Expected balance: (1000 - 200 + 500) = 1300.
    assertThat(account.balance()).isEqualTo(1300);
  }

  @Test
  public void sameTimestampOperationsAreHandledCorrectly() throws Exception {
    Account account = TestAccountBuilder.builder().buildAsCore();
    LocalDateTime timestamp = LocalDateTime.of(2021, 7, 1, 12, 0);
    DummyUserProfile dummy = new DummyUserProfile();
    
    // Both deposit and spend occur at exactly the same timestamp.
    account.deposit(timestamp, 1000, "Deposit Same Time", dummy);
    account.spend(timestamp, 400, "Spend Same Time", dummy);
    
    // Balance should be (1000 - 400) = 600.
    assertThat(account.balance()).isEqualTo(600);
  }

 @Test
  public void testEqualsNull() {
    // A profile should not be equal to null.
    UserProfile profile = new DummyUserProfile();
    profile.setId(1L);
    assertFalse("Profile should not equal null", profile.equals(null));
  }

  @Test
  public void testEqualsDifferentClass() {
    // A profile should not be equal to an instance of another class.
    UserProfile profile = new DummyUserProfile();
    profile.setId(1L);
    Object other = new Object();
    assertFalse("Profile should not equal an object of a different type", profile.equals(other));
  }

  @Test
  public void testEqualsDifferentIds() {
    // Two profiles with different non-null ids should not be equal.
    UserProfile profile1 = new DummyUserProfile();
    UserProfile profile2 = new DummyUserProfile();
    profile1.setId(1L);
    profile2.setId(2L);
    assertFalse("Profiles with different ids should not be equal", profile1.equals(profile2));
  }

  @Test
  public void testEqualsSameNonNullId() {
    // Two profiles with the same non-null id should be equal.
    UserProfile profile1 = new DummyUserProfile();
    UserProfile profile2 = new DummyUserProfile();
    profile1.setId(1L);
    profile2.setId(1L);
    assertTrue("Profiles with the same id should be equal", profile1.equals(profile2));
  }

  @Test
  public void testEqualsBothNullId() {
    // If both ids are null, then per our implementation they are considered equal.
    UserProfile profile1 = new DummyUserProfile();
    UserProfile profile2 = new DummyUserProfile();
    profile1.setId(null);
    profile2.setId(null);
    assertTrue("Profiles with both null ids should be equal", profile1.equals(profile2));
  }

  @Test
  public void testHashCodeNullId() {
    // When id is null, hashCode() should return 0.
    UserProfile profile = new DummyUserProfile();
    profile.setId(null);
    assertEquals("HashCode should be 0 when id is null", 0, profile.hashCode());
  }

  @Test
  public void testHashCodeNonNullId() {
    // When id is non-null, hashCode() should return the id's hashCode.
    UserProfile profile = new DummyUserProfile();
    profile.setId(123L);
    int expectedHash = Long.valueOf(123L).hashCode();
    assertEquals("HashCode should match the id's hashCode", expectedHash, profile.hashCode());
  }
  @Test
  public void testEqualsNonNullIds() {
    // When id is non-null, equals() returns id.equals(that.id)
    UserProfile profile1 = new DummyUserProfile();
    UserProfile profile2 = new DummyUserProfile();
    profile1.setId(100L);
    profile2.setId(100L);
    // Expected to be true because both ids are non-null and equal.
    assertTrue("Profiles with same non-null id should be equal", profile1.equals(profile2));
  }

  @Test
  public void testEqualsNonNullIdsDifferentValues() {
    // When ids are non-null but different, equals() should return false.
    UserProfile profile1 = new DummyUserProfile();
    UserProfile profile2 = new DummyUserProfile();
    profile1.setId(100L);
    profile2.setId(200L);
    // Expected to be false because the ids differ.
    assertFalse("Profiles with different non-null ids should not be equal", profile1.equals(profile2));
  }

  @Test
  public void testEqualsBothNullIds() {
    // When this.id is null, the ternary returns that.id == null.
    UserProfile profile1 = new DummyUserProfile();
    UserProfile profile2 = new DummyUserProfile();
    profile1.setId(null);
    profile2.setId(null);
    // Expected to be true because both ids are null.
    assertTrue("Profiles with both null ids should be equal", profile1.equals(profile2));
  }

  @Test
  public void testEqualsOneNullId() {
    // When this.id is null but the other is non-null, equals() should return false.
    UserProfile profile1 = new DummyUserProfile();
    UserProfile profile2 = new DummyUserProfile();
    profile1.setId(null);
    profile2.setId(100L);
    // Expected to be false because one id is null and the other is not.
    assertFalse("Profile with null id should not equal a profile with non-null id", profile1.equals(profile2));
  }

}
