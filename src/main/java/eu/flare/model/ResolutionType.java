package eu.flare.model;

import java.util.HashMap;
import java.util.Map;

public enum ResolutionType {
    DONE("Done"),
    WONT_FIX("Will not fix");

    private final String resolutionType;
    private static final Map<String, ResolutionType> resolutionTypeMap = new HashMap<>();

    static {
        resolutionTypeMap.put("Done", ResolutionType.DONE);
        resolutionTypeMap.put("Will not fix", ResolutionType.WONT_FIX);
    }

    ResolutionType(String resolutionType) {
        this.resolutionType = resolutionType;
    }

    public String getResolutionType() {
        return resolutionType;
    }

    public static ResolutionType valueOfLabel(String label) {
        return resolutionTypeMap.get(label);
    }
}
