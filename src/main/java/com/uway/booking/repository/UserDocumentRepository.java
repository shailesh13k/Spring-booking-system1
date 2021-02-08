package com.uway.booking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.uway.booking.model.UserDocument;

public interface UserDocumentRepository extends JpaRepository<UserDocument, Integer> {

	List<UserDocument> findAllByUserId(Long id);

}