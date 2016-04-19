package uno.cod.platform.server.websocket.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import uno.cod.platform.server.core.domain.CanonicalEntity;
import uno.cod.platform.server.core.domain.Task;
import uno.cod.platform.server.core.service.IClientPushConnection;

import java.io.IOException;

@Service
public class WebSocketService implements IClientPushConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketService.class);

    private final BiMap<CanonicalEntity, WebSocketSession> sessions = Maps.synchronizedBiMap(HashBiMap.create());

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void addSession(CanonicalEntity entity, WebSocketSession session) {
        WebSocketSession old = sessions.remove(entity);
        if (old != null) {
            LOGGER.debug("Removing session {} from session store in favor of a new one.", old.getId());
            try {
                old.close();
            } catch (IOException ioe) {
                LOGGER.error("Could not close session {}.", session.getId());
            }
        }
        LOGGER.debug("User {} connected with {}.", entity.getCanonicalName(), session.getId());
        sessions.put(entity, session);
    }

    public void removeSession(WebSocketSession session) {
        CanonicalEntity entity = sessions.inverse().remove(session);
        if (entity != null) {
            LOGGER.debug("User {} disconnected from {}.", entity.getCanonicalName(), session.getId());
        } else {
            LOGGER.error("Removing unknown session, this could be a synchronization problem!");
        }
    }

    public void send(CanonicalEntity entity, String message) {
        if (sessions.get(entity) == null) {
            LOGGER.info("Wanted to talk to user {} but have no session.", entity.getCanonicalName());
            return;
        }
        try {
            LOGGER.debug("Sending \"{}\" to user {}.", message, entity.getCanonicalName());
            sessions.get(entity).sendMessage(new TextMessage(message));
        } catch (IOException e) {
            LOGGER.warn("Sending a message to user {} failed.", message, entity.getCanonicalName(), e);
        }
    }

    @Override
    public void sendLevelCompleted(CanonicalEntity entity, Task task) {
        JsonNode obj = objectMapper.createObjectNode();
        ((ObjectNode)obj).put("levelState", "completed");
        ((ObjectNode)obj).put("task", task.getId().toString());
        send(entity, obj.toString());
    }
}
