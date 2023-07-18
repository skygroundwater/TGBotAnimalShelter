package com.telegrambotanimalshelter.services;

import com.pengrad.telegrambot.model.Document;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.telegrambotanimalshelter.exceptions.UploadFileException;
import com.telegrambotanimalshelter.models.images.AppImage;
import com.telegrambotanimalshelter.models.images.CatImage;
import com.telegrambotanimalshelter.models.images.DogImage;
import com.telegrambotanimalshelter.models.reports.CatReport;
import com.telegrambotanimalshelter.models.reports.DogReport;
import com.telegrambotanimalshelter.repositories.images.CatImagesRepository;
import com.telegrambotanimalshelter.repositories.images.DogImagesRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Service
public class FileServiceImpl<I extends AppImage> implements FileService<I> {

    private final CatImagesRepository catImagesRepository;

    private final DogImagesRepository dogImagesRepository;


    @Value("${bot.token}")
    private String botToken;

    @Value("${service.file_info.uri}")
    String fileInfo;

    @Value("${service.file_storage.uri}")
    String filePath;

    public FileServiceImpl(CatImagesRepository catImagesRepository, DogImagesRepository dogImagesRepository) {
        this.catImagesRepository = catImagesRepository;
        this.dogImagesRepository = dogImagesRepository;
    }


    @Override
    public AppImage processDoc(AppImage image, Message message) {
        PhotoSize photoSize;
        Document document = message.document();
        if(document != null){
            photoSize = document.thumb();
        }else {
            photoSize = Arrays.stream(message.photo()).toList().get(2);
        }
        if (photoSize != null) {
            String fileId = photoSize.fileId();
            ResponseEntity<String> response = getFilePath(fileId);
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonObject = new JSONObject(response.getBody());
                String filePath = String.valueOf(jsonObject.getJSONObject("result").getString("file_path"));
                byte[] fileInByte = downloadFile(filePath);
                image.setFileSize(photoSize.fileSize());
                image.setTelegramFileId(photoSize.fileId());
                image.setFileAsArrayOfBytes(fileInByte);
                return image;
            }
        } else {
            throw new UploadFileException("Ошибка ответа от телеграмма");
        }
        return null;
    }

    @Override
    public List<DogImage> getAllDogImages() {
        return dogImagesRepository.findAll();
    }

    @Override
    public List<CatImage> getAllCatImages() {
        return catImagesRepository.findAll();
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(fileInfo, HttpMethod.GET, request, String.class, botToken, fileId);
    }

    private byte[] downloadFile(String filePath) {
        String fullUri = this.filePath.replace("{bot.token}", this.botToken)
                .replace("{filePath}", filePath);
        URL urlObj;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new UploadFileException(e.getMessage());
        }
        try (InputStream inputStream = urlObj.openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new UploadFileException(urlObj.toExternalForm());
        }
    }
}
