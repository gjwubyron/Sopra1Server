package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * UserControllerTest
 * This is a WebMvcTest which allows to test the UserController i.e. GET/POST
 * request without actually sending them over the network.
 * This tests if the UserController works.
 */
@WebMvcTest(UserController.class)
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  // Get all users successful
  @Test
  public void givenUsers_whenGetUsers_thenReturnJsonArray() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setPassword("TestPassword");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus("ONLINE");
    user.setCreation_date(new Date());

    List<User> allUsers = Collections.singletonList(user);

    // this mocks the UserService -> we define above what the userService should
    // return when getUsers() is called
    given(userService.getUsers(Mockito.any())).willReturn(allUsers);

    // when
    MockHttpServletRequestBuilder getRequest = get("/users?token=1").contentType(MediaType.APPLICATION_JSON);

    // then
    mockMvc.perform(getRequest).andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id", is(user.getId().intValue())))
        .andExpect(jsonPath("$[0].username", is(user.getUsername())))
            .andExpect(jsonPath("$[0].token", is(user.getToken())))
        .andExpect(jsonPath("$[0].status", is(user.getStatus())));

  }

  // Post successful
  @Test
  public void createUser_validInput_userCreated() throws Exception {
    // given
    User user = new User();
    user.setId(1L);
    user.setPassword("TestPassword");
    user.setUsername("testUsername");
    user.setToken("1");
    user.setStatus("ONLINE");
    user.setCreation_date(new Date());

    UserPostDTO userPostDTO = new UserPostDTO();
    userPostDTO.setPassword("TestPassword");
    userPostDTO.setUsername("testUsername");

    given(userService.createUser(Mockito.any())).willReturn(user);

    // when/then -> do the request + validate the result
    MockHttpServletRequestBuilder postRequest = post("/users")
        .contentType(MediaType.APPLICATION_JSON)
        .content(asJsonString(userPostDTO));

    // then
    mockMvc.perform(postRequest)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
            .andExpect(jsonPath("$.token", is(user.getToken())))
        .andExpect(jsonPath("$.status", is(user.getStatus())));
  }

    // Post unsuccessful
    @Test
    public void createUser_inValidInput_userCreatingFailed() throws Exception {
        // given
        Exception exception = new ResponseStatusException(HttpStatus.CONFLICT,
                "The username provided is not unique.");

        UserPostDTO userPostDTO = new UserPostDTO();
        userPostDTO.setPassword("TestPassword");
        userPostDTO.setUsername("testUsername");

        given(userService.createUser(Mockito.any())).willThrow(exception);


        // when/then -> do the request + validate the result
        MockHttpServletRequestBuilder postRequest = post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(userPostDTO));

        // then
        mockMvc.perform(postRequest)
                .andExpect(status().isConflict())
                .andExpect(response -> assertEquals("The username provided is not unique.",
                        response.getResponse().getErrorMessage()))
                ;
    }

    // Get one user successful
    @Test
    public void givenUser_whenGetUser_thenReturnUser() throws Exception {
        // given
        User user = new User();
        user.setId(1L);
        user.setPassword("TestPassword");
        user.setUsername("testUsername");
        user.setToken("1");
        user.setStatus("ONLINE");
        user.setCreation_date(new Date());
        given(userService.getUser(Mockito.any(), Mockito.any())).willReturn(user);

        // when
        MockHttpServletRequestBuilder getRequest =
                get("/users/1?token=1").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId().intValue())))
                .andExpect(jsonPath("$.username", is(user.getUsername())))
                .andExpect(jsonPath("$.token", is(user.getToken())))
                .andExpect(jsonPath("$.status", is(user.getStatus())));

    }

    // Get one user unsuccessful
    @Test
    public void givenError_whenGetUser_thenReturnError() throws Exception {
        // given
        Exception exception = new ResponseStatusException(HttpStatus.NOT_FOUND,
                "user with userId 1 was not found");

        given(userService.getUser(Mockito.any(), Mockito.any())).willThrow(exception);

        // when
        MockHttpServletRequestBuilder getRequest =
                get("/users/1?token=1").contentType(MediaType.APPLICATION_JSON);

        // then
        mockMvc.perform(getRequest)
                .andExpect(status().isNotFound())
                .andExpect(response -> assertEquals("user with userId 1 was not found",
                        response.getResponse().getErrorMessage()));
    }

    // Put user successful
    @Test
    public void whenPutUser_thenReturnNoContent() throws Exception {

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("testUsername");
        userPutDTO.setBirthday("2000-03-17");

        // when
        MockHttpServletRequestBuilder putRequest =
                put("/users/1?token=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNoContent());
    }

    // Put user unsuccessful
    @Test
    public void whenPutUser_thenReturnError() throws Exception {

        Exception exception = new ResponseStatusException(HttpStatus.NOT_FOUND,
                "user with userId 1 was not found");

        UserPutDTO userPutDTO = new UserPutDTO();
        userPutDTO.setUsername("testUsername");
        userPutDTO.setBirthday("2000-03-17");

        when(userService.updateUser(Mockito.any(), Mockito.any(), Mockito.any())).thenThrow(exception);

        // when
        MockHttpServletRequestBuilder putRequest =
                put("/users/1?token=1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(userPutDTO));

        // then
        mockMvc.perform(putRequest)
                .andExpect(status().isNotFound())
                .andExpect(response -> assertEquals("user with userId 1 was not found",
                        response.getResponse().getErrorMessage()));
    }


  /**
   * Helper Method to convert userPostDTO into a JSON string such that the input
   * can be processed
   * Input will look like this: {"name": "Test User", "username": "testUsername"}
   * 
   * @param object
   * @return string
   */
  private String asJsonString(final Object object) {
    try {

      return new ObjectMapper().writeValueAsString(object);
    } catch (JsonProcessingException e) {
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
          String.format("The request body could not be created.%s", e.toString()));
    }
  }

}