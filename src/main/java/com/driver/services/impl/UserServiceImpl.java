package com.driver.services.impl;

import com.driver.model.*;
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
        if(!caseIgnoreCheckAndEnumCheck(countryName.toUpperCase().substring(0,3))){
            throw new Exception();
        }
        country.setCountryName(CountryName.valueOf(countryName.toUpperCase().substring(0,3)));
        country.setCode(CountryName.valueOf(countryName.toUpperCase().substring(0,3)).toCode());

        User user = new User();
        user.setPassword(password);//
        user.setUsername(username);//
        user.setOriginalCountry(country);//
        user.setConnected(false);//
        user.setMaskedIp(null);//
        country.setUser(user);

        List<ServiceProvider> serviceProviderList = serviceProviderRepository3.findAll();
        for(ServiceProvider serviceProvider : serviceProviderList){
            List<Country> countryList = serviceProvider.getCountryList();
            for(Country country1 : countryList){
                if(country1.getCountryName().toCode().equals(country.getCode())){
                    user.getServiceProviderList().add(serviceProvider);
                    break;
                }
            }
        }
        User userFromRepo = userRepository3.save(user);
        userFromRepo.setOriginalIp(country.getCode()+"."+userFromRepo.getId());
        User userFromRepoUpdated = userRepository3.save(userFromRepo);
        return userFromRepoUpdated;

    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).get();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();
        user.getServiceProviderList().add(serviceProvider);


        Connection connection = new Connection();
        connection.setUser(user);
        connection.setServiceProvider(serviceProvider);
        user.getConnectionList().add(connection);
        user.getServiceProviderList().add(serviceProvider);
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
