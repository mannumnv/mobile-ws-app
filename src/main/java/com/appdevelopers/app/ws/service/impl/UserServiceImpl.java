package com.appdevelopers.app.ws.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.appdevelopers.app.ws.exceptions.UserServiceException;
import com.appdevelopers.app.ws.io.entity.PasswordResetTokenEntity;
//import com.appdevelopers.app.ws.io.entity.AddressEntity;
import com.appdevelopers.app.ws.io.entity.UserEntity;
import com.appdevelopers.app.ws.io.repositories.PasswordResetTokenRepository;
import com.appdevelopers.app.ws.io.repositories.UserRepositories;
import com.appdevelopers.app.ws.service.UserService;
import com.appdevelopers.app.ws.shared.AmazonSES;
import com.appdevelopers.app.ws.shared.Utils;
import com.appdevelopers.app.ws.shared.dto.AddressDTO;
import com.appdevelopers.app.ws.shared.dto.UserDto;
import com.appdevelopers.app.ws.ui.model.response.ErrorMessages;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	UserRepositories userRepositories;
	// User repositories is the class we use to query to our database table(we can
	// add any method or condition related to db
	// example : we don`t want the duplicate data in db.
	
	@Autowired
	PasswordResetTokenRepository passwordResetTokenRepository;

	@Autowired
	Utils utils;

	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public UserDto createUser(UserDto user) {

		UserEntity storedUserDetails = userRepositories.findByemail(user.getEmail());

		if (storedUserDetails != null)
			throw new RuntimeException("Record already exist");

		for (int i = 0; i < user.getAddresses().size(); i++) {

			AddressDTO address = user.getAddresses().get(i);
			address.setUserDetails(user);
			address.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, address);

		}

		ModelMapper modelMapper = new ModelMapper();
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);

		// BeanUtils.copyProperties(user, userEntity);

		String publicUserId = utils.generateUserId(30);
		userEntity.setUserId(publicUserId);
		userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userEntity.setEmailVerificationToken(utils.generateEmailVerificationToken(publicUserId));
		userEntity.setEmailVerificationStatus(false);
		// List<AddressEntity> addressEntity = new AddressEntity<>();

		System.out.println(userEntity.getUserId());
		// Saving the information
		UserEntity storedvalue = userRepositories.save(userEntity);
		// UserDto returnValue = new UserDto();
		// BeanUtils.copyProperties(storedvalue, returnValue);
		UserDto returnValue = modelMapper.map(storedvalue, UserDto.class);
		
		// SEND AN EMAIL MESSAGE TO USER TO VERIFY THEIR EMAIL ADDRESS
		//new AmazonSES().verifyEmail(returnValue);
		return returnValue;

	}

	@Override
	public UserDto getUser(String email) {
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepositories.findByemail(email);
		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);

		return returnValue;
	}

	@Override
	public UserDto getUserByUserId(String userId) {

		// 2nd(next step) is to make use of user repository to query our database for a
		// user that will match
		// provided user id
		UserEntity userEntity = userRepositories.findByUserId(userId);
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		// 1st step to create return value (USER DTO thing)
		UserDto returnValue = new UserDto();
		BeanUtils.copyProperties(userEntity, returnValue);

		return returnValue;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepositories.findByemail(email);

		if (userEntity == null)
			throw new UsernameNotFoundException(email);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(),
				userEntity.getEmailVerificationStatus(), true, true, true, new ArrayList<>());
		// here by default get verification email status is false and bcz of that the
		// user will be disabled and that will prevent user from login
		//Once this value userEntity.getEmailVerificationStatus() is set to be true then only user will be able to login
		//when spring framework call loadUserByUsername method
		// return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new
		// ArrayList<>());
	}

	@Override
	public UserDto updateUser(String userId, UserDto user) {
		// TODO Auto-generated method stub
		UserDto returnValue = new UserDto();
		UserEntity userEntity = userRepositories.findByUserId(userId);
		// if(userEntity == null) throw new UsernameNotFoundException(userId); OR Custom
		// exception
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		userRepositories.save(userEntity);
		UserEntity updatedUserDetails = userRepositories.save(userEntity);
		BeanUtils.copyProperties(updatedUserDetails, returnValue);

		return returnValue;

	}

	@Override
	public void deleteUser(String userId) {
		// TODO Auto-generated method stub
		UserEntity userEntity = userRepositories.findByUserId(userId);
		// if(userEntity == null) throw new UsernameNotFoundException(userId); OR Custom
		// exception
		if (userEntity == null)
			throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
		userRepositories.delete(userEntity);

	}

	@Override
	public List<UserDto> getUsers(int page, int limit) {
		// TODO Auto-generated method stub
		List<UserDto> returnValue = new ArrayList<>();
		Pageable pageableRequest = PageRequest.of(page, limit);
		Page<UserEntity> userPage = userRepositories.findAll(pageableRequest);
		List<UserEntity> users = userPage.getContent();
		for (UserEntity userEntity : users) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(userEntity, userDto);
			returnValue.add(userDto);
		}
		return returnValue;
	}

	@Override
	public boolean VerifyEmailToken(String token) {
		// TODO Auto-generated method stub

		boolean returnValue = false;

		UserEntity userEntity = userRepositories.findUserByEmailVerificationToken(token);
		if (userEntity != null) {
			boolean hasTokenExpired = Utils.hasTokenExpired(token);
			if (!hasTokenExpired) {
				userEntity.setEmailVerificationToken(null);
				userEntity.setEmailVerificationStatus(Boolean.TRUE);
				userRepositories.save(userEntity);
				returnValue = true;
			}

		}

		return returnValue;
	}

	@Override
	public boolean requestPasswordReset(String email) {
		// TODO Auto-generated method stub
		boolean returnValue = false;
		
		UserEntity userEntity = userRepositories.findByemail(email);
		if(userEntity == null)
		{
			return returnValue;
		}
		
		String token = new  Utils().generatePasswordResetToken(userEntity.getUserId());
		
		PasswordResetTokenEntity passwordResetTokenEntity = new PasswordResetTokenEntity();
		passwordResetTokenEntity.setToken(token);
		passwordResetTokenEntity.setUserDetails(userEntity);
		passwordResetTokenRepository.save(passwordResetTokenEntity);
		
		returnValue = new AmazonSES().sendPasswordResetRequest(userEntity.getFirstName(),
				userEntity.getEmail(),
				token);
		
		return returnValue;
	}

	@Override
	public boolean resetPassword(String token, String password) {
		// TODO Auto-generated method stub
boolean returnValue = false;
        
        if( Utils.hasTokenExpired(token) )
        {
            return returnValue;
        }
 
        PasswordResetTokenEntity passwordResetTokenEntity = passwordResetTokenRepository.findByToken(token);

        if (passwordResetTokenEntity == null) {
            return returnValue;
        }

        // Prepare new password
        String encodedPassword = bCryptPasswordEncoder.encode(password);
        
        // Update User password in database
        UserEntity userEntity = passwordResetTokenEntity.getUserDetails();
        userEntity.setEncryptedPassword(encodedPassword);
        UserEntity savedUserEntity = userRepositories.save(userEntity);
 
        // Verify if password was saved successfully
        if (savedUserEntity != null && savedUserEntity.getEncryptedPassword().equalsIgnoreCase(encodedPassword)) {
            returnValue = true;
        }
   
        // Remove Password Reset token from database
        passwordResetTokenRepository.delete(passwordResetTokenEntity);
        
        return returnValue;
	}
	
	


}
