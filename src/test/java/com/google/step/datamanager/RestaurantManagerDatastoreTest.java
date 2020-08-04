package com.google.step.datamanager;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.BLOBKEY_B;
import static com.google.step.TestConstants.BLOBKEY_URL_A;
import static com.google.step.TestConstants.BLOBKEY_URL_B;
import static com.google.step.TestConstants.RESTAURANT_ID_C;
import static com.google.step.TestConstants.RESTAURANT_NAME_A;
import static com.google.step.TestConstants.RESTAURANT_NAME_B;
import static com.google.step.TestConstants.RESTAURANT_NAME_C;
import static com.google.step.TestConstants.RESTAURANT_PHOTO_REFERENCE_A;
import static com.google.step.TestConstants.RESTAURANT_PHOTO_REFERENCE_URL_A;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.step.model.Restaurant;
import com.google.step.servlets.ImageUploader;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.BDDMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ImageUploader.class)
public final class RestaurantManagerDatastoreTest {

  private final LocalServiceTestHelper helper =
      new LocalServiceTestHelper(
          new LocalDatastoreServiceTestConfig().setApplyAllHighRepJobPolicy());

  private final RestaurantManagerDatastore restaurantManagerDatastore =
      new RestaurantManagerDatastore();

  @Before
  public void setUp() {
    helper.setUp();
  }

  @After
  public void tearDown() {
    helper.tearDown();
  }

  @Test
  public void testCreateRestaurantWithBlobKey_success() throws Exception {
    Restaurant restaurant =
        restaurantManagerDatastore.createRestaurantWithBlobKey(RESTAURANT_NAME_A, BLOBKEY_A);

    assertEquals(RESTAURANT_NAME_A, restaurant.name);
    assertEquals(BLOBKEY_URL_A, restaurant.photoUrl);
  }

  @Test
  public void testCreateRestaurantWithPhotoReference_success() throws Exception {
    Restaurant restaurant =
        restaurantManagerDatastore.createRestaurantWithPhotoReference(
            RESTAURANT_NAME_A, RESTAURANT_PHOTO_REFERENCE_A);

    assertEquals(RESTAURANT_NAME_A, restaurant.name);
    assertEquals(RESTAURANT_PHOTO_REFERENCE_URL_A, restaurant.photoUrl);
  }

