package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

        Country country = new Country();

        if(countryName.equalsIgnoreCase("IND")){
            country.setCountryName(CountryName.IND);
            country.setCode(CountryName.IND.toCode());
        }
        else if(countryName.equalsIgnoreCase("AUS")){
            country.setCountryName(CountryName.AUS);
            country.setCode(CountryName.AUS.toCode());
        } else if (countryName.equalsIgnoreCase("USA")) {
            country.setCountryName(CountryName.USA);
            country.setCode(CountryName.USA.toCode());
        }
        else if(countryName.equalsIgnoreCase("CHI")){
            country.setCountryName(CountryName.CHI);
            country.setCode(CountryName.CHI.toCode());
        } else if (countryName.equalsIgnoreCase("JPN")) {
            country.setCountryName(CountryName.JPN);
            country.setCode(CountryName.JPN.toCode());
        }
        else{
            throw new Exception("Country not found");
        }
        User user = new User();
        user.setPassword(password);//
        user.setUsername(username);//
        user.setOriginalCountry(country);//
        user.setConnected(false);//
        country.setUser(user);//

        User userFromRepo = userRepository3.save(user);
        user.setOriginalIp(country.getCode()+"."+userFromRepo.getId());
        userRepository3.save(user);
        return user;

    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).get();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();
        user.getServiceProviderList().add(serviceProvider);

        serviceProvider.getUsers().add(user);

        userRepository3.save(user);
        return user;
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
