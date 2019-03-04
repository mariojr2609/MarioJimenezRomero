package com.example.inmobiliaria.ui.property;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.models.AddProperty;
import com.example.inmobiliaria.responses.CategoryResponse;
import com.example.inmobiliaria.responses.ContainerResponse;
import com.example.inmobiliaria.responses.PhotoUploadResponse;
import com.example.inmobiliaria.responses.UserResponse;
import com.example.inmobiliaria.retrofit.generator.ServiceGenerator;
import com.example.inmobiliaria.retrofit.generator.autentication;
import com.example.inmobiliaria.retrofit.services.CategoryService;
import com.example.inmobiliaria.retrofit.services.PhotoService;
import com.example.inmobiliaria.retrofit.services.PropertyService;
import com.example.inmobiliaria.retrofit.services.UserService;
import com.example.inmobiliaria.util.UtilToken;
import com.example.inmobiliaria.util.geography.GeoCode;
import com.example.inmobiliaria.util.geography.GeographyListener;
import com.example.inmobiliaria.util.geography.GeographySelectorFragment;
import com.example.inmobiliaria.util.geography.GeographySpain;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddPropertyActivity extends FragmentActivity implements View.OnClickListener {
    public static final int READ_REQUEST_CODE = 42;
    private EditText title, description, price, size, zipcode, address, rooms;
    private String fullAddress, jwt, loc;
    private TextView tvRegion;
    private TextView tvProvincia;
    private TextView tvMunicipio;
    PropertyService service;
    Uri uriSelected;
    UserResponse me;
    private Button btProbar, btnAdd;
    private Spinner categories;
    private List<CategoryResponse> listCategories = new ArrayList<>();
    private FloatingActionButton addPhoto;
    AddProperty property;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);
        jwt = UtilToken.getToken(this);
        me = getMe();
        uriSelected = null;
        addPhoto = findViewById(R.id.addPhoto);
        btnAdd = findViewById(R.id.add_property);
        btProbar = (Button) findViewById(R.id.btProbar);
        btProbar.setOnClickListener(this);
        title = findViewById(R.id.title_add);
        description = findViewById(R.id.description_add);
        price = findViewById(R.id.price_add);
        size = findViewById(R.id.size_add);
        address = findViewById(R.id.address_add);
        zipcode = findViewById(R.id.zipcode_add);
        rooms = findViewById(R.id.rooms_add);
        tvRegion = (TextView) findViewById(R.id.tvRegion);
        tvProvincia = (TextView) findViewById(R.id.tvProvincia);
        tvMunicipio = (TextView) findViewById(R.id.tvMunicipio);
        categories = findViewById(R.id.spinner_category);
        loadAllCategories();
        //addPhoto.setOnClickListener(v -> { performFileSearch(); });
        //btnAdd.setOnClickListener(v -> { makeProperty();
            /*new android.os.Handler().postDelayed(
                    () -> Log.i("tag", "This'll run 800 milliseconds later"),
                    800);*/
        //});
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btProbar) {
            GeographySelectorFragment gs = new GeographySelectorFragment(AddPropertyActivity.this);
            gs.setOnGeograpySelectedListener(AddPropertyActivity.this);
            FragmentManager fm = getSupportFragmentManager();
            gs.show(fm, "geographySelector");
        }
        if (v.getId() == R.id.add_property) {
        }
    }

    public void performFileSearch() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    public void makeProperty() {
        fullAddress = "Calle " + address.getText().toString() + ", " + zipcode.getText().toString() + " " + " " + tvProvincia.getText().toString() + ", España";
        try {
            loc = getLoc(fullAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        AddProperty create = new AddProperty();
        CategoryResponse chosen = (CategoryResponse) categories.getSelectedItem();
        create.setTitle(title.getText().toString());
        create.setRooms(Long.parseLong(rooms.getText().toString()));
        create.setDescription(description.getText().toString());
        create.setAddress(address.getText().toString());
        create.setZipcode(zipcode.getText().toString());
        create.setCity(tvMunicipio.getText().toString());
        create.setPrice(Long.parseLong(price.getText().toString()));
        create.setSize(Long.parseLong(size.getText().toString()));
        create.setProvince(tvProvincia.getText().toString());
        create.setOwnerId(me.get_id());
        create.setCategoryId(chosen.getId());
        create.setLoc(loc);
        addProperty(create);
    }

    public String getLoc(String fullAddress) throws IOException {
        String loc = GeoCode.getLatLong(AddPropertyActivity.this, fullAddress);
        return loc;
    }

    //@Override
    public void onGeographySelected(Map<String, String> hm) {
        tvRegion.setText(hm.get(GeographySpain.REGION));
        tvProvincia.setText(hm.get(GeographySpain.PROVINCIA));
        tvMunicipio.setText(hm.get(GeographySpain.MUNICIPIO));
    }

    public void loadAllCategories() {
        CategoryService serviceC = ServiceGenerator.createService(CategoryService.class);
        Call<ContainerResponse<CategoryResponse>> callC = serviceC.listCategories();
        callC.enqueue(new Callback<ContainerResponse<CategoryResponse>>() {
            private Call<ContainerResponse<CategoryResponse>> call;
            private Throwable t;
            @Override
            public void onResponse(Call<ContainerResponse<CategoryResponse>> call, Response<ContainerResponse<CategoryResponse>> response) {
                if (response.isSuccessful()) {
                    int spinnerPosition = 1;
                    Log.d("successCategory", "Got category");
                    listCategories = response.body().getRows();
                    System.out.println(listCategories);
                    List<String> namesC = new ArrayList<>();
                    for (CategoryResponse category : listCategories) {
                        namesC.add(category.getName());
                    }
                    ArrayAdapter<CategoryResponse> adapter = new ArrayAdapter<>(AddPropertyActivity.this, android.R.layout.simple_spinner_dropdown_item, listCategories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categories.setAdapter(adapter);
                    categories.setSelection(listCategories.size() - 1);
                } else {
                    Toast.makeText(AddPropertyActivity.this, "Error loading categories", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ContainerResponse<CategoryResponse>> call, Throwable t) {
                this.call = call;
                this.t = t;
                Toast.makeText(AddPropertyActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public UserResponse getMe() {
        final UserResponse me = new UserResponse();
        UserService serviceU = ServiceGenerator.createService(UserService.class, jwt, autentication.JWT);
        Call<UserResponse> callU = serviceU.getMe();
        callU.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful()) {
                    me.set_id(response.body().get_id());
                    me.setEmail(response.body().getEmail());
                    me.setName(response.body().getName());
                    me.setPicture(response.body().getPicture());
                    me.setPassword(response.body().getPassword());
                    Log.d("OK", "Got user");
                } else {
                    Log.e("Error", "Error getting user");
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Log.e("Failure", "Error getting user");
            }
        });
        return me;
    }

    public void addProperty(AddProperty create) {
        service = ServiceGenerator.createService(PropertyService.class, jwt, autentication.JWT);
        Call<AddProperty> call = service.create(create);
        call.enqueue(new Callback<AddProperty>() {
            @Override
            public void onResponse(Call<AddProperty> call, Response<AddProperty> response) {
                uploadPhoto(response.body());
                if (response.isSuccessful()) {
                    System.out.println(response.body());
                    Toast.makeText(AddPropertyActivity.this, "Created", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AddPropertyActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AddProperty> call, Throwable t) {
                Toast.makeText(AddPropertyActivity.this, "Failure", Toast.LENGTH_SHORT).show();
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
    }
    public void uploadPhoto(final AddProperty responseP) {
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
            RequestBody propertyId = RequestBody.create(MultipartBody.FORM, responseP.getId());
            PhotoService servicePhoto = ServiceGenerator.createService(PhotoService.class, jwt, autentication.JWT);
            Call<PhotoUploadResponse> callPhoto = servicePhoto.upload(body, propertyId);
            callPhoto.enqueue(new Callback<PhotoUploadResponse>() {
                @Override
                public void onResponse(Call<PhotoUploadResponse> call, Response<PhotoUploadResponse> response) {
                    if (response.isSuccessful()) {
                        responseP.getPhotos().add(response.body().getId());
                        Log.d("Uploaded", "Éxito");
                        Log.d("Uploaded", response.body().toString());
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
}
