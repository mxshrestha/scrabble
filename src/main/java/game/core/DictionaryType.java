package game.core;

/**
 * @author Manish Shrestha
 */
public enum DictionaryType {
    SOWPODS("sowpods", "dictionary/SOWPODS.txt"),
    TWL06("twl06", "dictionary/TWL06.txt");

    private final String name;
    private final String filePath;

    DictionaryType(String name, String filePath) {
        this.name = name;
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }

    public static DictionaryType fromName(String name) {
        if (DictionaryType.SOWPODS.name.equalsIgnoreCase(name)) {
            return DictionaryType.SOWPODS;
        } else if (DictionaryType.TWL06.name.equalsIgnoreCase(name)) {
            return DictionaryType.TWL06;
        }
        throw new RuntimeException("Unsupported dictionary type specified. Dictionary: " + name);
    }
}
