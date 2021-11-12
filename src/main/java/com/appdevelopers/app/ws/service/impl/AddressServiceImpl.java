package com.appdevelopers.app.ws.service.impl;

//import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.appdevelopers.app.ws.io.entity.AddressEntity;
import com.appdevelopers.app.ws.io.entity.UserEntity;
import com.appdevelopers.app.ws.io.repositories.AddressRepositories;
import com.appdevelopers.app.ws.io.repositories.UserRepositories;
import com.appdevelopers.app.ws.service.AddressService;
import com.appdevelopers.app.ws.shared.dto.AddressDTO;

@Service
public class AddressServiceImpl implements AddressService{

	@Autowired
	UserRepositories userRepositories;
	
	@Autowired
	AddressRepositories addressRepositories;
	
	@Override
	public List<AddressDTO> getAddresses(String userId) {
		List<AddressDTO> returnValue = new ArrayList<>();
		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = userRepositories.findByUserId(userId);
		
		if(userEntity == null) return returnValue;
		try {
		Iterable<AddressEntity> addresses = addressRepositories.findAllByUserDetails(userEntity);
		
		for(AddressEntity addressEntity : addresses) {
			
			returnValue.add(modelMapper.map(addressEntity, AddressDTO.class));
			
		}}
		catch(Exception e)
		{
			e.getCause().printStackTrace();
		}
		
		return returnValue;
	}

	@Override
	public AddressDTO getAddress(String addressId) {
		
		AddressDTO returnValue = null;
		AddressEntity addressEntity = addressRepositories.findByAddressId(addressId);
		ModelMapper modelMapper = new ModelMapper();
		if(addressEntity!=null) {
		returnValue = modelMapper.map(addressEntity, AddressDTO.class);
		}
		return returnValue;
	}

}
