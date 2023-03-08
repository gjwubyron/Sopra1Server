package ch.uzh.ifi.hase.soprafs23.controller;

import ch.uzh.ifi.hase.soprafs23.entity.User;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserGetDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPostDTO;
import ch.uzh.ifi.hase.soprafs23.rest.dto.UserPutDTO;
import ch.uzh.ifi.hase.soprafs23.rest.mapper.DTOMapper;
import ch.uzh.ifi.hase.soprafs23.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * User Controller
 * This class is responsible for handling all REST request that are related to
 * the user.
 * The controller will receive the request and delegate the execution to the
 * UserService and finally return the result.
 */
@RestController
public class UserController {

  private final UserService userService;

  UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public List<UserGetDTO> getAllUsers(@RequestParam String token) {
    // fetch all users in the internal representation
    List<User> users = userService.getUsers(token);
    List<UserGetDTO> userGetDTOs = new ArrayList<>();

    // convert each user to the API representation
    for (User user : users) {
      userGetDTOs.add(DTOMapper.INSTANCE.convertEntityToUserGetDTO(user));
    }
    return userGetDTOs;
  }

    @GetMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserGetDTO getUser(@PathVariable Long userId, @RequestParam String token) {
      User user = userService.getUser(userId, token);
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(user);
    }

  @PostMapping("/users")
  @ResponseStatus(HttpStatus.CREATED)
  @ResponseBody
  public UserGetDTO createUser(@RequestBody UserPostDTO userPostDTO) {
    // convert API user to internal representation
    User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);

    // create user
    User createdUser = userService.createUser(userInput);
    // convert internal representation of user back to API
    return DTOMapper.INSTANCE.convertEntityToUserGetDTO(createdUser);
  }

  @PutMapping("/users/{userId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void updateUser(@PathVariable Long userId, @RequestParam String token, @RequestBody UserPutDTO userPutDTO){
      if(userPutDTO.getBirthday() == ""){userPutDTO.setBirthday(null);}
      if (userPutDTO.getBirthday() != null){
        if(!userPutDTO.getBirthday().matches("\\d{4}-\\d{2}-\\d{2}")){
          throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Incorrect format for birthday YYYY-MM-DD");
        }
      }
      userService.updateUser(userId, token, DTOMapper.INSTANCE.convertUserPutDTOtoEntity(userPutDTO));
  }

  @PostMapping("/registered-users")
  @ResponseStatus(HttpStatus.OK)
  @ResponseBody
  public UserGetDTO checkUser(@RequestBody UserPostDTO userPostDTO){
      User userInput = DTOMapper.INSTANCE.convertUserPostDTOtoEntity(userPostDTO);
      User checkedUser = userService.checkUser(userInput);
      return DTOMapper.INSTANCE.convertEntityToUserGetDTO(checkedUser);
  }
}
