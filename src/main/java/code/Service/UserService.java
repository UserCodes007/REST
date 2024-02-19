package code.Service;

import java.util.List;
import java.util.Optional;

import code.Model.User;
import code.Exceptions.UserNotFoundException;
import code.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
	@Autowired private UserRepository repo;
	
	public User add(User user) {
		return repo.save(user);
	}
	
	public User update(User user) throws UserNotFoundException {
		if (!repo.existsById(user.getId())) {
			throw new UserNotFoundException();	
		}
		return repo.save(user);
	}
	
	public User get(Long id) throws UserNotFoundException {
		Optional<User> result = repo.findById(id);
		
		if (result.isPresent()) {
			return result.get();
		}
		
		else throw new UserNotFoundException();
	}
	
	public List<User> list() {
		return (List<User>) repo.findAll();
	}
	
	public void delete(Long id) throws UserNotFoundException {
		if (repo.existsById(id)) {
			repo.deleteById(id);
		}
		else throw new UserNotFoundException();
	}
}
