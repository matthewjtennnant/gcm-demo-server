package com.google.android.gcm.demo.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that favourites a team with a user, whose registration id is identified by {@link #PARAMETER_REG_ID}.
 * 
 * <p>
 * The client app should call this servlet when it the user favourites a football team
 */
@SuppressWarnings("serial")
public class FavouriteServlet extends BaseServlet {
    private static final String PARAMETER_UNIQUE_ID = "userId";
    private static final String PARAMETER_TEAM = "team[0]";
	 
	  @Override
	  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
	      throws ServletException {
	    String deviceId = getParameter(req, PARAMETER_UNIQUE_ID);
	    String team = getParameter(req, PARAMETER_TEAM);
	    Datastore.favourite(team,deviceId);
	    setSuccess(resp);
	  }
}
