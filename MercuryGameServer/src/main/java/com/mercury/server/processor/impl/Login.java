package com.mercury.server.processor.impl;

import com.mario.gateway.socket.SocketSession;
import com.mercury.server.annotation.CommandProcessor;
import com.mercury.server.entity.user.LoginContext;
import com.mercury.server.entity.user.User;
import com.mercury.server.entity.user.UserImpl;
import com.mercury.server.entity.zone.Zone;
import com.mercury.server.entity.zone.ZoneManager;
import com.mercury.server.event.reason.UserDisconnectReason;
import com.mercury.server.exception.MGSException;
import com.mercury.server.exception.ProcessMessageException;
import com.mercury.server.exception.UserAlreadyLoggedInException;
import com.mercury.server.message.MGSMessage;
import com.mercury.server.message.MGSMessageType;
import com.mercury.server.message.impl.LoginMessage;
import com.mercury.server.processor.MGSAbstractProcessor;
import com.mercury.server.response.MGSResponse;
import com.mercury.server.response.impl.LoginResponse;
import com.nhb.eventdriven.Callable;

@CommandProcessor(type = MGSMessageType.LOGIN)
public class Login extends MGSAbstractProcessor {

	@Override
	public MGSResponse execute(String sessionId, MGSMessage message) throws ProcessMessageException {
		ZoneManager zoneManager = getContext().getZoneManager();
		LoginMessage loginMessage = (LoginMessage) message;
		Zone zone = zoneManager.getZone(message.getZoneName());
		LoginContext context = new LoginContext();
		context.setUsername(loginMessage.getUsername());
		context.setPassword(loginMessage.getPassword());
		context.setParams(loginMessage.getParams());
		context.setSessionId(sessionId);

		if (zone == null) {
			throw new ProcessMessageException("request zone " + message.getZoneName() + " doesn't exists");
		}

		try {
			zone.getPlugin().onUserLogin(context);
		} catch (MGSException e) {
			LoginResponse loginResponse = new LoginResponse();
			loginResponse.setErrorCode(e.getErrorCode().getCode());
			loginResponse.setMessage(e.getErrorCode().getMessage());
			loginResponse.setUsername(loginMessage.getUsername());
			loginResponse.setZoneName(loginResponse.getZoneName());
			return loginResponse;
		}

		String username = context.getNewLoginName() != null ? context.getNewLoginName() : context.getUsername();
		UserImpl user = new UserImpl(username, sessionId);
		user.setUserVariables(context.getUserVariables());
		SocketSession socketSession = getContext().getApi().getSocketSession(sessionId);

		if (socketSession == null) {
			throw new ProcessMessageException("user want to login but session maybe closed");
		}

		user.setIP(socketSession.getRemoteAddress().getHostString());
		user.setLoginTime(System.currentTimeMillis());
		
		try {
			zone.getUserManager().addUser(user);
		} catch (UserAlreadyLoggedInException e) {
			User oldUser = e.getUser();
			getLogger().debug("kick exists user: {}", oldUser.getUsername());
			zone.getUserManager().removeUser(oldUser.getUsername(), UserDisconnectReason.KICK_LOGIN_OTHER_DEVICE,
					new Callable() {

						@Override
						public void call(Object... data) {
							try {
								getLogger().debug("readd user {} to zone {}", user.getUsername(), zone.getZoneName());
								zone.getUserManager().addUser(user);
							} catch (UserAlreadyLoggedInException e1) {
								getLogger().error("cannot remove last session", e1);
								try {
									socketSession.close();
								} catch (Exception e) {
									getLogger().debug("close socket session already logged in error", e);
								}
							}
						}
					});
		}

		return null;
	}

}
