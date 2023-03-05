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
        if(user.getConnected()){
            throw new Exception("Already connected");
        }

        String countryOfUser = user.getOriginalCountry().getCountryName().toString();
        if(countryOfUser.equalsIgnoreCase(countryName)){
            user.setConnected(true);
            return user;
        }
        //--------------------------------------------=======================================
        if(user.getServiceProviderList().isEmpty()){
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

//        user.setOriginalIp(CountryName.valueOf(countryName.toUpperCase().substring(0,3)).toCode()+"."+userId);
//        user.setOriginalCountry(countryProviderServes);

//        user.getServiceProviderList().add(serviceProviderUserIsGettingConnectedToKnow);
        serviceProviderUserIsGettingConnectedToKnow.getConnectionList().add(connection);

//        Connection connection1 = connectionRepository2.save(connection);
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
            if(sender.getOriginalCountry().getCode().equals(receiver.getOriginalCountry().getCode())){
                return sender;
            }
            User connectedSender;

            connectedSender = connect(senderId,receiver.getOriginalCountry().getCountryName().toString());
            if(!connectedSender.getConnected()){
                throw new Exception("Cannot establish communication");
            }
            return connectedSender;

        }
        else {
            if(sender.getOriginalCountry().getCode().equals(receiver.getMaskedIp().substring(0,3))){
                return sender;
            }
            List<ServiceProvider> serviceProviderList = receiver.getServiceProviderList();
            int id = Integer.MAX_VALUE;
            String countryNameToGetConnected = null;
            for(ServiceProvider serviceProvider : serviceProviderList){
                if(serviceProvider.getId()<id){
                    countryNameToGetConnected = serviceProvider.getCountryList().get(0).toString();
                    id = serviceProvider.getId();
                }
            }
            User connectSender;
//            try {
                connectSender = connect(senderId, countryNameToGetConnected);
//            } catch (Exception e){
//                throw new Exception("Cannot establish communication");
//            }

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
