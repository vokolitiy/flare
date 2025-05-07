package eu.flare.model;

import java.util.HashMap;
import java.util.Map;

public enum PriorityType {
    MINOR("Minor"),
    MAJOR("Major"),
    SEVERE("Severe"),
    BLOCKER("Blocker");

    private final String priorityType;
    private static final Map<String, PriorityType> priorityTypeMap = new HashMap<>();

    static {
        priorityTypeMap.put("Minor", PriorityType.MINOR);
        priorityTypeMap.put("Major", PriorityType.MAJOR);
        priorityTypeMap.put("Severe", PriorityType.SEVERE);
        priorityTypeMap.put("Blocker", PriorityType.BLOCKER);
    }

    PriorityType(String type) {
        this.priorityType = type;
    }

    public String getPriorityType() {
        return priorityType;
    }

    public static PriorityType valueOfLabel(String label) {
        return priorityTypeMap.get(label);
    }
}
