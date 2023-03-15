package ra.model.serviceImp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ra.model.entity.Users;
import ra.model.repository.UserRepository;
import ra.model.service.UserService;

import javax.servlet.Filter;
import java.util.List;
@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public Users findUsersByUserName(String userName) {
        return userRepository.findUsersByUserName(userName);
    }

    @Override
    public boolean existsByUserName(String userName) {
        return userRepository.existsByUserName(userName);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public List<Users> getAllByFilter(List<Filter> listFilter) {
        return null;
    }

    @Override
    public Users saveOrUpdate(Users users) {
        return userRepository.save(users);
    }


    @Override
    public Users findById(int userId) {
        return userRepository.findById(userId).get();
    }

    @Override
    public List<Users> getAll() {
        return userRepository.findAll();
    }
}
