import java.io.*;
import java.util.HashMap;

public class Authentication {
    private static final String AUTH_FILE = "auth.txt";
    private static final HashMap<String, String> userMap = new HashMap<>();

    public static boolean isRegisteredUser(String username) {
        loadUserData();
        return userMap.containsKey(username);
    }

    public static boolean registerUser(String username, String password) {
        loadUserData();
        if (!userMap.containsKey(username)) {
            userMap.put(username, password);
            saveUserData();
            return true;
        }
        return false;
    }

    public static String getPasswordForUser(String username) {
        loadUserData();
        return userMap.get(username);
    }

    private static void loadUserData() {
        try (BufferedReader br = new BufferedReader(new FileReader(AUTH_FILE))) {
            userMap.clear();
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    String username = parts[0];
                    String password = parts[1];
                    userMap.put(username, password);
                }
            }
        } catch (IOException e) {
            // File does not exist or cannot be read. Ignore.
        }
    }

    private static void saveUserData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(AUTH_FILE))) {
            for (String username : userMap.keySet()) {
                String password = userMap.get(username);
                bw.write(username + ":" + password);
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
