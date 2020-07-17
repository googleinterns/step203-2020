/**
 * Generates a list of restaurants.
 */
function getRestaurants() {
  const singapore = new google.maps.LatLng(1.352, 103.8198);
  const map = new google.maps.Map(document.getElementById('map'), {
    center: singapore,
    zoom: 15,
  });

  const service = new google.maps.places.PlacesService(map);

  const request = {
    location: singapore,
    radius: '13000',
    type: ['restaurant'],
  };
  service.nearbySearch(request, callback);
}

/**
 * Handles place search results.
 * @param {*} results place search results.
 * @param {*} status place search status.
 */
function callback(results, status) {
  if (status == google.maps.places.PlacesServiceStatus.OK) {
    restaurants = results.map((restaurant) => {
      return {
        placeId: restaurant.place_id,
        photo: restaurant.photos[0],
        name: restaurant.name,
        geometry: restaurant.geometry,
      };
    });
    console.log(JSON.stringify(restaurants));
  }
}

addLoadEvent(getRestaurants);

// eslint-disable-next-line no-unused-vars
const restaurantResults = [
  {
    'placeId': 'ChIJfyNkh6YZ2jERjHsd_P-iL_M',
    'photo': {
      'height': 2815,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/100178196521780490154">Hotel Swissôtel The Stamford</a>',
      ],
      'width': 3543,
    },
    'name': 'Hotel Swissôtel The Stamford',
    'geometry': {
      'location': {
        'lat': 1.2933597,
        'lng': 103.8531036,
      },
      'viewport': {
        'south': 1.291946819708498,
        'west': 103.8521475197085,
        'north': 1.294644780291502,
        'east': 103.8548454802915,
      },
    },
  },
  {
    'placeId': 'ChIJW-HHrWwZ2jERC16smRC_0NQ',
    'photo': {
      'height': 2528,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/104787677084564953190">Carlton City Hotel Singapore</a>',
      ],
      'width': 4500,
    },
    'name': 'Carlton City Hotel Singapore',
    'geometry': {
      'location': {
        'lat': 1.2760444,
        'lng': 103.8437551,
      },
      'viewport': {
        'south': 1.274630219708498,
        'west': 103.8422257697085,
        'north': 1.277328180291502,
        'east': 103.8449237302915,
      },
    },
  },
  {
    'placeId': 'ChIJRe0Wq5cZ2jER5qoN9Q0znq8',
    'photo': {
      'height': 1849,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/110192968845016030183">Concorde Hotel Singapore</a>',
      ],
      'width': 2882,
    },
    'name': 'Concorde Hotel Singapore',
    'geometry': {
      'location': {
        'lat': 1.3007654,
        'lng': 103.841536,
      },
      'viewport': {
        'south': 1.299631119708498,
        'west': 103.8405627697085,
        'north': 1.302329080291502,
        'east': 103.8432607302915,
      },
    },
  },
  {
    'placeId': 'ChIJjS4cuwoZ2jERCTeN-k9LUIE',
    'photo': {
      'height': 853,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/101990229713558816171">Hotel Swissôtel Merchant Court</a>',
      ],
      'width': 1279,
    },
    'name': 'Hotel Swissôtel Merchant Court',
    'geometry': {
      'location': {
        'lat': 1.2884308,
        'lng': 103.8458053,
      },
      'viewport': {
        'south': 1.287286519708498,
        'west': 103.8446658697085,
        'north': 1.289984480291502,
        'east': 103.8473638302915,
      },
    },
  },
  {
    'placeId': 'ChIJrXZUXmsZ2jERdZPWv7o3QHA',
    'photo': {
      'height': 1235,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/113446576631978744393">Amara Singapore</a>',
      ],
      'width': 1181,
    },
    'name': 'Amara Singapore',
    'geometry': {
      'location': {
        'lat': 1.2753354,
        'lng': 103.8435764,
      },
      'viewport': {
        'south': 1.274135319708498,
        'west': 103.8423018697085,
        'north': 1.276833280291502,
        'east': 103.8449998302915,
      },
    },
  },
  {
    'placeId': 'ChIJ-Uph06UZ2jERjKQsyQgfltg',
    'photo': {
      'height': 2362,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/113379668537940060151">Hotel Fairmont Singapore</a>',
      ],
      'width': 3544,
    },
    'name': 'Hotel Fairmont Singapore',
    'geometry': {
      'location': {
        'lat': 1.2940937,
        'lng': 103.853722,
      },
      'viewport': {
        'south': 1.292848869708498,
        'west': 103.8524375197085,
        'north': 1.295546830291502,
        'east': 103.8551354802915,
      },
    },
  },
  {
    'placeId': 'ChIJKWGbYo0Z2jER7di0vTo564Q',
    'photo': {
      'height': 3219,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/104526442015655408809">Mezza9</a>',
      ],
      'width': 4828,
    },
    'name': 'Mezza9',
    'geometry': {
      'location': {
        'lat': 1.306262,
        'lng': 103.8334,
      },
      'viewport': {
        'south': 1.305060769708498,
        'west': 103.8317170697085,
        'north': 1.307758730291502,
        'east': 103.8344150302915,
      },
    },
  },
  {
    'placeId': 'ChIJcRzD8XMZ2jER_xM9qjz0C3E',
    'photo': {
      'height': 1365,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/104656034851092900073">Hotel Re!</a>',
      ],
      'width': 2048,
    },
    'name': 'Hotel Re!',
    'geometry': {
      'location': {
        'lat': 1.2853812,
        'lng': 103.838417,
      },
      'viewport': {
        'south': 1.283954369708498,
        'west': 103.8371175197085,
        'north': 1.286652330291502,
        'east': 103.8398154802915,
      },
    },
  },
  {
    'placeId': 'ChIJxb_xL8YZ2jERVFvEcbhwuaM',
    'photo': {
      'height': 854,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/113880495727690146547">PARKROYAL on Kitchener Road</a>',
      ],
      'width': 1280,
    },
    'name': 'PARKROYAL on Kitchener Road',
    'geometry': {
      'location': {
        'lat': 1.310489,
        'lng': 103.8556366,
      },
      'viewport': {
        'south': 1.309204769708498,
        'west': 103.8543750197085,
        'north': 1.311902730291502,
        'east': 103.8570729802915,
      },
    },
  },
  {
    'placeId': 'ChIJH7yUdwoZ2jERili_YBPVHSk',
    'photo': {
      'height': 3854,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/102485854744254251106">ODOCO Agency Singapore</a>',
      ],
      'width': 5772,
    },
    'name': 'JUMBO Seafood - The Riverwalk',
    'geometry': {
      'location': {
        'lat': 1.2892875,
        'lng': 103.8482627,
      },
      'viewport': {
        'south': 1.287725269708498,
        'west': 103.8468155197085,
        'north': 1.290423230291502,
        'east': 103.8495134802915,
      },
    },
  },
  {
    'placeId': 'ChIJSZujEpEZ2jERc0ez6ins24Y',
    'photo': {
      'height': 3024,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/101897897812496256499">Sinjaya Salim</a>',
      ],
      'width': 4032,
    },
    'name': 'Wild Honey Mandarin Gallery',
    'geometry': {
      'location': {
        'lat': 1.3019538,
        'lng': 103.8368539,
      },
      'viewport': {
        'south': 1.300587619708498,
        'west': 103.8352969197085,
        'north': 1.303285580291502,
        'east': 103.8379948802915,
      },
    },
  },
  {
    'placeId': 'ChIJv1DkBMgZ2jERft-n2ibNvh0',
    'photo': {
      'height': 4312,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/115840775805887150461">Enos Nugraha</a>',
      ],
      'width': 5760,
    },
    'name': 'Swee Choon Tim Sum Restaurant',
    'geometry': {
      'location': {
        'lat': 1.3082025,
        'lng': 103.8569614,
      },
      'viewport': {
        'south': 1.306803419708498,
        'west': 103.8556675197085,
        'north': 1.309501380291502,
        'east': 103.8583654802915,
      },
    },
  },
  {
    'placeId': 'ChIJS5IVbvwa2jERDxPwmWkRghI',
    'photo': {
      'height': 1440,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/110655842346420247846">James Tan</a>',
      ],
      'width': 2560,
    },
    'name': 'McDonald\'s West Coast Park',
    'geometry': {
      'location': {
        'lat': 1.2975443,
        'lng': 103.7633584,
      },
      'viewport': {
        'south': 1.296259019708498,
        'west': 103.7620470197085,
        'north': 1.298956980291502,
        'east': 103.7647449802915,
      },
    },
  },
  {
    'placeId': 'ChIJZ9ocfqYZ2jER_PEzCkShP_s',
    'photo': {
      'height': 2362,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/115266945386573690115">SKAI</a>',
      ],
      'width': 3543,
    },
    'name': 'SKAI',
    'geometry': {
      'location': {
        'lat': 1.2931529,
        'lng': 103.8535406,
      },
      'viewport': {
        'south': 1.292028969708498,
        'west': 103.8524331697085,
        'north': 1.294726930291502,
        'east': 103.8551311302915,
      },
    },
  },
  {
    'placeId': 'ChIJWxVlyBIZ2jER0Wo8fPFxt88',
    'photo': {
      'height': 640,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/108247479126053096983">Food In Singapore</a>',
      ],
      'width': 960,
    },
    'name': 'Otto Ristorante',
    'geometry': {
      'location': {
        'lat': 1.2770184,
        'lng': 103.8463762,
      },
      'viewport': {
        'south': 1.275802919708498,
        'west': 103.8451781197085,
        'north': 1.278500880291502,
        'east': 103.8478760802915,
      },
    },
  },
  {
    'placeId': 'ChIJP1VVZSEa2jER7_4_d_LArq8',
    'photo': {
      'height': 2770,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/114552518139216645870">Jim Thompson</a>',
      ],
      'width': 4163,
    },
    'name': 'Jim Thompson',
    'geometry': {
      'location': {
        'lat': 1.305449,
        'lng': 103.815515,
      },
      'viewport': {
        'south': 1.304002969708498,
        'west': 103.8142958697085,
        'north': 1.306700930291502,
        'east': 103.8169938302915,
      },
    },
  },
  {
    'placeId': 'ChIJrYMeu5sQ2jERfpA5zK1UM9U',
    'photo': {
      'height': 3024,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/112684301108809381901">Alice Yang</a>',
      ],
      'width': 4032,
    },
    'name': 'Blu Kouzina',
    'geometry': {
      'location': {
        'lat': 1.3032472,
        'lng': 103.8103639,
      },
      'viewport': {
        'south': 1.301911169708498,
        'west': 103.8089342697085,
        'north': 1.304609130291502,
        'east': 103.8116322302915,
      },
    },
  },
  {
    'placeId': 'ChIJUw9ZUoUQ2jER2vah4NE87Io',
    'photo': {
      'height': 924,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/115999926585157457039">LINO</a>',
      ],
      'width': 1232,
    },
    'name': 'LINO',
    'geometry': {
      'location': {
        'lat': 1.3357543,
        'lng': 103.7866954,
      },
      'viewport': {
        'south': 1.334397969708498,
        'west': 103.7853754197085,
        'north': 1.337095930291502,
        'east': 103.7880733802915,
      },
    },
  },
  {
    'placeId': 'ChIJdxS7gPEa2jERBv5lgvo9Il8',
    'photo': {
      'height': 2736,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/118298782296293155581">Roy Dai</a>',
      ],
      'width': 3648,
    },
    'name': 'Subway',
    'geometry': {
      'location': {
        'lat': 1.2987473,
        'lng': 103.7749991,
      },
      'viewport': {
        'south': 1.297499619708498,
        'west': 103.7737259697085,
        'north': 1.300197580291502,
        'east': 103.7764239302915,
      },
    },
  },
  {
    'placeId': 'ChIJQ0U9IscZ2jERxoggGQSjBpA',
    'photo': {
      'height': 3024,
      'html_attributions': [
        '<a href="https://maps.google.com/maps/contrib/102799842666007918002">Subir Modak</a>',
      ],
      'width': 4032,
    },
    'name': 'Gayatri Restaurant',
    'geometry': {
      'location': {
        'lat': 1.3096712,
        'lng': 103.8519936,
      },
      'viewport': {
        'south': 1.308353319708498,
        'west': 103.8506036697085,
        'north': 1.311051280291502,
        'east': 103.8533016302915,
      },
    },
  },
];

