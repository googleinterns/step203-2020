package com.google.step.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

public class ImageUploader {

  private static final BlobstoreService blobstoreService =
      BlobstoreServiceFactory.getBlobstoreService();

  /**
   * Creates an upload URL for blobstore services
   *
   * @param targetUrl the URL to redirect the POST request
   * @return
   */
  public static String getUploadUrl(String targetUrl) {
    return blobstoreService.createUploadUrl(targetUrl);
  }

  /**
   * Returns a blobkey that points to the uploaded image file, or null if the user didn't upload an
   * image.
   */
  public static String getUploadedImageBlobkey(
      HttpServletRequest request, String formInputElementName) {
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a URL. (dev server)
    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    // Our form only contains a single file input, so get the first index.
    BlobKey blobKey = blobKeys.get(0);

    // User submitted form without selecting a file, so we can't get a URL. (live server)
    BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    if (blobInfo.getSize() == 0) {
      blobstoreService.delete(blobKey);
      return null;
    }

    // Check if uploaded file is an image
    InputStream stream;
    try {
      stream = new BlobstoreInputStream(blobKey);
    } catch (IOException e) {
      blobstoreService.delete(blobKey);
      return null;
    }
    if (!isImage(stream)) {
      blobstoreService.delete(blobKey);
      return null;
    }

    return blobKey.getKeyString();
  }

  private static boolean isImage(InputStream stream) {
    try {
      if (ImageIO.read(stream) == null) {
        return false;
      }
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  /**
   * Deletes the image file identified by the blobKey.
   *
   * @param blobKey blobKey of the image file.
   */
  public static void deleteImage(String blobKey) {
    try {
      blobstoreService.delete(new BlobKey(blobKey));
    } catch (Exception e) {
      return;
    }
  }
}
