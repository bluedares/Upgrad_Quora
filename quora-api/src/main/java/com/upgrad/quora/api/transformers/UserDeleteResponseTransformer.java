package com.upgrad.quora.api.transformers;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.common.Constants;
import org.springframework.stereotype.Component;

@Component
public class UserDeleteResponseTransformer {
    /**
     *
     * @param uuid
     * @return
     * This method creates the user delete response object for the deleted user id.
     */
    public UserDeleteResponse transform(String uuid){
        UserDeleteResponse userDeleteResponse = new UserDeleteResponse().id(uuid).status(Constants.USER_DELETED);
        return userDeleteResponse;
    }
}
