package easv.dk.eventticketsystem.bll.util;

import easv.dk.eventticketsystem.be.Users;

import java.util.ArrayList;
import java.util.List;

public class UserSearcher {
    public List<Users> searchUsers(List<Users> searchBase, String query) {
        List<Users> searchResult = new ArrayList<>();
        for (Users users : searchBase) {
            if (compareToUserName(query, users) || compareToUserRole(query, users))
            {
                searchResult.add(users);
            }
            else {
                System.out.println("No match found : " + users);
            }
        }
        return searchResult;
    }

    private boolean compareToUserName(String query, Users users) {
        return users.getUserName().toLowerCase().contains(query.toLowerCase());
    }

    private boolean compareToUserRole(String query, Users users) {
        return users.getRole().toLowerCase().contains(query.toLowerCase());
    }

}
