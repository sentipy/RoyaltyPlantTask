package com.sentilabs.royaltyplanttask.utils;

import com.sentilabs.royaltyplanttask.Constants;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by sentipy on 12/07/15.
 */
public class UserUtils {

    public static Long getCurrentUserId(HttpServletRequest httpServletRequest) {
        return (long) httpServletRequest.getSession().getAttribute(Constants.SESSION_ATTR_NAME_WITH_CLIENT_ID);
    }
}
