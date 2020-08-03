package com.google.step.servlets;

import static com.google.step.TestConstants.DEAL_A;
import static com.google.step.TestConstants.DEAL_ID_A;
import static com.google.step.TestConstants.RESTAURANT_ID_A;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.step.datamanager.DealManager;
import com.google.step.datamanager.RestaurantManager;
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

  private DeleteHelper deleteHelper;

  @Before
  public void setUp() {
    dealManager = mock(DealManager.class);
    restaurantManager = mock(RestaurantManager.class);
    deleteHelper = new DeleteHelper(dealManager, restaurantManager);
  }

  @Test
  public void testDeleteDeal() {
    deleteHelper.deleteDeal(DEAL_ID_A);

    verify(dealManager).deleteDeal(DEAL_ID_A);
  }

  @Test
  public void testDeleteRestaurant() {
    when(dealManager.getDealsOfRestaurant(RESTAURANT_ID_A)).thenReturn(Arrays.asList(DEAL_A));
    deleteHelper.deleteRestaurant(RESTAURANT_ID_A);
    verify(restaurantManager).deleteRestaurant(RESTAURANT_ID_A);
    verify(dealManager).deleteDeal(DEAL_A.id);
  }
}
