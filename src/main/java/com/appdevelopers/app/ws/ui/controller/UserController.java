package com.appdevelopers.app.ws.ui.controller;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.appdevelopers.app.ws.exceptions.UserServiceException;
import com.appdevelopers.app.ws.service.AddressService;
import com.appdevelopers.app.ws.service.UserService;
import com.appdevelopers.app.ws.shared.dto.AddressDTO;
import com.appdevelopers.app.ws.shared.dto.UserDto;
import com.appdevelopers.app.ws.ui.model.request.PasswordResetModel;
import com.appdevelopers.app.ws.ui.model.request.PasswordResetRequestModel;
import com.appdevelopers.app.ws.ui.model.request.UserDetailsRequestModel;
import com.appdevelopers.app.ws.ui.model.response.AddressesRest;
import com.appdevelopers.app.ws.ui.model.response.ErrorMessages;
import com.appdevelopers.app.ws.ui.model.response.OperationStatusModel;
import com.appdevelopers.app.ws.ui.model.response.RequestOperationStatus;
import com.appdevelopers.app.ws.ui.model.response.UserRest;

//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//When our application starts, for this class to be able to receive a request, 
//This will register this class as a rest controller and it will be able to receive Http requests when
//they're sent and match the url path.
//One, annotation is called rest controller.

@RestController
@RequestMapping("users") // send to http://localhost:8080/users/
public class UserController {

	 Logger log = LoggerFactory.getLogger(UserController.class);

	@Autowired
	UserService userService;
	@Autowired
	AddressService addressesService;

	@Autowired
	AddressService addressService;

	@GetMapping(path = "/{id}", produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest getUser(@PathVariable String id) {

		UserRest returnValue = new UserRest();
		UserDto userDto = userService.getUserByUserId(id);
		ModelMapper mapper = new ModelMapper();
		returnValue = mapper.map(userDto, UserRest.class);
		// BeanUtils.copyProperties(userDto, returnValue);
		// log.debug("Inside of checkStatus() method ");
		log.error(id);
		return returnValue;
	}

	@PostMapping(consumes = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {

		UserRest returnValue = new UserRest();

//		if (userDetails.getFirstName().isEmpty())
//			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
//		UserDto userDto = new UserDto();
//		BeanUtils.copyProperties(userDetails, userDto);
		// userDTO is a shared class that can be used in different layers
		ModelMapper modelMapper = new ModelMapper();

		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		UserDto createdUser = userService.createUser(userDto);
		// BeanUtils.copyProperties(createdUser, returnValue);
		returnValue = modelMapper.map(createdUser, UserRest.class);

		return returnValue;
	}

	@PutMapping(path = "/{id}", consumes = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE }, produces = { MediaType.APPLICATION_XML_VALUE,
					MediaType.APPLICATION_JSON_VALUE })
	// read the user id @PathVariable String id from the given path path = "/{id}"
	public UserRest updateUser(@PathVariable String id, @RequestBody UserDetailsRequestModel userDetails) {

		UserRest returnValue = new UserRest();

		if (userDetails.getFirstName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());
		UserDto userDto = new UserDto();
		BeanUtils.copyProperties(userDetails, userDto);
		// userDTO is a shared class that can be used in different layers
		UserDto updateUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(updateUser, returnValue);

		return returnValue;
	}

	@DeleteMapping(path = "/{id}",

			produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel deleteUser(@PathVariable String id) {

		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.DELETE.name());
		userService.deleteUser(id);
		returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
		return returnValue;
	}

	// here int page is data type int and page is method argument
	@GetMapping(produces = { MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE })

	public List<UserRest> getUsers(@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "limit", defaultValue = "25") int limit) {
		List<UserRest> returnValue = new ArrayList<>();
		List<UserDto> users = userService.getUsers(page, limit);
		// here userDto will fetch one one value of user
		for (UserDto userDto : users) {
			UserRest userModel = new UserRest();
			BeanUtils.copyProperties(userDto, userModel);
			returnValue.add(userModel);
		}
		return returnValue;
	}

