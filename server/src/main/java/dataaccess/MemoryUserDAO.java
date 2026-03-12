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
        String hashedPassword = org.mindrot.jbcrypt.BCrypt.hashpw(user.password(), org.mindrot.jbcrypt.BCrypt.gensalt());
        users.put(user.username(), new UserData(user.username(), hashedPassword, user.email()));
    }

    public UserData getUser(String username) throws DataAccessException {
        return users.get(username);
    }
}