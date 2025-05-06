package eu.flare.model;

public enum StoryResolutionType {
    DONE("Done"),
    WONT_FIX("Will not fix");

    private final String resolutionType;

    StoryResolutionType(String resolutionType) {
        this.resolutionType = resolutionType;
    }

    public String getResolutionType() {
        return resolutionType;
    }
}
