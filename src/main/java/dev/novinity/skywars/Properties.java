package dev.novinity.skywars;

public class Properties {
    public static String getProperty(String tag) {
        try {
            final java.util.Properties properties = new java.util.Properties();
            properties.load(Properties.class.getClassLoader().getResourceAsStream("project.properties"));
            return properties.getProperty(tag);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
