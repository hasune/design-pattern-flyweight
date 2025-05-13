package com.designpattern.flyweight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/documents")
public class DocumentController {
    
    @Autowired
    private DocumentService documentService;
    
    @GetMapping
    public String listDocuments(Model model) {
        List<Document> documents = documentService.getAllDocuments();
        model.addAttribute("documents", documents);
        model.addAttribute("cacheSize", documentService.getCharacterCacheSize());
        return "documents/list";
    }
    
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("document", new Document());
        return "documents/form";
    }
    
    @PostMapping
    public String saveDocument(@ModelAttribute Document document) {
        documentService.saveDocument(document);
        return "redirect:/documents";
    }
    
    @GetMapping("/{id}/render")
    public String renderDocument(@PathVariable Long id, Model model) {
        Document document = documentService.getDocumentById(id);
        List<String> renderedCharacters = documentService.renderDocument(id);
        
        model.addAttribute("document", document);
        model.addAttribute("renderedCharacters", renderedCharacters);
        model.addAttribute("cacheSize", documentService.getCharacterCacheSize());
        return "documents/render";
    }
    
    @PostMapping("/clear-cache")
    public String clearCache() {
        documentService.clearCharacterCache();
        return "redirect:/documents";
    }
}
