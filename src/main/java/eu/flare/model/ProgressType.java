package eu.flare.model;

import java.util.HashMap;
import java.util.Map;

public enum ProgressType {
    TODO("Todo"),
    IN_PROGRESS("In Progress"),
    IN_REVIEW("In Review"),
    DONE("Done");

    private final String progressType;
    private static final Map<String, ProgressType> progressTypeMap = new HashMap<>();

    static {
        progressTypeMap.put("Todo", ProgressType.TODO);
        progressTypeMap.put("In Progress", ProgressType.IN_PROGRESS);
        progressTypeMap.put("In Review", ProgressType.IN_REVIEW);
        progressTypeMap.put("Done", ProgressType.DONE);
    }

    ProgressType(String type) {
        this.progressType = type;
    }

    public String getProgressType() {
        return progressType;
    }

    public static ProgressType valueOfLabel(String label) {
        return progressTypeMap.get(label);
    }
}
