package circlesvssquares;

public class Session {
    private static Session instance = null;
    
    public static Session instance() {
        if (instance == null) Session.reset();
        return instance;
    }
    public static void reset() {
        instance = new Session();
    }
    
    public int resetsUsed;
    public int radiusLost;
    public Session() {
        this.resetsUsed = 0;
        this.radiusLost = 0;
    }
}
