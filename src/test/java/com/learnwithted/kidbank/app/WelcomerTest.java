package com.learnwithted.kidbank.app;

import org.junit.Test;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import com.learnwithted.kidbank.domain.FakeUserProfileRepository;
import com.learnwithted.kidbank.domain.PhoneNumber;
import com.learnwithted.kidbank.domain.Role;
import com.learnwithted.kidbank.domain.UserProfile;
import com.learnwithted.kidbank.domain.UserProfileRepository;
import com.learnwithted.kidbank.domain.WelcomerService;

public class WelcomerTest {

  @Test
  public void welcomeUserSendsTextMessageToUserPhoneNumber() throws Exception {
    String toPhoneNumber = "+16509871234";
    UserProfile userProfile = new UserProfile("Ted", new PhoneNumber(toPhoneNumber), "", Role.PARENT);
    UserProfileRepository fakeRepo = new FakeUserProfileRepository(userProfile);

    TextMessageSender spy = spy(TextMessageSender.class);
    WelcomerService welcomerService = new WelcomerService(spy, fakeRepo);

    welcomerService.welcome(1L);

    String welcome = "Hi Ted, welcome to Kid Money Manager. You can use the commands: BALANCE, DEPOSIT, SPEND, and GOALS.";
    verify(spy).send(toPhoneNumber, welcome);
  }

}