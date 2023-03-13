package com.driver.controllers;
import com.driver.model.User;
import com.driver.services.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserServiceImpl userService;
    @PostMapping("/register")
    public ResponseEntity<Void> registerUser(@RequestParam("name") String username, @RequestParam("pass") String password, @RequestParam("coun") String countryName) throws Exception{
        //Here I have created a user of given country. The originalIp of the user is "countryCode.userId" and user is returned.
        //Right now user is not connected and thus connected would be false and maskedIp would be null
        //The userId is created automatically by the repository layer
        User user = userService.register(username, password, countryName);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/subscribe")
    public void subscribe(@RequestParam Integer userId, @RequestParam Integer serviceProviderId){
        //Subscribe to the serviceProvider by adding the said serviceProvider to the list of providers and returning updated User
        User user = userService.subscribe(userId, serviceProviderId);
    }
}
