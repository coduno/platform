package uno.cod.storage.gcs;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;
import com.google.api.services.storage.model.Objects;
import com.google.api.services.storage.model.StorageObject;
import com.google.common.collect.Lists;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uno.cod.storage.PlatformStorage;

import java.io.*;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class GcsStorageDriver implements PlatformStorage {
    private static final Logger LOGGER = LoggerFactory.getLogger(GcsStorageDriver.class);

    private Storage storage;
    private PrivateKey signingKey;
    private String accountId;

    public GcsStorageDriver(String accountId, File p12key) throws GeneralSecurityException, IOException {
        GoogleCredential credential = new GoogleCredential.Builder().
                setTransport(new NetHttpTransport()).
                setJsonFactory(new JacksonFactory()).
                setServiceAccountId(accountId).
                setServiceAccountScopes(Lists.newArrayList(StorageScopes.DEVSTORAGE_FULL_CONTROL)).
                setServiceAccountPrivateKeyFromP12File(p12key).
                build();

        storage = new Storage.Builder(
                new NetHttpTransport(),
                new JacksonFactory(), credential).
                setApplicationName("platform").build();
        this.signingKey = loadKeyFromPkcs12(p12key);
        this.accountId = accountId;
    }

    @Override
    public List<String> listFiles(String bucket, String path) throws IOException {
        List<String> allItems = new LinkedList<>();
        Objects response = storage.objects().list(bucket).
                setPrefix(path).execute();

        Iterable<StorageObject> objs = response.getItems();
        if (objs == null) {
            return Collections.emptyList();
        }

        for (StorageObject obj : objs) {
            allItems.add(obj.getName());
        }

        while (response.getNextPageToken() != null) {
            String pageToken = response.getNextPageToken();
            response = storage.objects().list(bucket).
                    setPrefix(path).setPageToken(pageToken).execute();
            for (StorageObject obj : response.getItems()) {
                allItems.add(obj.getName());
            }
        }
        return allItems;
    }

    @Override
    public void upload(String bucket, String fileName, InputStream data, String contentType) throws IOException {
        InputStreamContent mediaContent = new InputStreamContent(contentType, data);
        Storage.Objects.Insert insertObject = storage
                .objects()
                .insert(bucket, null, mediaContent)
                .setName(fileName);
        // The media uploader gzips content by default, and alters the Content-Encoding accordingly.
        // GCS dutifully stores content as-uploaded. This line disables the media uploader behavior,
        // so the service stores exactly what is in the InputStream, without transformation.
        insertObject.getMediaHttpUploader().setDisableGZipContent(true);
        insertObject.execute();
    }

    public InputStream download(String bucketName, String objectName) throws IOException {
        Storage.Objects.Get getObject = storage.objects().get(bucketName, objectName);
        getObject.getMediaHttpDownloader().setDirectDownloadEnabled(true);
        return getObject.executeMediaAsInputStream();
    }

    public void downloadToOutputStream(String bucketName, String objectName, OutputStream data) throws IOException {
        Storage.Objects.Get getObject = storage.objects().get(bucketName, objectName);
        getObject.getMediaHttpDownloader().setDirectDownloadEnabled(true);
        getObject.executeMediaAndDownloadTo(data);
    }

    @Override
    public List<String> exposeFilesInFolder(String bucket, String folderName, Long expiration) throws GeneralSecurityException, IOException {
        if (!folderName.endsWith("/")) {
            folderName = folderName.concat("/");
        }
        List<String> files = listFiles(bucket, folderName);
        files.remove(folderName);

        if (files.isEmpty()) {
            LOGGER.debug("Was asked to expose files in folder {} (bucket {}) but there are no objects inside?", folderName, bucket);
            return Collections.emptyList();
        }

        List<String> exposedFiles = new ArrayList<>(files.size());
        for (String file : files) {
            exposedFiles.add(exposeFile(bucket, file, expiration));
        }
        return exposedFiles;
    }

    @Override
    public String exposeFile(String bucket, String fileName, Long expiration) throws GeneralSecurityException, UnsupportedEncodingException {
        String urlSignature = this.signString("GET\n\n\n" + expiration + "\n" + "/" + bucket + "/" + fileName);
        return "https://storage.googleapis.com/" + bucket + "/" + fileName +
                "?GoogleAccessId=" + accountId +
                "&Expires=" + expiration +
                "&Signature=" + URLEncoder.encode(urlSignature, "UTF-8");
    }

    private PrivateKey loadKeyFromPkcs12(File p12key) throws IOException, GeneralSecurityException {
        char[] password = "notasecret".toCharArray();
        FileInputStream fis = new FileInputStream(p12key);
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(fis, password);
        return (PrivateKey) ks.getKey("privatekey", password);
    }

    private String signString(String stringToSign) throws GeneralSecurityException, UnsupportedEncodingException {
        if (signingKey == null) {
            throw new IllegalArgumentException("Private Key not initalized");
        }
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(signingKey);
        signer.update(stringToSign.getBytes("UTF-8"));
        byte[] rawSignature = signer.sign();
        return new String(Base64.encodeBase64(rawSignature, false), "UTF-8");
    }
}
