package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserDetailsResponseTransformer {
    /**
     *
     * @param userEntity
     * @return
     * This method returns the user details response object for the fetched user entity.
     */
    public UserDetailsResponse transform(UserEntity userEntity){
        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().firstName(userEntity.getFirstName()).userName(userEntity.getUserName())
                .lastName(userEntity.getLastName()).contactNumber(userEntity.getContactNumber()).emailAddress(userEntity.getEmail())
                .dob(userEntity.getDob()).country(userEntity.getCountry()).aboutMe(userEntity.getAboutMe());
        return userDetailsResponse;
    }
}
