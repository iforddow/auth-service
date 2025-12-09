package com.iforddow.authservice.common.utility;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

/*
* A utility class for extracting cookie values from HTTP requests.
*
* @author IFD
* @since 2025-12-08
* */
@Component
public class CookieUtility {

    /**
    * A method to extract a cookie value from the HTTP request.
    * If a cookie with the specified name is found, its value is returned.
    * If no such cookie is found, the method returns null. Note that if
    * a cookie somehow has multiple values, only the first one encountered
    * will be returned.
    *
    * @param cookieName The name of the cookie to extract.
    * @param request The HTTP servlet request containing the cookies.
    * @return The value of the cookie if found; otherwise, null.
    *
    * @author IFD
    * @since 2025-12-08
    * */
    public String getCookieValue(String cookieName, HttpServletRequest request) {

        Cookie[] cookies = request.getCookies();

        String cookieValue = null;

        if(cookies == null) {
            return cookieValue;
        }

        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(cookieName)) {
                cookieValue = cookie.getValue();
                break;
            }
        }

        return cookieValue;

    }

}
