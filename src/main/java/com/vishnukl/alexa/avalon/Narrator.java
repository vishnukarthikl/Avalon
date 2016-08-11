package com.vishnukl.alexa.avalon;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.*;

public class Narrator {

    private static final String NO_INTENT = "NO_INTENT";
    private static final String SLOT_ROLE_NAME = "RoleName";
    private static final String MODRED = "modred";
    private static final String MERLIN = "merlin";
    private static final String PERCIVAL = "percival";
    private static final String MORGANA = "morgana";
    private RoleManager roleManager;

    private Map<String, java.util.function.BiFunction<Intent, Session, SpeechletResponse>> intentMap = new HashMap<>();

    public Narrator() {
        this.roleManager = new RoleManager(new AmazonDynamoDBClient());
        this.intentMap.put("AMAZON.HelpIntent", this::helpHandler);
        this.intentMap.put(NO_INTENT, this::helpHandler);
        this.intentMap.put("AddRoleIntent", this::addRoleHandler);
        this.intentMap.put("RemoveRoleIntent", this::removeRoleHandler);
        this.intentMap.put("StartNarrationIntent", this::startNarrationHandler);
    }

    public SpeechletResponse respondTo(Intent intent, Session session) {
        String intentName = getIntentName(intent);
        return intentMap.get(intentName).apply(intent, session);
    }

    private SpeechletResponse startNarrationHandler(Intent intent, Session session) {
        List<String> roles = roleManager.getRoles(session);
        return narrate(new HashSet<>(roles));
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

    private SpeechletResponse narrate(Set<String> roles) {
        SsmlOutputSpeech ssmlOutputSpeech = new SsmlOutputSpeech();
        StringBuilder speechBuilder = new StringBuilder();
        speechBuilder.append("<speak>");
        addMinions(speechBuilder);
        addMerlin(roles, speechBuilder);
        addPercival(roles, speechBuilder);
        speechBuilder.append("Everyone, open your eyes.");
        speechBuilder.append("</speak>");
        ssmlOutputSpeech.setSsml(speechBuilder.toString());
        return SpeechletResponse.newTellResponse(ssmlOutputSpeech);
    }

    private void addPercival(Set<String> roles, StringBuilder speechBuilder) {
        if (roles.contains(PERCIVAL)) {
            if (roles.contains(MORGANA) && roles.contains(MERLIN)) {
                speechBuilder.append("Merlin and Morgana, extend your thumbs so Percival will know of you.");
            } else if (roles.contains(MERLIN)) {
                speechBuilder.append("Merlin, extend your thumb so Percival will know of you.");
            } else if (roles.contains(MORGANA)) {
                speechBuilder.append("Morgana, extend your thumb so Percival will know of you.");
            }
            addBreak(speechBuilder, 1);
            speechBuilder.append("Percival, open your eyes.");
            addBreak(speechBuilder, 3);
            if (roles.contains(MORGANA) && roles.contains(MERLIN)) {
                speechBuilder.append("Merlin and Morgana, put your thumbs down.");
            } else if (roles.contains(MERLIN)) {
                speechBuilder.append("Merlin, put your thumb down.");
            } else if (roles.contains(MORGANA)) {
                speechBuilder.append("Morgana, put your thumb down.");
            }
            addBreak(speechBuilder, 1);
            speechBuilder.append("Percival, close your eyes.");
            addBreak(speechBuilder, 1);
        }
    }

    private void addMerlin(Set<String> roles, StringBuilder speechBuilder) {
        if (!roles.contains(MERLIN)) {
            return;
        }
        if (roles.contains(MODRED)) {
            speechBuilder.append("Minions of Mordred, extend your thumb so Merlin will know of you.");
        } else {
            speechBuilder.append("Minions of Mordred, except Mordred himself, extend your thumb so Merlin will know of you.");
        }
        addBreak(speechBuilder, 1);
        speechBuilder.append("Merlin, open your eyes so you will know the agents of evil.");
        addBreak(speechBuilder, 3);
        speechBuilder.append("Minions of Mordred, put your thumbs down. Merlin, close your eyes.");
    }

    private void addMinions(StringBuilder speechBuilder) {
        speechBuilder.append("Minions of Mordred, open your eyes and look around so you know the agents of evil.");
        addBreak(speechBuilder, 3);
        speechBuilder.append("Minions of Mordred, close your eyes.");
        addBreak(speechBuilder, 1);
    }

    private void addBreak(StringBuilder speechBuilder, int seconds) {
        speechBuilder.append("<break time=\"").append(seconds).append("s\"/>");
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
