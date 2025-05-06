package eu.flare.model;

import java.util.HashMap;
import java.util.Map;

public enum StoryProgressType {
    TODO("Todo"),
    IN_PROGRESS("In Progress"),
    IN_REVIEW("In Review"),
    DONE("Done");

    private final String progressType;
    private static final Map<String, StoryProgressType> progressTypeMap = new HashMap<>();

    static {
        progressTypeMap.put("Todo", StoryProgressType.TODO);
        progressTypeMap.put("In Progress", StoryProgressType.IN_PROGRESS);
        progressTypeMap.put("In Review", StoryProgressType.IN_REVIEW);
        progressTypeMap.put("Done", StoryProgressType.DONE);
    }

    StoryProgressType(String type) {
        this.progressType = type;
    }

    public String getProgressType() {
        return progressType;
    }

    public static StoryProgressType valueOfLabel(String label) {
        return progressTypeMap.get(label);
    }
}
