package com.vishnukl.alexa.avalon;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.HashMap;
import java.util.Map;

public class Narrator {

    private static final String NO_INTENT = "NO_INTENT";
    private static final String SLOT_ROLE_NAME = "RoleName";
    private RoleManager roleManager;

    private Map<String, java.util.function.BiFunction<Intent, Session, SpeechletResponse>> intentMap = new HashMap<>();

    public Narrator() {
        this.roleManager = new RoleManager(new AmazonDynamoDBClient());
        this.intentMap.put("AMAZON.HelpIntent", this::helpHandler);
        this.intentMap.put(NO_INTENT, this::helpHandler);
        this.intentMap.put("AddRoleIntent", this::addRoleHandler);
        this.intentMap.put("RemoveRoleIntent", this::removeRoleHandler);
    }

    public SpeechletResponse respondTo(Intent intent, Session session) {
        String intentName = getIntentName(intent);
        return intentMap.get(intentName).apply(intent, session);
    }

    private SpeechletResponse addRoleHandler(Intent intent, Session session) {
        String roleName = intent.getSlot(SLOT_ROLE_NAME).getValue();
        String result = roleManager.addRole(roleName, session);
        return simpleResponse(result);
    }

    private SpeechletResponse removeRoleHandler(Intent intent, Session session) {
        String roleName = intent.getSlot(SLOT_ROLE_NAME).getValue();
        String result = roleManager.removeRole(roleName, session);
        return simpleResponse(result);
    }

    public SpeechletResponse getWelcomeMessage() {
        return simpleResponse("You can add roles my saying for example, 'add merlin'. And you can start narration by saying, 'start narration'");
    }

    public SpeechletResponse getEndGameMessage() {
        return simpleResponse("I hope minions of modred won!");
    }

    private SpeechletResponse simpleResponse(String result) {
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(result);
        return SpeechletResponse.newTellResponse(speech);
    }

    public SpeechletResponse helpHandler(Intent intent, Session session) {
        return getWelcomeMessage();
    }


    private String getIntentName(Intent intent) {
        return (intent != null) ? intent.getName() : NO_INTENT;
    }
}
