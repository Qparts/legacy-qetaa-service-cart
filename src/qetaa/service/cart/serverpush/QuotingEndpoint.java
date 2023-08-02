package qetaa.service.cart.serverpush;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.Response;

import qetaa.service.cart.helpers.AppConstants;

@ServerEndpoint(value = "/ws/quoting/user/{username}/token/{token}")
public class QuotingEndpoint {

	private Session session;
	private int userId;
	private String token;
	private static Set<QuotingEndpoint> quotingEndpoints = new CopyOnWriteArraySet<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("username") Integer userId,  @PathParam("token") String token) throws IOException {
		this.session = session;
		this.userId = userId;
		this.token = token;
		if(tokenMatched()) {
			quotingEndpoints.add(this);
		}
		else {
			session.close();
		}
	}

	@OnMessage
	public void onMessage(Session session, String message) throws IOException {

	}

	@OnClose
	public void onClose(Session session) throws IOException {
		quotingEndpoints.remove(this);
	}

	@OnError
	public void onError(Session session, Throwable throwable) {
		// Do error handling here
	}
	
	public static void sendToUser(String message, int userId) {
		quotingEndpoints.forEach(endpoint -> {
			synchronized (endpoint) {
				if (endpoint.session.isOpen()) {
					if(endpoint.userId == userId) {
						endpoint.session.getAsyncRemote().sendText(message);
					}
				}
			}
		});
	}

	public static void broadcast(String message) {
		quotingEndpoints.forEach(endpoint -> {
			synchronized (endpoint) {
				if (endpoint.session.isOpen()) {
					endpoint.session.getAsyncRemote().sendText(message);
				}
			}
		});
	}
	
	
	private boolean tokenMatched() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("token", token);
		map.put("userId", userId);
		Response r = this.postNoneSecuredRequest(AppConstants.USER_MATCH_TOKEN_WS, map);
		if(r.getStatus() == 200) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public <T> Response postNoneSecuredRequest(String link, T t) {
		Builder b = ClientBuilder.newClient().target(link).request();
		Response r = b.post(Entity.entity(t, "application/json"));// not secured
		return r;
	}

}
