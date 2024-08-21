package edu.rpi.cs.chat.chatapp;

import jakarta.websocket.*;

import java.net.URI;

/**
 * Class which acts as a web socket endpoint for the chat application. Sends and receives messages 
 * to be displayed on the app.
 * @author lungua
 */
public class ChatWebSocketEndpoint extends Endpoint {
	private Session session;
	private ChatController window;
	
	private ChatMessageHandler messageHandler;
	
	/**
	 * Connects this web socket endpoint to the web socket server specified by endpointURI.
	 * @param endpointURI web socket server URI for the chat server.
	 * @param window the chatcontroller the endpoint is connected to
	 */
	public ChatWebSocketEndpoint(URI endpointURI, ChatController window) {
		try {
			WebSocketContainer container = ContainerProvider.getWebSocketContainer();
			container.connectToServer(this, endpointURI);
			this.window = window;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Callback hook for opening the connection. Connects the newly opened user session to this object.
	 * @param userSession newly opened user session on the server.
	 */
	@OnOpen
	public void onOpen(Session userSession, EndpointConfig endpointConfig) {
		this.session = userSession;
		System.out.println("Connection Opened");
		this.session.addMessageHandler(new ChatMessageHandler());
	}


    /**
     * Callback hook for receiving. This method will be invoked when a client receives a message.
     *
     * @param message Message received from the server.
     */
    @OnMessage
    public void receiveMessage(String message) {
    	System.out.println("received message: " + message);
//        if(this.messageHandler != null) {
//        	this.messageHandler.onMessage(message);
//        }
    }


    /**
     * Send a message to the websocket. Should be in json format.
     *
     * @param message JSON message to send, with information about message contents, message recipient, etc.
     */
    public void sendMessage(String message) {
    	System.out.println("Websocket: sending message: " + message);
		this.session.getAsyncRemote().sendText(message, new SendHandler() {
			@Override
			public void onResult(SendResult result) {
				if (result.isOK()) {
					System.out.println("Message sent successfully!");
				} else {
					System.err.println("Message failed to send: " + result.getException());
				}
			}
		});
    }

	/**
	 * Close websocket connection; for closing the app or logging out.
	 */
	public void close() {
		if (this.session != null && this.session.isOpen()) {
            try {
                this.session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Application closed"));
                
                System.out.println("WebSocket connection closed.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	/**
	 * Callback hook for closing the connection. Sets this object's session to null.
	 * @param userSession session which is being closed
	 * @param reason reason for connection closing.
	 */
	@Override
	public void onClose(Session userSession, CloseReason reason) {
		super.onClose(session, reason);
		System.out.println("closing websocket. Reason: " + reason.getReasonPhrase()+", code: " + reason.getCloseCode().getCode());
		this.session = null;
	}
	
	/**
	 * Message handler which communicates with the chat application upon receiving a message.
	 * @author lungua
	 */
	private class ChatMessageHandler implements MessageHandler.Whole<String>{

		/**
		 * Called when the endpoint receives a message so the app can be updated with the newly received message.
		 */
		@Override
		public void onMessage(String message) {
			System.out.println("Message received: " + message);	
			window.receiveMessage(message);
		}
		
	}

}
