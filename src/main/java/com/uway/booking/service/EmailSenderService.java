package com.uway.booking.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

@Service
public class EmailSenderService {
	@Autowired
	private JavaMailSender emailSender;
	@Autowired
	private SpringTemplateEngine templateEngine;

	public void sendEmail(String to , String subject,String name) throws MessagingException, IOException {
		MimeMessage message = emailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
				StandardCharsets.UTF_8.name());
		helper.addAttachment("cta-bg.jpg", new ClassPathResource("cta-bg.jpg"));
		Context context = new Context();
		
		Map<String, Object> model = new HashMap<String, Object>();
        model.put("name", name);
        
		context.setVariables(model);

		String html = templateEngine.process("registration-email.html", context);
		helper.setTo(to);
		helper.setText(html, true);
		helper.setSubject(subject);
		helper.setFrom("noreply@uway.com");
		System.out.println("sending mail");
		emailSender.send(message);
	}
}