package faang.school.urlshortenerservice.controller;

import faang.school.urlshortenerservice.dto.UrlDto;
import faang.school.urlshortenerservice.service.UrlService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Slf4j
public class UrlController {
    private final UrlService urlService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String saveAndGetShortUrl(@RequestBody @Valid UrlDto urlDto){
        return urlService.saveAndGetShortUrl(urlDto);
    }

    @GetMapping
    public RedirectView getUrl(@RequestParam("shortUrl") String shortUrl){
        log.info("Retrieving URL for shortUrl: {}", shortUrl);
        var url = urlService.getUrl(shortUrl);
        return new RedirectView(url, true);
    }
}