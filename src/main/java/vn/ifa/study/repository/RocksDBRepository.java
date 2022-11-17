package vn.ifa.study.repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.springframework.util.SerializationUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Configuration
public class RocksDBRepository implements KVRepository<String, Object> {
    @Value("${rocksdb.dir}")
    private File baseDir;
    RocksDB db;

    @Override
    public synchronized boolean delete(final String key) {

        log.info("deleting key '{}'", key);

        try {
            db.delete(key.getBytes());
        }
        catch (final RocksDBException e) {
            log.error("Error deleting entry, cause: '{}', message: '{}'", e.getCause(), e.getMessage());

            return false;
        }

        return true;
    }

    @Override
    public synchronized Optional<Object> find(final String key) {

        Object value = null;

        try {
            final byte[] bytes = db.get(key.getBytes());

            if (bytes != null) {
                value = SerializationUtils.deserialize(bytes);
            }

        }
        catch (final RocksDBException e) {
            log.error("Error retrieving the entry with key: {}, cause: {}, message: {}",
                      key,
                      e.getCause(),
                      e.getMessage());
        }

        log.info("finding key '{}' returns '{}'", key, value);

        return value != null ? Optional.of(value) : Optional.empty();
    }

    @PostConstruct // execute after the application starts.
    void initialize() throws IOException, RocksDBException {

        RocksDB.loadLibrary();
        final Options options = new Options();
        options.setCreateIfMissing(true);

        Files.createDirectories(baseDir.getParentFile()
                .toPath());
        Files.createDirectories(baseDir.getAbsoluteFile()
                .toPath());
        db = RocksDB.open(options, baseDir.getAbsolutePath());
        log.info("RocksDB was initialized successfully");

    }

    @Override
    public synchronized boolean save(final String key, final Object value) {

        log.info("saving value '{}' with key '{}'", value, key);

        try {
            db.put(key.getBytes(), SerializationUtils.serialize(value));
        }
        catch (final RocksDBException e) {
            log.error("Error saving entry. Cause: '{}', message: '{}'", e.getCause(), e.getMessage());

            return false;
        }

        return true;
    }
}
