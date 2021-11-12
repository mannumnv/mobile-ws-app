package com.appdevelopers.app.ws.io.repositories;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.appdevelopers.app.ws.io.entity.AddressEntity;
import com.appdevelopers.app.ws.io.entity.UserEntity;

@Repository
public interface AddressRepositories extends CrudRepository<AddressEntity, Long> {
	// Here we need to provide the data type of entity so upper we provide address entity
	//long is bcz data base id is primary key and this id is long
	List<AddressEntity> findAllByUserDetails(UserEntity userEntity);
	AddressEntity findByAddressId(String adressId);

}
