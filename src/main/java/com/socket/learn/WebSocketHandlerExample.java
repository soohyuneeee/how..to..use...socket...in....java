package com.socket.learn;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketHandlerExample extends TextWebSocketHandler {
    private static final ConcurrentHashMap<String, WebSocketSession> CLIENTS = new ConcurrentHashMap<String, WebSocketSession>();

    //사용자가 연결 되었을 때 사용자를 CLIENT에 담기
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        CLIENTS.put(session.getId(), session); //고유 아이디 값을 키로 하고 세션을 값으로 넣어준다.
    }
    //연결이 끊겼을 때 CLIENT 객체에서 put 했던 값을 삭제한다. (접속이 끊겼으니 메세지가 가지 않도록)
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        CLIENTS.remove(session.getId());
    }
    //사용자가 메세지를 받게 되면
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String id = session.getId();
        CLIENTS.entrySet().forEach( arg -> { //CLIENTS 객체에 담긴 세션값들을 가져와서 반복(객체 담는 것은 위의 afterConnectionEstablished 매소드)
            if(!arg.getKey().equals(id)) { //같은 아이디가 아니면 메세지를 전달한다. (자기자신에게는 보내지 않음)
                try {
                    arg.getValue().sendMessage(message); //CLIENT 객체에 접속한 사용자를 담았으므로 자기사진 이외의 사용자에게 메세지를 보낼 수 있다.
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        });
    }
}
