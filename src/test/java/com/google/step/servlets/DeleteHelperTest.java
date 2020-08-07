package com.google.step.servlets;

import static com.google.step.TestConstants.BLOBKEY_A;
import static com.google.step.TestConstants.DEAL_A;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

import com.google.step.datamanager.CommentManager;
import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.DealTagManager;
import com.google.step.datamanager.FollowManager;
import com.google.step.datamanager.RestaurantManager;
import com.google.step.datamanager.RestaurantPlaceManager;
import java.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ImageUploader.class)
public class DeleteHelperTest {
  private DealManager dealManager;
  private RestaurantManager restaurantManager;
  private DealTagManager dealTagManager;
  private RestaurantPlaceManager restaurantPlaceManager;
  private CommentManager commentManager;
  private FollowManager followManager;

  private DeleteHelper deleteHelper;

  @Before
  public void setUp() {
    dealManager = mock(DealManager.class);
    restaurantManager = mock(RestaurantManager.class);
    dealTagManager = mock(DealTagManager.class);
    restaurantPlaceManager = mock(RestaurantPlaceManager.class);
    commentManager = mock(CommentManager.class);
    followManager = mock(FollowManager.class);
    mockStatic(ImageUploader.class);
    deleteHelper =
        new DeleteHelper(
            dealManager,
            restaurantManager,
            dealTagManager,
            commentManager,
            restaurantPlaceManager,
            followManager);
  }

  @Test
  public void testDeleteDeal() {
    when(dealManager.readDeal(DEAL_ID_A)).thenReturn(DEAL_A);

    deleteHelper.deleteDeal(DEAL_ID_A);

    verify(dealManager).deleteDeal(DEAL_ID_A);
    verify(dealTagManager).deleteAllTagsOfDeal(DEAL_ID_A);
    verify(commentManager).deleteAllCommentsOfDeal(DEAL_ID_A);
    verifyStatic(ImageUploader.class);
    ImageUploader.deleteImage(BLOBKEY_A);
  }

  @Test
  public void testDeleteRestaurant() {
    when(dealManager.getDealsOfRestaurant(RESTAURANT_ID_A)).thenReturn(Arrays.asList(DEAL_A));
    when(dealManager.readDeal(DEAL_ID_A)).thenReturn(DEAL_A);

    deleteHelper.deleteRestaurant(RESTAURANT_ID_A);

    verify(restaurantManager).deleteRestaurant(RESTAURANT_ID_A);
    verify(restaurantPlaceManager).deletePlacesOfRestaurant(RESTAURANT_ID_A);
    verify(followManager).deleteFollowersOfRestaurant(RESTAURANT_ID_A);
    verify(dealManager).deleteDeal(DEAL_ID_A);
    verify(dealTagManager).deleteAllTagsOfDeal(DEAL_ID_A);
    verify(commentManager).deleteAllCommentsOfDeal(DEAL_ID_A);
  }
}
