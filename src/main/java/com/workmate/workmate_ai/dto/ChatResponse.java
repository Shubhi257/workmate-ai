package com.workmate.workmate_ai.dto;

import java.util.List;

public class ChatResponse {

    private String answer;
    private List<Source> sources;

    public ChatResponse() {
    }

    public ChatResponse(String answer, List<Source> sources) {
        this.answer = answer;
        this.sources = sources;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public static class Source {

        private Long documentId;
        private String fileName;
        private Integer chunkIndex;

        public Source(Long documentId, String fileName, Integer chunkIndex) {
            this.documentId = documentId;
            this.fileName = fileName;
            this.chunkIndex = chunkIndex;
        }

        public Long getDocumentId() {
            return documentId;
        }

        public String getFileName() {
            return fileName;
        }

        public Integer getChunkIndex() {
            return chunkIndex;
        }
    }
}