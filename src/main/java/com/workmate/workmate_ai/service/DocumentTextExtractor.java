package com.workmate.workmate_ai.service;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;

import java.io.InputStream;

@Service
public class DocumentTextExtractor {

    public String extractText(InputStream inputStream) throws Exception {

        AutoDetectParser parser = new AutoDetectParser();

        ContentHandler handler = new BodyContentHandler(-1);
        Metadata metadata = new Metadata();

        parser.parse(
                inputStream,
                handler,
                metadata
        );

        return handler.toString();
    }
}