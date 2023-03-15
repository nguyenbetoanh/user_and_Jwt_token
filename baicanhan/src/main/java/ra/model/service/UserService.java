package ra.model.service;

import ra.model.entity.Users;

import javax.servlet.Filter;
import java.util.List;

public interface UserService {
    Users findUsersByUserName(String userName);
    boolean existsByUserName(String userName);
    boolean existsByEmail(String email);
    Users saveOrUpdate(Users users);
    Users findById(int userId);
    List<Users> getAll();
}
