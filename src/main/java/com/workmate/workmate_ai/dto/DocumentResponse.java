package com.workmate.workmate_ai.dto;

public class DocumentResponse {

    private Long id;
    private String title;
    private String fileName;
    private String fileType;
    private String filePath;

    public DocumentResponse() {
    }

    public DocumentResponse(
            Long id,
            String title,
            String fileName,
            String fileType,
            String filePath) {

        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.fileType = fileType;
        this.filePath = filePath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}