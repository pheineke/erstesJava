import java.io.*;
import java.util.HashMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class Authentication {
    private static final String AUTH_FILE = "auth.json";
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
            StringBuilder jsonData = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                jsonData.append(line);
            }

            java.lang.reflect.Type type = new TypeToken<HashMap<String, String>>() {}.getType();
            Gson gson = new Gson();
            userMap.clear();
            userMap.putAll(gson.fromJson(jsonData.toString(), type));
        } catch (IOException e) {
            // File does not exist or cannot be read. Ignore.
        }
    }

    private static void saveUserData() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(AUTH_FILE))) {
            Gson gson = new Gson();
            String jsonData = gson.toJson(userMap);
            bw.write(jsonData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
