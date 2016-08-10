package com.vishnukl.alexa.avalon;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.*;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AvalonSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(AvalonSpeechlet.class);
    private Narrator narrator;

    @Override
    public void onSessionStarted(SessionStartedRequest request, Session session) throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        initializeComponents();
    }

    private void initializeComponents() {
        if (narrator == null) {
            narrator = new Narrator();
        }
    }

    @Override
    public SpeechletResponse onLaunch(LaunchRequest request, Session session) throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        return narrator.getWelcomeMessage();
    }

    @Override
    public SpeechletResponse onIntent(IntentRequest intentRequest, Session session) throws SpeechletException {
        return narrator.respondTo(intentRequest.getIntent(), session);
    }

    @Override
    public void onSessionEnded(SessionEndedRequest sessionEndedRequest, Session session) throws SpeechletException {
    }
}
