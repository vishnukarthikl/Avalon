package com.vishnukl.alexa.avalon;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

import java.util.HashSet;
import java.util.Set;

public final class AvalonSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds;

    static {
        supportedApplicationIds = new HashSet<>();
        supportedApplicationIds.add("amzn1.ask.skill.0bffbda7-f0f0-49fd-a5eb-54a47441376e");
    }

    public AvalonSpeechletRequestStreamHandler() {
        super(new AvalonSpeechlet(), supportedApplicationIds);
    }
}
