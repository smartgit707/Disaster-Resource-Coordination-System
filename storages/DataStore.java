package storages;
import java.io.*; 
import java.util.ArrayList;
import java.util.List;
import models.*;

public class DataStore {
    private static final String USER_FILE = "data/users.ser";
    private static final String RESOURCE_FILE = "data/resources.ser";
    private static final String REQUEST_FILE = "data/requests.ser";
    private static final String ALERT_FILE = "data/alerts.ser";

    @SuppressWarnings("unchecked")
    private static <T> List<T> load(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (List<T>) ois.readObject();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private static <T> void save(String fileName, List<T> list) {
        File file = new File(fileName);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();   // Ensures "data" directory exists
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(list);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Users
    public static List<User> loadUsers() { return load(USER_FILE); }
    public static void saveUsers(List<User> users) { save(USER_FILE, users); }

    // --- NEW HELPER METHOD: Find a user by ID ---
    public static User getUserById(int id) {
        List<User> users = loadUsers();
        for (User u : users) {
            if (u.getId() == id) return u;
        }
        return null;
    }

    // Resources
    public static List<Resource> loadResources() { return load(RESOURCE_FILE); }
    public static void saveResources(List<Resource> resources) { save(RESOURCE_FILE, resources); }

    // Requests
    public static List<Request> loadRequests() { return load(REQUEST_FILE); }
    public static void saveRequests(List<Request> requests) { save(REQUEST_FILE, requests); }

    // Alerts
    public static List<Alert> loadAlerts() { return load(ALERT_FILE); }
    public static void saveAlerts(List<Alert> alerts) { save(ALERT_FILE, alerts); }

    // --- Helper methods for Requests and Resources ---

    // Find a request by id
    public static Request getRequestById(int id) {
        List<Request> requests = loadRequests();
        for (Request r : requests) {
            if (r.getId() == id) return r;
        }
        return null;
    }

    // Update an existing request (or add if not present)
    public static void updateRequest(Request updated) {
        List<Request> requests = loadRequests();
        for (int i = 0; i < requests.size(); i++) {
            if (requests.get(i).getId() == updated.getId()) {
                requests.set(i, updated);
                saveRequests(requests);
                return;
            }
        }
        requests.add(updated);
        saveRequests(requests);
    }

    // Find a resource by id
    public static Resource getResourceById(int id) {
        List<Resource> resources = loadResources();
        for (Resource r : resources) {
            if (r.getId() == id) return r;
        }
        return null;
    }

    // Update an existing resource (or add if not present)
    public static void updateResource(Resource updated) {
        List<Resource> resources = loadResources();
        for (int i = 0; i < resources.size(); i++) {
            if (resources.get(i).getId() == updated.getId()) {
                resources.set(i, updated);
                saveResources(resources);
                return;
            }
        }
        resources.add(updated);
        saveResources(resources);
    }
}
