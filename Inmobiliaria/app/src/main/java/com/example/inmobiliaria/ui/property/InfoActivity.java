package com.example.inmobiliaria.ui.property;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.bumptech.glide.Glide;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.responses.ContainerResponse;
import com.example.inmobiliaria.responses.PhotoResponse;
import com.example.inmobiliaria.responses.PhotoUploadResponse;
import com.example.inmobiliaria.responses.PropertyFavsResponse;
import com.example.inmobiliaria.responses.PropertyResponse;
import com.example.inmobiliaria.retrofit.generator.ServiceGenerator;
import com.example.inmobiliaria.retrofit.generator.autentication;
import com.example.inmobiliaria.retrofit.services.PhotoService;
import com.example.inmobiliaria.retrofit.services.PropertyService;
import com.example.inmobiliaria.util.UtilToken;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ImageView photo, imageViewLeftArrow, imageViewRightArrow;
    private Context ctx;
    private PropertyResponse property;
    private int count = 0;
    private MapView mapViewDetails;
    private PropertyService service;
    private GoogleMap gmap;
    private ImageButton deletePhoto;
    private FloatingActionButton addPhoto;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private TextView title, description, price, size, room, zipcode, address, category, city;
    public static final int READ_REQUEST_CODE = 42;
    Uri uriSelected;
    String jwt, idUser;
    private PhotoService servicePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        jwt = UtilToken.getToken(InfoActivity.this);
        idUser = UtilToken.getToken(getApplicationContext());
        System.out.println(idUser);
        //setSupportActionBar(toolbar);
        checkOwnerPhotos();
        //FloatingActionButton fab = findViewById(R.id.fab);
        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        Intent i = getIntent();
        property = (PropertyResponse) i.getSerializableExtra("property");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        loadItems();
        setItems();
        if (property.getPhotos().size() == 0) {
            imageViewLeftArrow.setImageDrawable(null);
            imageViewRightArrow.setImageDrawable(null);
        } else {
            imageViewRightArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InfoActivity.this.changePictureRight();
                }
            });
            imageViewLeftArrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    InfoActivity.this.changePictureLeft();
                }
            });
        }
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }
        mapViewDetails = findViewById(R.id.mapViewDetails);
        mapViewDetails.onCreate(mapViewBundle);
        mapViewDetails.getMapAsync(this);
        imageViewRightArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoActivity.this.changePictureRight();
            }
        });
        imageViewLeftArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoActivity.this.changePictureLeft();
            }
        });
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoActivity.this.performFileSearch();
            }
        });
        deletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoActivity.this.deletePhoto();
            }
        });
    }

    private void loadItems() {
        ctx = this;
        title = findViewById(R.id.details_title);
        description = findViewById(R.id.details_description);
        price = findViewById(R.id.price_details);
        size = findViewById(R.id.details_size);
        room = findViewById(R.id.rooms_details);
        address = findViewById(R.id.details_address);
        zipcode = findViewById(R.id.details_zipcode);
        category = findViewById(R.id.category_details);
        city = findViewById(R.id.details_city);
        mapViewDetails = findViewById(R.id.mapViewDetails);
        imageViewLeftArrow = findViewById(R.id.imageViewLeftArrow);
        imageViewRightArrow = findViewById(R.id.imageViewRightArrow);
        photo = findViewById(R.id.details_photo);
        addPhoto = findViewById(R.id.addPhotoDetails);
        deletePhoto = findViewById(R.id.deletePhoto);
        if (property.getPhotos().size() == 0) {
            Glide.with(this).load("https://rexdalehyundai.ca/dist/img/nophoto.jpg")
                    //.centerCrop()
                    .into(photo);
        } else {
            Glide.with(this).load(property
                    .getPhotos().get(0))
                    //.centerCrop()
                    .into(photo);
        }
        if (jwt == null) {
            addPhoto.setImageDrawable(null);
        }
    }

    public void deletePhoto() {
        servicePhoto = ServiceGenerator.createService(PhotoService.class);
        Call<ContainerResponse<PhotoResponse>> callList = servicePhoto.getAll();
        callList.enqueue(new Callback<ContainerResponse<PhotoResponse>>() {
            @Override
            public void onResponse(Call<ContainerResponse<PhotoResponse>> call, Response<ContainerResponse<PhotoResponse>> response) {
                if (response.isSuccessful()) {
                    for (PhotoResponse photo : response.body().getRows()) {
                        if (photo.getImgurlink().equals(property.getPhotos().get(count))) {
                            PhotoService servicePhotoDelete = ServiceGenerator.createService(PhotoService.class, jwt, autentication.JWT);
                            Call<PhotoResponse> callDelete = servicePhotoDelete.delete(photo.getId());
                            callDelete.enqueue(new Callback<PhotoResponse>() {
                                @Override
                                public void onResponse(Call<PhotoResponse> call, Response<PhotoResponse> response) {
                                    if (response.isSuccessful()) {
                                        Toast.makeText(InfoActivity.this, "Photo deleted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(InfoActivity.this, "no is.Successful DELETE", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<PhotoResponse> call, Throwable t) {
                                    Toast.makeText(InfoActivity.this, "Failure DELETE", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(InfoActivity.this, "no is.Successful GET", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContainerResponse<PhotoResponse>> call, Throwable t) {
                Toast.makeText(InfoActivity.this, "Failure GET", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                Log.i("Filechooser URI", "Uri: " + uri.toString());
            }
            uriSelected = uri;
        }
        uploadPhoto();
    }

    public void setItems() {
        title.setText(property.getTitle());
        description.setText(property.getDescription());
        price.setText(String.valueOf(property.getPrice()) + "€");
        address.setText(property.getAddress());
        size.setText(String.valueOf(property.getSize()) + "/m2");
        city.setText(property.getZipcode() + ", " + property.getCity() + ", " + property.getProvince());
        category.setText(property.getCategoryId().getName());
    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public void changePictureRight() {
        Glide
                .with(ctx)
                .load(property.getPhotos().get(count))
                .into(photo);
        count++;
        if (count >= property.getPhotos().size()) {
            count = 0;
        }
    }

    public void changePictureLeft() {
        Glide
                .with(ctx)
                .load(property.getPhotos().get(count))
                .into(photo);
        count--;
        if (count < 0) {
            count = property.getPhotos().size() - 1;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gmap = googleMap;
        gmap.setMinZoomPreference(10);
        String loc = property.getLoc();
        String[] locs = loc.split(",");
        locs[0].trim();
        locs[1].trim();
        float latitud = Float.parseFloat(locs[0]);
        float longitud = Float.parseFloat(locs[1]);
        LatLng position = new LatLng(latitud, longitud);
        googleMap.addMarker(new MarkerOptions()
                .position(position)
                .title(property.getAddress())
                .snippet("com.example.inmobiliaria")
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_location))
        );
        gmap.moveCamera(CameraUpdateFactory.newLatLng(position));
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }
        mapViewDetails.onSaveInstanceState(mapViewBundle);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapViewDetails.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapViewDetails.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapViewDetails.onStop();
    }

    @Override
    protected void onPause() {
        mapViewDetails.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mapViewDetails.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapViewDetails.onLowMemory();
    }

    public void uploadPhoto() {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uriSelected);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            int cantBytes;
            byte[] buffer = new byte[1024 * 4];
            while ((cantBytes = bufferedInputStream.read(buffer, 0, 1024 * 4)) != -1) {
                baos.write(buffer, 0, cantBytes);
            }
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(uriSelected)), baos.toByteArray());
            MultipartBody.Part body = MultipartBody.Part.createFormData("photo", "photo", requestFile);
            RequestBody propertyId = RequestBody.create(MultipartBody.FORM, property.getId());
            PhotoService servicePhoto = ServiceGenerator.createService(PhotoService.class, jwt, autentication.JWT);
            Call<PhotoUploadResponse> callPhoto = servicePhoto.upload(body, propertyId);
            callPhoto.enqueue(new Callback<PhotoUploadResponse>() {
                @Override
                public void onResponse(Call<PhotoUploadResponse> call, Response<PhotoUploadResponse> response) {
                    if (response.isSuccessful()) {
                        property.getPhotos().add(response.body().getId());
                        Log.d("Uploaded", "Éxito");
                        Log.d("Uploaded", response.body().toString());
                        System.out.println(response.code());
                    } else {
                        Log.e("Upload error", response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<PhotoUploadResponse> call, Throwable t) {
                    Log.e("Upload error", t.getMessage());
                }
            });
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkOwnerPhotos() {
        service = ServiceGenerator.createService(PropertyService.class, jwt, autentication.JWT);
        Call<ContainerResponse<PropertyFavsResponse>> callProp = service.getMine();
        callProp.enqueue(new Callback<ContainerResponse<PropertyFavsResponse>>() {
            @Override
            public void onResponse(Call<ContainerResponse<PropertyFavsResponse>> call, Response<ContainerResponse<PropertyFavsResponse>> response) {
                if (response.isSuccessful()) {
                    for (PropertyFavsResponse propertyMine : response.body().getRows()) {
                        System.out.println(propertyMine.getId());
                        System.out.println(property.getId());
                        if (propertyMine.getId().equals(property.getId())) {
                            Log.d("ok", "ok");
                        } else {
                            addPhoto.setImageDrawable(null);
                            deletePhoto.setImageDrawable(null);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ContainerResponse<PropertyFavsResponse>> call, Throwable t) {
            }
        });
    }
}
