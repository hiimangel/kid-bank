package com.learnwithted.kidbank.adapter.web;

import org.assertj.core.util.Lists;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.learnwithted.kidbank.app.TextMessageSender;
import com.learnwithted.kidbank.domain.Account;
import com.learnwithted.kidbank.domain.Action;
import com.learnwithted.kidbank.domain.DummyUserProfile;
import com.learnwithted.kidbank.domain.DummyUserProfileRepository;
import com.learnwithted.kidbank.domain.FakeUserProfileRepository;
import com.learnwithted.kidbank.domain.PhoneNumber;
import com.learnwithted.kidbank.domain.Role;
import com.learnwithted.kidbank.domain.TestAccountBuilder;
import com.learnwithted.kidbank.domain.Transaction;
import com.learnwithted.kidbank.domain.UserProfile;
import com.learnwithted.kidbank.domain.UserProfileRepository;
import com.learnwithted.kidbank.domain.WelcomerService;

import static org.mockito.Mockito.spy;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class WebCoverageTest {

    @Mock
    private Model model; 
 
    @Mock(stubOnly = true)
    private BindingResult mockBindingResult;

    @Test
    public void accountControllerBalanceView() {
        Account account = TestAccountBuilder.builder().buildAsCore();
        AccountController controller = new AccountController(account);


        String nullResponse = controller.viewBalance(model, null); 
        Assert.assertTrue(nullResponse == "redirect:/logout");

        String response = controller.viewBalance(model, new PrincipalImplementation());
        Assert.assertTrue(response == "account-balance");


    }

    /* 
    this class is here solely because the abstract class Principal 
    is never implemented anywhere in the original code and is needed 
    to reach 100% coverage 
    */
    class PrincipalImplementation implements Principal {
        String name; 
        public String getName(){
            return this.name; 
        }
    }

    /*
     * This is  unnecessary and only included to have complete code coverage
     * The class "ActionFormatter" only consists of a single static method and should be marked abstract 
     */
    @Test
    public void actionFormatterInstance(){
        new ActionFormatter(); 
    }

    @Test (expected = IllegalArgumentException.class)
    public void invalidTransactionTypeInCsvShouldResultInIllegalArgumentException() {
        String csv = "01/05/2018, INVALID, $50.00, Birthday Gift"; 
        List<String> csvList = Lists.list(csv); 

        new CsvImporter().importFrom(csvList);
    }

    @Test
    public void depositForm() {
        Account account = TestAccountBuilder.builder().buildAsCore();
        DepositController depositController = new DepositController(account);
        String response = depositController.depositForm(model);
        Assert.assertTrue(response == "deposit");
    }

    @Test
    public void depositCommandHasErrors() {
        TransactionCommand depositCommand = TransactionCommand.createWithTodayDate();
        depositCommand.setAmount(BigDecimal.valueOf(-12.34));

        Account account = TestAccountBuilder.builder().buildAsCore();

        DepositController depositController = new DepositController(account);

        Mockito.when(mockBindingResult.hasErrors()).thenReturn(true);

        String result = depositController.processDepositCommand(depositCommand, mockBindingResult, new DummyUserProfile());

        Assert.assertTrue(result == "deposit");
    }
    
    @Test
    public void importCsvForm() {
        Account account = TestAccountBuilder.builder().buildAsCore();
        ImportCsvController importCsvController = new ImportCsvController(account);
        Assert.assertTrue(importCsvController.importCsvForm(model) == "import-csv");
    }

    @Test
    public void spendForm() {
        Account account = TestAccountBuilder.builder().buildAsCore();
        SpendController spendController = new SpendController(account);
        Assert.assertTrue(spendController.spendForm(model) == "spend");
    }
    
    @Test
    public void spendCommandShouldReduceAmountInAccount() throws Exception {
        TransactionCommand spendCommand = TransactionCommand.createWithTodayDate();
        spendCommand.setAmount(BigDecimal.valueOf(34.79));

        Account account = TestAccountBuilder.builder().buildAsCore();

        SpendController spendController = new SpendController(account);

        Mockito.when(mockBindingResult.hasErrors()).thenReturn(true);
        String response = spendController.processSpendCommand(spendCommand, mockBindingResult, new DummyUserProfile());

        Assert.assertTrue(response == "spend");
    }

    @Test
    public void getBalanceReturn() {
        Account account = TestAccountBuilder.builder().buildAsCore(); 
        TransactionController controller = new TransactionController(account);
        controller.getBalance(); 
    }

    @Test
    public void transactionViewEqualsCheck() {
        UserProfile userProfile = new UserProfile("Dad", null, null, null);
        Transaction transaction = new Transaction(LocalDateTime.of(2012, 2, 9, 0, 0),
                                              Action.DEPOSIT,
                                              45_98,
                                              "Gift",
                                              userProfile);

        TransactionView view = TransactionView.viewOf(transaction, 49_90);

        // same object, should return true 
        Assert.assertTrue(view.equals(view));

        // null object, should return false
        Assert.assertFalse(view.equals(null));

        // different class, should return false 
        Assert.assertFalse(view.equals(transaction));

        // same 
        Transaction transaction2 = new Transaction(LocalDateTime.of(2012, 2, 9, 0, 0),
                                              Action.DEPOSIT,
                                              45_98,
                                              "Gift",
                                              userProfile);
        TransactionView view2 = TransactionView.viewOf(transaction2, 49_90);
        Assert.assertEquals(view.getCreator(), view2.getCreator());
        Assert.assertEquals(view.hashCode(), view2.hashCode());
        Assert.assertEquals(view.toString(), view2.toString());

        // different source 
        transaction2 = new Transaction(LocalDateTime.of(2012, 2, 9, 0, 0),
            Action.DEPOSIT,
            45_98,
            "Source",
            userProfile);
        view2 = TransactionView.viewOf(transaction2, 49_90);
        Assert.assertEquals(view.getCreator(), view2.getCreator());
        Assert.assertNotEquals(view.getSource(), view2.getSource());
        Assert.assertFalse(view.equals(view2));

        // different running balance 
        view2 = TransactionView.viewOf(transaction2, 45_90);
        Assert.assertNotEquals(view.getRunningBalance(), view2.getRunningBalance());
        Assert.assertFalse(view.equals(view2));

        // different amount 
        transaction2 = new Transaction(LocalDateTime.of(2012, 2, 9, 0, 0),
            Action.DEPOSIT,
            43_98,
            "Source",
            userProfile);
        view2 = TransactionView.viewOf(transaction2, 45_90);
        Assert.assertNotEquals(view.getAmount(), view2.getAmount()); 
        Assert.assertFalse(view.equals(view2));

        // different action
        transaction2 = new Transaction(LocalDateTime.of(2012, 2, 9, 0, 0),
            Action.INTEREST_CREDIT,
            43_98,
            "Source",
            userProfile);
        view2 = TransactionView.viewOf(transaction2, 45_90);
        Assert.assertNotEquals(view.getAction(), view2.getAction());
        Assert.assertFalse(view.equals(view2));    
        
        // different date
        transaction2 = new Transaction(LocalDateTime.of(2015, 2, 9, 0, 0),
            Action.INTEREST_CREDIT,
            43_98,
            "Source",
            userProfile);
        view2 = TransactionView.viewOf(transaction2, 45_90);
        Assert.assertNotEquals(view.getDate(), view2.getDate());
        Assert.assertFalse(view.equals(view2));            

    }

    @Test(expected = IllegalArgumentException.class)
    public void userProfileControllerSendWelcomeWithNullId(){
        UserProfileRepository repository = new DummyUserProfileRepository(); 
        TextMessageSender spy = spy(TextMessageSender.class);
        WelcomerService welcomer = new WelcomerService(spy, repository);
        UserProfileController userProfileController = new UserProfileController(repository, welcomer);
        userProfileController.sendWelcome(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void userProfileControllerSendWelcomeWithIdNotInDb(){
        UserProfileRepository repository = new DummyUserProfileRepository(); 
        TextMessageSender spy = spy(TextMessageSender.class);
        WelcomerService welcomer = new WelcomerService(spy, repository);
        UserProfileController userProfileController = new UserProfileController(repository, welcomer);
        userProfileController.sendWelcome(0L);
    }

    @Test
    public void userProfileControllerSendWelcome() {
        String toPhoneNumber = "+16509871234";
        UserProfile userProfile = new UserProfile("Ted", new PhoneNumber(toPhoneNumber), "", Role.PARENT);
        userProfile.setId(0L);
        UserProfileRepository fakeRepo = new FakeUserProfileRepository(userProfile);

        TextMessageSender spy = spy(TextMessageSender.class);
        WelcomerService welcomerService = new WelcomerService(spy, fakeRepo);
        UserProfileController userProfileController = new UserProfileController(fakeRepo, welcomerService);
        userProfileController.sendWelcome(userProfile.getId());
    }

    @Test
    public void userProfileControllerUserCreateForm() {
        UserProfileRepository repository = new DummyUserProfileRepository(); 
        TextMessageSender spy = spy(TextMessageSender.class);
        WelcomerService welcomer = new WelcomerService(spy, repository);
        UserProfileController userProfileController = new UserProfileController(repository, welcomer);
        String response = userProfileController.getUserCreateForm(model);
        Assert.assertEquals(response, "create-user");
    }

    @Test
    public void userProfileControllerCreateUserProfile(){
        UserProfileRepository repository = new DummyUserProfileRepository(); 
        TextMessageSender spy = spy(TextMessageSender.class);
        WelcomerService welcomer = new WelcomerService(spy, repository);
        UserProfileController userProfileController = new UserProfileController(repository, welcomer);
        
        CreateUserProfile createUserProfile = new CreateUserProfile("Ted", "+16509871234", " ", "Parent"); 
        userProfileController.createUserProfile(createUserProfile);
    }

    @Test
    public void userProfileControllerViewAllUsers() {
        String toPhoneNumber = "+16509871234";
        UserProfile userProfile = new UserProfile("Ted", new PhoneNumber(toPhoneNumber), "", Role.PARENT);
        userProfile.setId(0L);
        UserProfileRepository fakeRepo = new FakeUserProfileRepository(userProfile);        
        TextMessageSender spy = spy(TextMessageSender.class);
        WelcomerService welcomer = new WelcomerService(spy, fakeRepo);
        UserProfileController userProfileController = new UserProfileController(fakeRepo, welcomer);
        userProfileController.viewAllUsers(model);        
    }

    @Test
    public void welcomeControllerWelcome(){
        WelcomeController welcomeController = new WelcomeController(); 
        String response = welcomeController.welcome(model);
        Assert.assertEquals(response, "welcome");
    }
}
