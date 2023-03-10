package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user = userRepository2.findById(userId).get();
        if(user.getMaskedIp()!=null){
            throw new Exception("Already connected");
        }

        String countryOfUser = user.getOriginalCountry().getCountryName().toString();
        if(countryOfUser.equalsIgnoreCase(countryName)){
            return user;
        }
        //--------------------------------------------=======================================
        if(user.getServiceProviderList().size()==0){
            throw new Exception("Unable to connect");
        }
        List<ServiceProvider> serviceProviderListOfUser = user.getServiceProviderList();
        boolean anyOneOfTheServiceProviderHasThisCountry = false;
        Country countryProviderServes = null;
        ServiceProvider serviceProviderUserIsGettingConnectedToKnow = null;
        int idOfThisServiceProvider = Integer.MAX_VALUE;
        for(ServiceProvider serviceProvider : serviceProviderListOfUser){
            for(Country countryThisProviderServes : serviceProvider.getCountryList()){
                if(countryName.equalsIgnoreCase(countryThisProviderServes.getCountryName().toString())
                        && serviceProvider.getId()<idOfThisServiceProvider){
                    anyOneOfTheServiceProviderHasThisCountry = true;
                    countryProviderServes = countryThisProviderServes;
                    idOfThisServiceProvider = serviceProvider.getId();
                    serviceProviderUserIsGettingConnectedToKnow = serviceProvider;
                }
            }
        }
        if(!anyOneOfTheServiceProviderHasThisCountry){
            throw new Exception("Unable to connect");
        }
        Connection connection = new Connection();
        connection.setUser(user);
        connection.setServiceProvider(serviceProviderUserIsGettingConnectedToKnow);
        user.setConnected(true);
        user.setMaskedIp(countryProviderServes.getCode()+"."+idOfThisServiceProvider+"."+userId);
        serviceProviderUserIsGettingConnectedToKnow.getConnectionList().add(connection);

        user.getConnectionList().add(connection);


        userRepository2.save(user);
        serviceProviderRepository2.save(serviceProviderUserIsGettingConnectedToKnow);

        return user;

    }
    @Override
    public User disconnect(int userId) throws Exception {

        User user = userRepository2.findById(userId).get();
        if(!user.getConnected()){
            throw new Exception("Already disconnected");
        }
        user.setConnected(false);
        user.setMaskedIp(null);
        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();
        if(!receiver.getConnected()){
//            sender.getOriginalCountry().getCountryName().toCode().equals(receiver.getOriginalCountry().getCountryName().toCode())
            if(sender.getOriginalCountry().equals(receiver.getOriginalCountry())){
                return sender;
            }
            User connectedSender;
            try {
                connectedSender = connect(senderId,receiver.getOriginalCountry().getCountryName().toString());
            } catch(Exception e){
                throw new Exception("Cannot establish communication");
            }

            if(!connectedSender.getConnected()){
                throw new Exception("Cannot establish communication");
            }
            return connectedSender;

        }
        else {
            if(sender.getOriginalCountry().getCountryName().toCode().equals(receiver.getMaskedIp().substring(0,3))){
                return sender;
            }
            User connectSender;
            try {
                connectSender = connect(senderId, returnCountry(receiver.getMaskedIp().substring(0,3)).getCountryName().toString());
            } catch (Exception e){
                throw new Exception("Cannot establish communication");
            }

            if(!connectSender.getConnected()){
                throw new Exception("Cannot establish communication");
            }
            return connectSender;
        }
    }

    public Country returnCountry(String code){
        CountryName countryName = null;
        switch(code){
            case("001"):{
                countryName = CountryName.IND;
                break;
            }
            case("002"):{
                countryName = CountryName.USA;
                break;
            }case("003"):{
                countryName = CountryName.AUS;
                break;
            }case("004"):{
                countryName = CountryName.CHI;
                break;
            }case("005"):{
                countryName = CountryName.JPN;
            }
        }
        Country country = new Country();
        country.setCountryName(countryName);
        country.setCode(code);
        return country;
    }
}
