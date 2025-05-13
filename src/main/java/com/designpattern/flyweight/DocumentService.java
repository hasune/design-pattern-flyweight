package com.designpattern.flyweight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class DocumentService {
    
    @Autowired
    private DocumentRepository documentRepository;
    
    @Autowired
    private CharacterFactory characterFactory;
    
    public List<Document> getAllDocuments() {
        return documentRepository.findAll();
    }
    
    public Document saveDocument(Document document) {
        return documentRepository.save(document);
    }
    
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id).orElse(null);
    }
    
    /**
     * 플라이웨이트 패턴을 사용하여 문서의 문자들을 렌더링
     */
    public List<String> renderDocument(Long documentId) {
        Document document = getDocumentById(documentId);
        if (document == null) {
            return new ArrayList<>();
        }
        
        List<String> renderedCharacters = new ArrayList<>();
        String content = document.getContent();
        
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            
            // 플라이웨이트 패턴: 동일한 문자는 재사용
            CharacterFlyweight characterFlyweight = characterFactory.getCharacter(c);
            
            // Extrinsic State: 위치와 스타일 정보
            CharacterStyle style = new CharacterStyle(
                i * 12,  // x position
                50,      // y position
                "#000000", // color
                14,        // fontSize
                "Arial"    // fontFamily
            );
            
            characterFlyweight.display(style);
            renderedCharacters.add(characterFlyweight.getCharacter() + " at (" + style.getX() + ", " + style.getY() + ")");
        }
        
        log.info("Document rendered. Character cache size: {}", characterFactory.getCacheSize());
        return renderedCharacters;
    }
    
    public void clearCharacterCache() {
        characterFactory.clearCache();
    }
    
    public int getCharacterCacheSize() {
        return characterFactory.getCacheSize();
    }
}
