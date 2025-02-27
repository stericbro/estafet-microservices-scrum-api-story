package com.estafet.microservices.api.story.jms;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.estafet.microservices.api.story.event.MessageEventHandler;
import com.estafet.microservices.api.story.model.Task;
import com.estafet.microservices.api.story.service.StoryService;

import io.opentracing.Tracer;

@Component
public class NewTaskConsumer {

    public final static String TOPIC = "new.task.topic";

    @Autowired
    private Tracer tracer;

    @Autowired
    private StoryService storyService;

    @Autowired
    private MessageEventHandler messageEventHandler;

    @JmsListener(destination = TOPIC, containerFactory = "myFactory")
    public void onMessage(String message, @Header("message.event.interaction.reference") String reference) {
        try {
            if (messageEventHandler.isValid(TOPIC, reference)) {
                storyService.newTask(Task.fromJSON(message));
            }
        } finally {
            if (tracer.activeSpan() != null) {
                tracer.activeSpan().close();
            }
        }
    }

}
