package vn.ifa.study.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.ifa.study.repository.KVRepository;

@RestController
@RequestMapping("/api")
public class Api {

    @Autowired
    private KVRepository<String, Object> repository;

    @DeleteMapping(value = "/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> delete(@PathVariable("key") final String key) {

        return repository.delete(key) ? ResponseEntity.noContent()
                .build()
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build();
    }

    @GetMapping(value = "/{key}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> find(@PathVariable("key") final String key) {

        return ResponseEntity.of(repository.find(key));
    }

    @PostMapping(value = "/{key}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> save(@PathVariable("key") final String key, @RequestBody final Object value) {

        return repository.save(key, value) ? ResponseEntity.ok(value)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build();
    }
}