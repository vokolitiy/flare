package eu.flare.model;

import java.util.HashMap;
import java.util.Map;

public enum StoryPriorityType {
    MINOR("Minor"),
    MAJOR("Major"),
    SEVERE("Severe"),
    BLOCKER("Blocker");

    private final String priorityType;
    private static final Map<String, StoryPriorityType> priorityTypeMap = new HashMap<>();

    static {
        priorityTypeMap.put("Minor", StoryPriorityType.MINOR);
        priorityTypeMap.put("Major", StoryPriorityType.MAJOR);
        priorityTypeMap.put("Severe", StoryPriorityType.SEVERE);
        priorityTypeMap.put("Blocker", StoryPriorityType.BLOCKER);
    }

    StoryPriorityType(String type) {
        this.priorityType = type;
    }

    public String getPriorityType() {
        return priorityType;
    }

    public static StoryPriorityType valueOfLabel(String label) {
        return priorityTypeMap.get(label);
    }
}
