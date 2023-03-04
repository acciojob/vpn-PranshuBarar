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
        return adminRepository1.save(admin);
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        Admin admin = adminRepository1.findById(adminId).get();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);
        admin.getServiceProviders().add(serviceProvider);
        return adminRepository1.save(admin);
    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();
        List<Country> countryList = serviceProvider.getCountryList();

        Country country = new Country();

        if(!caseIgnoreCheckAndEnumCheck(countryName)){
            throw new Exception("Country not found");
        }

        country.setCountryName(CountryName.valueOf(countryName));
//        try {
//            country.setCountryName(CountryName.caseIgnoreCheck(countryName));
//        } catch (Exception e){
//            throw new Exception("Country not found");
//        }

        country.setCode(CountryName.valueOf(countryName).toCode());
        countryList.add(country);

        return serviceProviderRepository1.save(serviceProvider);
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
