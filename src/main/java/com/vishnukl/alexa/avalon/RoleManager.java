package com.vishnukl.alexa.avalon;

import com.amazon.speech.speechlet.Session;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.vishnukl.alexa.avalon.storage.AvalonData;

import java.util.ArrayList;
import java.util.List;

public class RoleManager {
    private final DynamoDBMapper dynamoDBmapper;

    public RoleManager(AmazonDynamoDBClient amazonDynamoDBClient) {
        this.dynamoDBmapper = new DynamoDBMapper(amazonDynamoDBClient);
    }

    public String addRole(String roleName, Session session) {
        AvalonData data = getData(session);
        if (data.getRoles().contains(roleName)) {
            return roleName + " is already added.";
        }
        data.getRoles().add(roleName);
        dynamoDBmapper.save(data);
        return roleName + " is now added.";
    }

    public String removeRole(String roleName, Session session) {
        AvalonData data = getData(session);
        if (!data.getRoles().contains(roleName)) {
            return roleName + " is not in game anyway.";
        }
        data.getRoles().remove(roleName);
        dynamoDBmapper.save(data);
        return roleName + " is now removed.";
    }

    private AvalonData getData(Session session) {
        AvalonData data = new AvalonData();
        data.setCustomerId(session.getUser().getUserId());
        AvalonData existingGameData = this.dynamoDBmapper.load(data);
        if (existingGameData == null) {
            return newInstance(session);
        }
        return existingGameData;
    }

    public static AvalonData newInstance(Session session) {
        AvalonData game = new AvalonData();
        game.setCustomerId(session.getUser().getUserId());
        game.setRoles(new ArrayList<>());
        return game;
    }

    public List<String> getRoles(Session session) {
        AvalonData data = getData(session);
        return data.getRoles();
    }
}
