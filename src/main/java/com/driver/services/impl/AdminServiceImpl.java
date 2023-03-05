package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import java.awt.*;
import java.awt.*;
import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        adminRepository1.save(admin);
        return admin; //adminRepository1.save(admin);
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin = adminRepository1.findById(adminId).get();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);
        admin.getServiceProviders().add(serviceProvider);
//        serviceProviderRepository1.save(serviceProvider); service provider will be saved automatically, we
        //don't have to do this explicitly because serviceProvider is child of admin
        adminRepository1.save(admin);
        return admin;
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
//        !caseIgnoreCheckAndEnumCheck(countryName.toUpperCase().substring(0,3))
        boolean flag = countryName.equalsIgnoreCase("ind") ||
                countryName.equalsIgnoreCase("aus") ||
                countryName.equalsIgnoreCase("chi") ||
                countryName.equalsIgnoreCase("jpn") ||
                countryName.equalsIgnoreCase("usa");
        if(!flag){
            throw new Exception("Country not found");
        }
        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();
        List<Country> countryList = serviceProvider.getCountryList();

        Country country = new Country();


        country.setCountryName(CountryName.valueOf(countryName.toUpperCase().substring(0,3)));
//        try {
//            country.setCountryName(CountryName.caseIgnoreCheck(countryName));
//        } catch (Exception e){
//            throw new Exception("Country not found");
//        }

        country.setCode(CountryName.valueOf(countryName.toUpperCase().substring(0,3)).toCode());
        countryList.add(country);

        country.setServiceProvider(serviceProvider);
//        serviceProvider.getCountryList().add(country);
        serviceProviderRepository1.save(serviceProvider);
        return serviceProvider;
    }

    public boolean caseIgnoreCheckAndEnumCheck(String countryName){
        for (CountryName countryName1 : CountryName.values()) {
            if (countryName1.name().equalsIgnoreCase(countryName)) {
                return true;
            }
        }
        return false;
    }
}