	@GetMapping(path = "/{id}/addresses", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public List<AddressesRest> getUserAddresses(@PathVariable String id) {

		List<AddressesRest> returnValue = new ArrayList<>();

		List<AddressDTO> addressDTO = addressesService.getAddresses(id);

		if (addressDTO != null && !addressDTO.isEmpty()) {
			Type listType = new TypeToken<List<AddressesRest>>() {
			}.getType();
			ModelMapper modelMapper = new ModelMapper();
			returnValue = modelMapper.map(addressDTO, listType);
		}
		// BeanUtils.copyProperties(userDto, returnValue);
		// log.debug("Inside of checkStatus() method ");
		return returnValue;
	}

	@GetMapping(path = "/{userId}/addresses/{addressId}", produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	// 1) method of using hateoas
	// public AddressesRest getUserAddress(@PathVariable String userId,
	// @PathVariable String addressId) {
//2 method using EntityModel
	public EntityModel<AddressesRest> getUserAddress(@PathVariable String userId, @PathVariable String addressId) {

		// AddressesRest returnValue = new AddressesRest();

		AddressDTO addressDTO = addressService.getAddress(addressId);
		ModelMapper modelMapper = new ModelMapper();

		AddressesRest returnValue = modelMapper.map(addressDTO, AddressesRest.class);
		// http://localhost:8080/users/<userId>/addressess/addressId/address
		// try {

		Link userLink = WebMvcLinkBuilder.linkTo(UserController.class).slash(userId).withRel("user");
		// 3rd method using methodOn
		Link addressesLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddresses(userId))
				// .slash(userId).slash("addresses")
				.withRel("addresses");
		Link selfLink = WebMvcLinkBuilder
				.linkTo(WebMvcLinkBuilder.methodOn(UserController.class).getUserAddress(userId, addressId))
				// .slash(userId).slash("addresses")
				// .slash(addressId)
				.withSelfRel();

//		returnValue.add(userLink);
//		returnValue.add(addressesLink);
//		returnValue.add(selfLink);}
		// }

//		catch(Exception e)
//		{
//			e.getCause().printStackTrace();
//			
//		}
		return EntityModel.of(returnValue, Arrays.asList(userLink, addressesLink, selfLink));

		// BeanUtils.copyProperties(userDto, returnValue);
		// log.debug("Inside of checkStatus() method ");
		// return returnValue;
	}
	//http://localhost:8080/mobile-app-ws/users/email-verification?token=jknjnf
	@GetMapping(path="/email-verification" , produces = { MediaType.APPLICATION_XML_VALUE,
			MediaType.APPLICATION_JSON_VALUE })
	public OperationStatusModel VerifyEmailToken(@RequestParam(value = "token") String token )
	
	{
		OperationStatusModel returnValue = new OperationStatusModel();
		returnValue.setOperationName(RequestOperationName.VERIFY_EMAIL.name());
		boolean isVerified = userService.VerifyEmailToken(token);
		if(isVerified)
		{
			returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
			
		}else
		{
			returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
		}
		
		return returnValue;
	}
	
	 /*
     * http://localhost:8080/mobile-app-ws/users/password-reset-request
     * */
    @PostMapping(path = "/password-reset-request", 
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel requestReset(@RequestBody PasswordResetRequestModel passwordResetRequestModel) {
    	OperationStatusModel returnValue = new OperationStatusModel();
 
        boolean operationResult = userService.requestPasswordReset(passwordResetRequestModel.getEmail());
        
        returnValue.setOperationName(RequestOperationName.REQUEST_PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
 
        if(operationResult)
        {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }
    @PostMapping(path = "/password-reset",
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public OperationStatusModel resetPassword(@RequestBody PasswordResetModel passwordResetModel) {
    	OperationStatusModel returnValue = new OperationStatusModel();
 
        boolean operationResult = userService.resetPassword(
                passwordResetModel.getToken(),
                passwordResetModel.getPassword());
        
        returnValue.setOperationName(RequestOperationName.PASSWORD_RESET.name());
        returnValue.setOperationResult(RequestOperationStatus.ERROR.name());
 
        if(operationResult)
        {
            returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());
        }

        return returnValue;
    }
}
