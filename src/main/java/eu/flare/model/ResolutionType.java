package eu.flare.model;

public enum ResolutionType {
    DONE("Done"),
    WONT_FIX("Will not fix");

    private final String resolutionType;

    ResolutionType(String resolutionType) {
        this.resolutionType = resolutionType;
    }

    public String getResolutionType() {
        return resolutionType;
    }
}
