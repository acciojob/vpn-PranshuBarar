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

        String countryOfUser = user.getOriginalCountry().getCountryName().toString().substring(0,3);
        if(countryOfUser.equalsIgnoreCase(countryName.toUpperCase().substring(0,3))){
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
                if(countryThisProviderServes.getCountryName().toString().equalsIgnoreCase(countryName.substring(0,3).toUpperCase())
                        && serviceProvider.getId()<=idOfThisServiceProvider){
                    if(!anyOneOfTheServiceProviderHasThisCountry){
                        anyOneOfTheServiceProviderHasThisCountry = true;
                    }
                    countryProviderServes = countryThisProviderServes;
                    idOfThisServiceProvider = serviceProvider.getId();
                    serviceProviderUserIsGettingConnectedToKnow = serviceProvider;
                }
            }
        }
        if(!anyOneOfTheServiceProviderHasThisCountry){
            throw new Exception("Unable to connect");
        }
        user.setOriginalIp(CountryName.valueOf(countryName.toUpperCase().substring(0,3)).toCode()+"."+userId);
        user.setOriginalCountry(countryProviderServes);
        user.setConnected(true);
        user.getServiceProviderList().add(serviceProviderUserIsGettingConnectedToKnow);
        Connection connection = new Connection();
        connection.setUser(user);
        connection.setServiceProvider(serviceProviderUserIsGettingConnectedToKnow);
        Connection connection1 = connectionRepository2.save(connection);
        user.getConnectionList().add(connection1);
        user.setMaskedIp(CountryName.valueOf(countryName.substring(0,3).toUpperCase()).toCode()+"."+idOfThisServiceProvider+"."+userId);
        userRepository2.save(user);

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
        user.getConnectionList().clear();
        user.getServiceProviderList().clear();
//        user.setOriginalCountry(returnCountry(user.getOriginalIp().substring(0,3)));
//        user.setOriginalCountry(returnCountry(user.getOriginalIp().substring(0,3)));
        user.getServiceProviderList().clear();
        user.getConnectionList().clear();

        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();
        String currCountryOfReceiver;

        if(receiver.getMaskedIp()==null){
            currCountryOfReceiver = receiver.getOriginalIp().substring(0,3);
        }
        else {
            currCountryOfReceiver = returnCountry(receiver.getMaskedIp().substring(0,3)).getCountryName().toString().substring(0,3);
        }
        String countryOfSender = sender.getOriginalCountry().getCountryName().toString().substring(0,3).toUpperCase();

        if(!currCountryOfReceiver.equals(countryOfSender)){
            //Sender is not connected this time to any vpn
            List<ServiceProvider> listOfServiceProviderReceiverIsConnectedTo = receiver.getServiceProviderList();
            if(listOfServiceProviderReceiverIsConnectedTo.size()==0){
                throw new Exception("Cannot establish communication");
            }
            int minServiceProviderId = Integer.MAX_VALUE;
            Country countrySenderIsToBeConnected = null;
            ServiceProvider serviceProviderForSender = null;
            for(ServiceProvider serviceProvider : listOfServiceProviderReceiverIsConnectedTo){
                if(serviceProvider.getId()<minServiceProviderId){
                     countrySenderIsToBeConnected = serviceProvider.getCountryList().get(0);
                     serviceProviderForSender = serviceProvider;
                     minServiceProviderId = serviceProvider.getId(); ///This was the place I got stuck for 4 hours :)
                }
            }
            sender.setConnected(true);
            sender.setOriginalCountry(countrySenderIsToBeConnected);
            sender.getServiceProviderList().add(serviceProviderForSender);
            userRepository2.save(sender);
        }
        return sender;
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
