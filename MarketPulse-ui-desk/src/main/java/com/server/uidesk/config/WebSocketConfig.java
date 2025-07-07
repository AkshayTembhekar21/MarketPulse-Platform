package com.server.uidesk.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import com.server.uidesk.websocket.TradeWebSocketHandler;
import com.server.uidesk.websocket.P2PWebSocketHandler;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final TradeWebSocketHandler tradeWebSocketHandler;
    private final P2PWebSocketHandler p2pWebSocketHandler;

    public WebSocketConfig(TradeWebSocketHandler tradeWebSocketHandler, P2PWebSocketHandler p2pWebSocketHandler) {
        this.tradeWebSocketHandler = tradeWebSocketHandler;
        this.p2pWebSocketHandler = p2pWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Register trade updates WebSocket endpoint
        registry.addHandler(tradeWebSocketHandler, "/ws/trades")
                .setAllowedOriginPatterns("*");
        
        // Register P2P trading WebSocket endpoint
        registry.addHandler(p2pWebSocketHandler, "/ws/p2p")
                .setAllowedOriginPatterns("*");
    }
}
