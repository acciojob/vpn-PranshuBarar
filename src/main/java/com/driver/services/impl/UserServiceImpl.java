package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
//        if(!caseIgnoreCheckAndEnumCheck(countryName)){
//            throw new Exception();
//        }

        Country country = new Country();
        if(!caseIgnoreCheckAndEnumCheck(countryName.toUpperCase().substring(0,3))){
            throw new Exception();
        }
        country.setCountryName(CountryName.valueOf(countryName.toUpperCase().substring(0,3)));
        country.setCode(CountryName.valueOf(countryName.toUpperCase().substring(0,3)).toCode());

        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        user.setOriginalCountry(country);
        user.setConnected(false);
        user.setMaskedIp(null);

        User userFromRepo = userRepository3.save(user);
        user.setOriginalIp(country.getCode()+"."+userFromRepo.getId());
        return userRepository3.save(userFromRepo);

    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).get();

        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();
        user.getServiceProviderList().add(serviceProvider);


        return userRepository3.save(user);
    }

    public boolean caseIgnoreCheckAndEnumCheck(String countryName){
        for (CountryName countryName1 : CountryName.values()) {
            if (countryName1.name().equals(countryName)) {
                return true;
            }
        }
        return false;
    }
}
