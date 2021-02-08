package com.uway.booking.controller;

import java.util.Iterator;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.uway.booking.message.request.OrderRequest;
import com.uway.booking.message.response.OrderResponse;
import com.uway.booking.message.response.ResponseMessage;
import com.uway.booking.message.response.UploadFileResponse;
import com.uway.booking.model.User;
import com.uway.booking.model.UserDocument;
import com.uway.booking.model.UserOrder;
import com.uway.booking.repository.UserDocumentRepository;
import com.uway.booking.repository.UserOrderRepository;
import com.uway.booking.repository.UserRepository;
import com.uway.booking.uploadmultiple.service.DocumentStorageService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/details")
public class BookingController {


	@Value("${razorpay.key}")
	private String razorPayKey;
	
	@Value("${razorpay.name}")
	private String razorPayName;
	
	@Value("${razorpay.description}")
	private String razorPayDescription;

	@Autowired
	private DocumentStorageService documneStorageService;

	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserDocumentRepository userDocumentRepository;

	@Autowired
	UserOrderRepository userOrderRepository;
	
	
	@GetMapping("/user/me")
	public User getMyDetails() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();

		System.out.println("login success");

		User user = userRepository.findByEmail(username);
		System.out.println(user.getId());

		// user.setDocuments(userDocumentRepository.findAllByUserId(user.getId()));
		return user;
	}

	@PostMapping("/user/upload")
	public ResponseEntity<ResponseMessage> uploadFiles(@RequestParam("file") MultipartFile file) {

		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();
		if (username != null) {
			User user = userRepository.findByEmail(username);
			System.out.println("upload");
			String fileName = documneStorageService.storeFile(file, user);

			String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
					.path(fileName).toUriString();

			return ResponseEntity.status(HttpStatus.OK).body(new UploadFileResponse(fileName, fileDownloadUri,
					file.getContentType(), file.getSize(), "File Uploaded Successfully"));
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseMessage("Fail -> Bad request"));
	}

	@GetMapping("/user/files")
	public List<UserDocument> getUserFiles() {
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();

		System.out.println("get user files");

		User user = userRepository.findByEmail(username);
		System.out.println(user.getId());
		List<UserDocument> fileInfos = userDocumentRepository.findAllByUserId(user.getId());

		for (Iterator iterator = fileInfos.iterator(); iterator.hasNext();) {
			UserDocument userDocument = (UserDocument) iterator.next();
			String filename = userDocument.getFileName().toString();
			String url = MvcUriComponentsBuilder
					.fromMethodName(BookingController.class, "getFile", userDocument.getFileName().toString()).build()
					.toString();
			userDocument.setUrl(url);
		}

		return fileInfos;
	}

	@GetMapping("user/files/{filename:.+}")
	public ResponseEntity<Resource> getFile(@PathVariable String filename) {
		System.out.println("get File");
		Resource file = documneStorageService.load(filename);
		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
				.body(file);
	}

	@PostMapping("/user/order")
	public OrderResponse processOrder(@Valid @RequestBody OrderRequest orderRequest) {
		OrderResponse orderResponse = new OrderResponse();

		System.out.println("order " + orderRequest.getAmount());
		UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = userDetails.getUsername();

		Double amountDouble = Double.parseDouble(orderRequest.getAmount());
		User user = userRepository.findByEmail(username);

		UserOrder userOrder = new UserOrder();
		userOrder.setAmount(amountDouble);
		userOrder.setUserId(user.getId());
		userOrder = userOrderRepository.save(userOrder);

		orderResponse.setAmount(amountDouble * 100); // amount in the smallest currency unit
		orderResponse.setCurrency("INR");
		orderResponse.setReceipt(userOrder.getId());

		orderResponse.setKey(razorPayKey);
		orderResponse.setName(razorPayName);
		orderResponse.setDescription(razorPayDescription);

		return orderResponse;

	}
	
	
	@PostMapping("/user/capture/{paymentId}/{orderId}")
	public String capture(@PathVariable String paymentId, @PathVariable long orderId) {
		System.out.println(paymentId);
		System.out.println(orderId);
		
		UserOrder order = userOrderRepository.findById(orderId).get();
		order.setPaymentId(paymentId);
		
		userOrderRepository.save(order);
		
		return "order processed";
	}
}
