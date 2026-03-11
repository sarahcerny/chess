package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> users = new HashMap<>();

    public void clear() {
        users.clear();
    }

    public void createUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
    }

    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }
}