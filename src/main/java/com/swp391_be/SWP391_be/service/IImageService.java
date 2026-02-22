package com.swp391_be.SWP391_be.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface IImageService {
  Map<String, String> uploadImage(MultipartFile file) throws IOException;
  void deleteImage(String publicId) throws IOException;
}
