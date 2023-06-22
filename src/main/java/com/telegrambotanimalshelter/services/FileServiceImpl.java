package com.telegrambotanimalshelter.services;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.telegrambotanimalshelter.exceptions.UploadFileException;
import com.telegrambotanimalshelter.models.images.AppDocument;
import com.telegrambotanimalshelter.models.images.BinaryContent;
import com.telegrambotanimalshelter.repositories.images.BinaryContentRepository;
import com.telegrambotanimalshelter.repositories.images.DocumentRepository;
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
public class FileServiceImpl implements FileService {
    private final BinaryContentRepository binaryContentRepository;
    private final DocumentRepository documentRepository;
    @Value("${bot.token}")
    private String botToken;

    @Value("${service.file_info.uri}")
    String fileInfo;

    @Value("${service.file_storage.uri}")
    String filePath;

    public FileServiceImpl(BinaryContentRepository binaryContentRepository, DocumentRepository documentRepository) {
        this.binaryContentRepository = binaryContentRepository;
        this.documentRepository = documentRepository;
    }

    @Override
    public AppDocument processDoc(Message message) {
        PhotoSize photoSize = Arrays.stream(message.photo()).toList().get(2);
        if (photoSize != null) {
            String fileId = photoSize.fileId();
            ResponseEntity<String> response = getFilePath(fileId);
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonObject = new JSONObject(response.getBody());
                String filePath = String.valueOf(jsonObject.getJSONObject("result").getString("file_path"));
                byte[] fileInByte = downloadFile(filePath);
                BinaryContent transientBinaryContent = BinaryContent.builder().fileAsArrayOfBytes(fileInByte).build();
                BinaryContent persistentBinaryContent = binaryContentRepository.save(transientBinaryContent);
                AppDocument transientAppDocument = buildTransientDocument(photoSize, persistentBinaryContent);
                return documentRepository.save(transientAppDocument);
            }
        } else {
            throw new UploadFileException("Ошибка ответа от телеграма");
        }
        return null;
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);
        return restTemplate.exchange(fileInfo, HttpMethod.GET, request, String.class, botToken, fileId);
    }

    private AppDocument buildTransientDocument(PhotoSize telegramDocument, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(telegramDocument.fileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramDocument.fileSize())
                .build();
    }

    private byte[] downloadFile(String filePath) {
        System.out.println(filePath);
        String fullUri = this.filePath.replace("{bot.token}", this.botToken)
                .replace("{filePath}", filePath);
        System.out.println(fullUri);
        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
            System.out.println(urlObj);
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
