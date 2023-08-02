package qetaa.service.cart.filters;

import java.io.IOException;

import javax.annotation.Priority;
import javax.ejb.EJB;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import qetaa.service.cart.dao.DAO;
import qetaa.service.cart.model.security.WebApp;

@ValidApp
@Provider
@Priority(Priorities.AUTHENTICATION)
public class ApplicationValidityAgent implements ContainerRequestFilter {

	@EJB
	private DAO dao;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		try{
			// GET http autherization header from the request
			String authHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
			// check if http authorization header is present and formatted correctly
			if (authHeader == null || !authHeader.startsWith("Bearer")) {
				requestContext.abortWith(Response.status(401).build());
			} else {
				String[] values = authHeader.split("&&");
				String appSecret = values[2].trim();
				try {
					// Validate app secret
					getWebAppFromSecret(appSecret);
				} catch (Exception e) {
					requestContext.abortWith(Response.status(401).build());
				}
			}
		}catch (Exception e){
			requestContext.abortWith(Response.status(401).build());
		}

	}

	// retrieves app object from app secret
	private WebApp getWebAppFromSecret(String secret) throws Exception {
		// verify web app secret
		WebApp webApp = dao.findTwoConditions(WebApp.class, "appSecret", "active", secret, true);
		if (webApp == null) {
			throw new Exception();
		}
		return webApp;
	}

}
