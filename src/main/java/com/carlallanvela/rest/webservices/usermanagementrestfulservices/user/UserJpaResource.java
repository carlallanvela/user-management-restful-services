package com.carlallanvela.rest.webservices.usermanagementrestfulservices.user;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import java.net.URI;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.carlallanvela.rest.webservices.usermanagementrestfulservices.PostRepository;
import com.carlallanvela.rest.webservices.usermanagementrestfulservices.UserRepository;

@CrossOrigin("http://localhost:8080")
@RestController
public class UserJpaResource {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PostRepository postRepository;

	@GetMapping(path="/jpa/users")
	public List<User> retrieveAllUsers() {
		return userRepository.findAll();
	}
	
	@GetMapping(path="/jpa/users/{id}")
	public Resource<User> retrieveUser(@PathVariable int id) {
		Optional<User> user = userRepository.findById(id);
		
		if (!user.isPresent()) {
			throw new UserNotFoundException("id: " + id);
		}

		// HATEOAS
		// enables us to add links using method		
		//"all-users", SERVER_PATH + "/users"
		Resource<User> resource = new Resource<User>(user.get());
		
		// Enable creates links from methods
		ControllerLinkBuilder linkTo = 
				linkTo(methodOn(this.getClass()).retrieveAllUsers());
		
		resource.add(linkTo.withRel("all-users"));
		
		return resource;
	}

	@PostMapping("/jpa/users")
	public ResponseEntity<Object> createUser(@Valid @RequestBody User user) {
		User savedUser = userRepository.save(user);

		// Return a status called CREATED
		// Set a URI of the created Resource to the Response
		URI location = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(savedUser.getId())
			.toUri();
		
		return ResponseEntity.created(location).build();
	}
	
	@DeleteMapping("/jpa/users/{id}")
	public void deleteUser(@PathVariable int id) {
		userRepository.deleteById(id);
	}
	
	@GetMapping(path="/jpa/users/{id}/posts")
	public List<Post> retrieveAllPosts(@PathVariable int id) {
		Optional<User> userOptional = userRepository.findById(id);
		if (!userOptional.isPresent()) {
			throw new UserNotFoundException("id-" + id);
			
		}
		return userOptional.get().getPosts();
	}
	
	@PostMapping("/jpa/users/{id}/posts")
	public ResponseEntity<Object> createPost(@PathVariable int id, @RequestBody Post post) {
		
		Optional<User> userOptional = userRepository.findById(id);
		
		if (!userOptional.isPresent()) {
			throw new UserNotFoundException("id-" + id);
		}

		User user = userOptional.get();
		post.setUser(user);
		postRepository.save(post);
		
		URI location = ServletUriComponentsBuilder
			.fromCurrentRequest()
			.path("/{id}")
			.buildAndExpand(post.getId())
			.toUri();
		
		return ResponseEntity.created(location).build();
	}
}
