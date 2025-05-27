package io.ossnass.advSpring;


//import io.ossnass.ucontrol.backend.v1.organization.users.Users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * The base class for protocolFQ contains one inheritable attribute, logger allowing fast and simple logging
 */
public class Loggable {

    /**
     * The logger
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * get the IP of the request
     *
     * @return the IP of the request
     */
    protected String getIpOfRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest()
                .getRemoteAddr();
    }

    /*/**
     * Returns the user of the request, requires a user to be logged in and the access token to be sent
     *
     * @return the user of the request
     */
    //DISABLED FOR NOT ENABLING SPRING BOOT SECURITY
//    protected UserDetails getUserOfRequest() {
//        return (UserDetails) SecurityContextHolder.getContext()
//                                                  .getAuthentication()
//                                                  .getPrincipal();
//    }
}
