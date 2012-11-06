package com.google.android.gcm.demo.server;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Servlet that unfavourites a team with a device, whose device id is identified by
 * {@link #PARAMETER_DEVICE_ID}.
 *
 * <p>
 * The client app should call this servlet when the user
 * unfavourites a football team
 */
@SuppressWarnings("serial")
public class UnfavouriteServlet extends BaseServlet {
	
		 private static final String PARAMETER_DEVICE_ID = "deviceId";
		 private static final String PARAMETER_TEAM = "team";
		 
		  @Override
		  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
		      throws ServletException {
		    String deviceId = getParameter(req, PARAMETER_DEVICE_ID);
		    String team = getParameter(req, PARAMETER_TEAM);
		    Datastore.unfavourite(team,deviceId);
		    setSuccess(resp);
		  }
	}

