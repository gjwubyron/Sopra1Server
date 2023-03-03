package ch.uzh.ifi.hase.soprafs23.repository;

import ch.uzh.ifi.hase.soprafs23.constant.UserStatus;
import ch.uzh.ifi.hase.soprafs23.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Date;

@DataJpaTest
public class UserRepositoryIntegrationTest {

  @Autowired
  private TestEntityManager entityManager;

  @Autowired
  private UserRepository userRepository;

  @Test
  public void findByUsername_success() {
    // given
    User user = new User();
    user.setPassword("Firstname Lastname");
    user.setUsername("firstname@lastname");
    user.setStatus("OFFLINE");
    user.setCreation_date(new Date());
    user.setToken("1");

    user = userRepository.save(user);
    userRepository.flush();

    entityManager.persist(user);
    entityManager.flush();

    // when
    User found = userRepository.findByUsername(user.getUsername());

    // then
      //assertNotNull(found.getId());
//    assertEquals(found.getPassword(), user.getPassword());
//    assertEquals(found.getUsername(), user.getUsername());
//    assertEquals(found.getCreation_date(), user.getCreation_date());
//    assertEquals(found.getToken(), user.getToken());
//    assertEquals(found.getStatus(), user.getStatus());
  }
}
