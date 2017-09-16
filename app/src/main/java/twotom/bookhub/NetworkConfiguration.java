package twotom.bookhub;

class NetworkConfiguration {
    private static boolean useLocalHost = true;

    static String getURL() {
        return useLocalHost
             ? "http://10.0.2.2:8080/"
             : "http://bookhub-backend.herokuapp.com/";
    }
}