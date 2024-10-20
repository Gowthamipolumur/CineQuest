package com.jts.movie.services;

import com.jts.movie.entities.User;
import com.jts.movie.entities.PaymentCard;
import com.jts.movie.repositories.UserRepository;
import com.jts.movie.repositories.PaymentCardRepository;
import com.jts.movie.request.UserRequest;
import com.jts.movie.request.PaymentCardRequest;
import com.jts.movie.request.ChangePasswordRequest;
import com.jts.movie.request.EditProfileRequest;
import com.jts.movie.response.UserResponse;
import com.jts.movie.config.JWTService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PaymentCardRepository paymentCardRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private JavaMailSender mailSender;

	@Autowired
	private JWTService jwtService;

	// Register user and send confirmation email
	public String addUser(UserRequest userRequest) throws MessagingException {
		// Check if the user already exists
		if (userRepository.findByEmailId(userRequest.getEmailId()).isPresent()) {
			throw new IllegalArgumentException("User already exists with email: " + userRequest.getEmailId());
		}

		// Encrypt password and save the user
		String encryptedPassword = passwordEncoder.encode(userRequest.getPassword());
		User user = User.builder()
				.firstName(userRequest.getFirstName())
				.lastName(userRequest.getLastName())
				.emailId(userRequest.getEmailId())
				.mobileNo(userRequest.getMobileNo())
				.password(encryptedPassword)
				.address(userRequest.getAddress())
				.city(userRequest.getCity())
				.state(userRequest.getState())
				.zipcode(userRequest.getZipcode())
				.roles(userRequest.getRoles())
				.age(userRequest.getAge())
				.isActive(false) // Initially inactive
				.promotionPreference(userRequest.isPromotionPreference())
				.build();

		// Generate and set the confirmation token
		String token = UUID.randomUUID().toString();
		user.setConfirmationToken(token);

		// Save the user
		userRepository.save(user);

		// Handle payment cards if provided
		if (userRequest.getPaymentCards() != null && !userRequest.getPaymentCards().isEmpty()) {
			if (userRequest.getPaymentCards().size() > 3) {
				throw new IllegalArgumentException("You can add a maximum of 3 payment cards.");
			}

			// Loop through each payment card and save it
			for (PaymentCardRequest cardRequest : userRequest.getPaymentCards()) {
				PaymentCard paymentCard = new PaymentCard();
				paymentCard.setCardNumber(cardRequest.getCardNumber());
				paymentCard.setCardHolderName(cardRequest.getCardHolderName());
				paymentCard.setExpiryDate(cardRequest.getExpiryDate());
				paymentCard.setCvv(cardRequest.getCvv());
				paymentCard.setUser(user);  // Associate the card with the user

				// Save payment card to repository (you need to inject the PaymentCardRepository)
				paymentCardRepository.save(paymentCard);
			}
		}

		// Send confirmation email
		sendConfirmationEmail(user, token);

		return "User registered successfully. Please check your email for confirmation.";
	}

	// Send confirmation email
	private void sendConfirmationEmail(User user, String token) throws MessagingException {
		String confirmationLink = "http://localhost:8080/user/confirmRegistration?token=" + token;
		String subject = "Confirm Your Registration";
		String body = "<p>Hello " + user.getFirstName() + " " + user.getLastName() + ",</p>"
				+ "<p>Thank you for registering. Please click the link below to confirm your registration:</p>"
				+ "<a href=\"" + confirmationLink + "\">Confirm Registration</a>"
				+ "<p>If you did not register, please ignore this email.</p>";

		sendEmail(user.getEmailId(), subject, body);
	}

	// Email sending logic
	private void sendEmail(String to, String subject, String body) throws MessagingException {
		MimeMessage mimeMessage = mailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(body, true);
		mailSender.send(mimeMessage);
		log.info("Confirmation email sent to: {}", to);
	}

	// Confirm registration
	public void confirmRegistration(String token) {
		User user = userRepository.findByConfirmationToken(token)
				.orElseThrow(() -> new IllegalArgumentException("Invalid token: " + token));

		user.setIsActive(true);
		user.setConfirmationToken(null);
		userRepository.save(user);
		log.info("User {} confirmed their registration.", user.getEmailId());
	}

	// Login user
	public UserResponse loginUser(UserRequest userRequest) {
		Optional<User> userOptional = userRepository.findByEmailId(userRequest.getEmailId());
		if (userOptional.isEmpty()) {
			throw new IllegalArgumentException("Invalid email or password");
		}

		User user = userOptional.get();

		if (!passwordEncoder.matches(userRequest.getPassword(), user.getPassword())) {
			throw new IllegalArgumentException("Invalid email or password");
		}

		// Check if the user is active (has confirmed registration)
		if (!user.getIsActive()) {
			throw new IllegalArgumentException("User has not confirmed their email");
		}

		// Generate JWT token
		String token = jwtService.generateToken(user.getEmailId());

		return new UserResponse(user.getEmailId(), token, "Login successful");
	}

	public void updateUserProfile(String currentUserEmail, EditProfileRequest editProfileRequest) {
		// Find the user by the current logged-in email
		Optional<User> userOptional = userRepository.findByEmailId(currentUserEmail);

		if (userOptional.isEmpty()) {
			throw new IllegalArgumentException("User not found.");
		}

		User user = userOptional.get();

		// Update user profile information from the request
		user.setFirstName(editProfileRequest.getFirstName());
		user.setLastName(editProfileRequest.getLastName());
		user.setMobileNo(editProfileRequest.getMobileNo());
		user.setAddress(editProfileRequest.getAddress());
		user.setCity(editProfileRequest.getCity());
		user.setState(editProfileRequest.getState());
		user.setZipcode(editProfileRequest.getZipcode());

		// Save the updated user profile
		userRepository.save(user);
	}

	public void changePassword(String currentUserEmail, ChangePasswordRequest changePasswordRequest) {
		// Find the user by the current logged-in email
		Optional<User> userOptional = userRepository.findByEmailId(currentUserEmail);

		if (userOptional.isEmpty()) {
			throw new IllegalArgumentException("User not found.");
		}

		User user = userOptional.get();

		// Check if the current password provided matches the user's existing password
		if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
			throw new IllegalArgumentException("Current password is incorrect.");
		}

		// Ensure new password and confirm password match
		if (!changePasswordRequest.getNewPassword().equals(changePasswordRequest.getConfirmPassword())) {
			throw new IllegalArgumentException("New password and confirm password do not match.");
		}

		// Encrypt the new password and update it
		String encodedPassword = passwordEncoder.encode(changePasswordRequest.getNewPassword());
		user.setPassword(encodedPassword);

		// Save the updated user with the new password
		userRepository.save(user);
	}

}
