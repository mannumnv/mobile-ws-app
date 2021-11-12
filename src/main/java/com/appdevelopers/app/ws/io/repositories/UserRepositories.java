package com.appdevelopers.app.ws.io.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.appdevelopers.app.ws.io.entity.UserEntity;

//Crud repositories provide a ready to use method (crud) apart from that we can add own own method as well.
@Repository
// data base table is define under userEntity
public interface UserRepositories extends PagingAndSortingRepository<UserEntity, Long> {
	
	UserEntity findByemail(String email);
	//here it will query to the data base table which is define is user Entity named as Users and return  it as user entity.
	UserEntity findByUserId(String Id);
	//when findByUserId method is called spring data JPA will do all the work
	//it will create a sql query , connect to DATABASE using DATABASE connection provided in app properties
	// and it will find that record and if record is found then it will create a USER ENTITY OBJECT for us
	//and return it to our service implementation
	UserEntity findUserByEmailVerificationToken(String token);
	
	@Query(value="Select *from Users u with u.EMAIL_VERIFICATION_STATUS='true'",
			countQuery="Select count(*)from Users u with u.EMAIL_VERIFICATION_STATUS='true'",
			nativeQuery=true)
	Page<UserEntity> findAllUsersWithConfirmedUserAddress(Pageable pageableRequest);
	
}