  @Test
  public void testReadRestaurant_success() throws Exception {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurantWithBlobKey(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurantA_Test = restaurantManagerDatastore.readRestaurant(restaurantA.id);

    assertEquals(RESTAURANT_NAME_A, restaurantA_Test.name);
    assertEquals(BLOBKEY_URL_A, restaurantA_Test.photoUrl);
  }

  @Test
  public void testReadRestaurant_doesNotExist() throws Exception {
    Restaurant restaurant = restaurantManagerDatastore.readRestaurant(1000);

    assertNull(restaurant);
  }

  @Test
  public void testUpdateRestaurant_name() throws Exception {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurantWithBlobKey(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurantA_New =
        Restaurant.createRestaurantWithBlobkey(restaurantA.id, RESTAURANT_NAME_B, null);
    Restaurant restaurantA_Updated = restaurantManagerDatastore.updateRestaurant(restaurantA_New);

    assertEquals(RESTAURANT_NAME_B, restaurantA_Updated.name);
    assertEquals(BLOBKEY_URL_A, restaurantA_Updated.photoUrl);
  }

  @Test
  public void testUpdateRestaurant_blobKey() throws Exception {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurantWithBlobKey(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurantA_New =
        Restaurant.createRestaurantWithBlobkey(restaurantA.id, null, BLOBKEY_B);
    Restaurant restaurantA_Updated = restaurantManagerDatastore.updateRestaurant(restaurantA_New);

    assertEquals(RESTAURANT_NAME_A, restaurantA_Updated.name);
    assertEquals(BLOBKEY_URL_B, restaurantA_Updated.photoUrl);
  }

  @Test
  public void testDeleteRestaurant() throws Exception {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurantWithBlobKey(RESTAURANT_NAME_A, BLOBKEY_A);

    mockStatic(ImageUploader.class);
    BDDMockito.given(ImageUploader.getBlobKeyFromUrl(restaurantA.photoUrl)).willReturn(BLOBKEY_A);

    restaurantManagerDatastore.deleteRestaurant(restaurantA.id);
    verifyStatic(ImageUploader.class);
    ImageUploader.deleteImage(BLOBKEY_A);

    assertNull(restaurantManagerDatastore.readRestaurant(restaurantA.id));
  }

  @Test
  public void testReadRestaurants() {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurantWithBlobKey(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurantB =
        restaurantManagerDatastore.createRestaurantWithBlobKey(RESTAURANT_NAME_B, BLOBKEY_B);
    List<Long> ids = Arrays.asList(restaurantA.id, restaurantB.id);
    List<Restaurant> users = restaurantManagerDatastore.readRestaurants(ids);

    assertThat(users, containsInAnyOrder(restaurantA, restaurantB));
  }

  @Test
  public void testReadRestaurants_idDoesNotExist() {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurantWithBlobKey(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurantB =
        restaurantManagerDatastore.createRestaurantWithBlobKey(RESTAURANT_NAME_B, BLOBKEY_B);
    List<Long> ids = Arrays.asList(restaurantA.id, restaurantB.id, RESTAURANT_ID_C);
    List<Restaurant> users = restaurantManagerDatastore.readRestaurants(ids);

    assertThat(users, containsInAnyOrder(restaurantA, restaurantB));
  }

  public void testSearchRestaurant() {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurantWithBlobKey("abcde", BLOBKEY_A);
    Restaurant restaurantB =
        restaurantManagerDatastore.createRestaurantWithBlobKey("abxyz", BLOBKEY_A);
    Restaurant restaurantC =
        restaurantManagerDatastore.createRestaurantWithBlobKey("aqqq", BLOBKEY_A);

    List<Restaurant> restaurants = restaurantManagerDatastore.searchRestaurants("ab");

    assertEquals(2, restaurants.size());
    assertThat(restaurants, hasItems(restaurantA, restaurantB));
    assertThat(restaurants, not(hasItem(restaurantC)));
  }

  @Test
  public void testDeleteAllRestaurant() {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurantWithBlobKey(RESTAURANT_NAME_A, BLOBKEY_A);
    Restaurant restaurantB =
        restaurantManagerDatastore.createRestaurantWithBlobKey(RESTAURANT_NAME_B, BLOBKEY_B);
    Restaurant restaurantC =
        restaurantManagerDatastore.createRestaurantWithPhotoReference(
            RESTAURANT_NAME_C, RESTAURANT_PHOTO_REFERENCE_A);

    mockStatic(ImageUploader.class);
    BDDMockito.given(ImageUploader.getBlobKeyFromUrl(restaurantA.photoUrl)).willReturn(BLOBKEY_A);
    BDDMockito.given(ImageUploader.getBlobKeyFromUrl(restaurantB.photoUrl)).willReturn(BLOBKEY_B);
    BDDMockito.given(ImageUploader.getBlobKeyFromUrl(restaurantC.photoUrl)).willReturn(null);

    restaurantManagerDatastore.deleteAllRestaurants();

    verifyStatic(ImageUploader.class);
    ImageUploader.deleteImage(BLOBKEY_A, BLOBKEY_B);
    assertNull(restaurantManagerDatastore.readRestaurant(restaurantA.id));
    assertNull(restaurantManagerDatastore.readRestaurant(restaurantB.id));
    assertNull(restaurantManagerDatastore.readRestaurant(restaurantC.id));
  }

  @Test
  public void testSearchRestaurant_caseInsensitive() {
    Restaurant restaurantA =
        restaurantManagerDatastore.createRestaurantWithBlobKey("AbCdE", BLOBKEY_A);

    List<Restaurant> restaurants = restaurantManagerDatastore.searchRestaurants("abcde");

    assertEquals(1, restaurants.size());
    assertThat(restaurants, hasItem(restaurantA));
  }
}
