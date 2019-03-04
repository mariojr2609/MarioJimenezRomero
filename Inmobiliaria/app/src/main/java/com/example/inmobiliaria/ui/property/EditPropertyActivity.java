package com.example.inmobiliaria.ui.property;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.models.EditProperty;
import com.example.inmobiliaria.responses.CategoryResponse;
import com.example.inmobiliaria.responses.ContainerResponse;
import com.example.inmobiliaria.responses.UserResponse;
import com.example.inmobiliaria.retrofit.generator.ServiceGenerator;
import com.example.inmobiliaria.retrofit.generator.autentication;
import com.example.inmobiliaria.retrofit.services.CategoryService;
import com.example.inmobiliaria.retrofit.services.PropertyService;
import com.example.inmobiliaria.util.UtilToken;
import com.example.inmobiliaria.util.geography.GeoCode;
import com.example.inmobiliaria.util.geography.GeographyListener;
import com.example.inmobiliaria.util.geography.GeographySelectorFragment;
import com.example.inmobiliaria.util.geography.GeographySpain;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPropertyActivity extends FragmentActivity implements View.OnClickListener{
    private EditText title, description, price, size, zipcode, address, rooms;
    private String fullAddress, jwt, loc;
    private TextView tvRegion;
    private TextView tvProvincia;
    private TextView tvMunicipio;
    PropertyService service;
    Uri uriSelected;
    UserResponse me;
    private Button btProbar, btnEdit;
    private Spinner categories;
    private List<CategoryResponse> listCategories = new ArrayList<>();
    private FloatingActionButton editPhoto;
    EditProperty property;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);
        jwt = UtilToken.getToken(EditPropertyActivity.this);
        Intent i = getIntent();
        property = (EditProperty) i.getSerializableExtra("property");
        loadItems();
        setItems();
        //btnEdit.setOnClickListener(v -> { editProperty(); });
    }

    public void loadItems() {
        title = findViewById(R.id.title_edit);
        description = findViewById(R.id.description_edit);
        price = findViewById(R.id.price_edit);
        size = findViewById(R.id.size_edit);
        zipcode = findViewById(R.id.zipcode_edit);
        address = findViewById(R.id.address_edit);
        rooms = findViewById(R.id.rooms_edit);
        tvRegion = (TextView) findViewById(R.id.tvRegion);
        tvProvincia = (TextView) findViewById(R.id.tvProvincia);
        tvMunicipio = (TextView) findViewById(R.id.tvMunicipio);
        btProbar = findViewById(R.id.btProbar);
        btProbar.setOnClickListener(this);
        categories = findViewById(R.id.spinner_category);
        loadAllCategories();
        btnEdit = findViewById(R.id.edit_property);
    }

    public void setItems() {
        title.setText(property.getTitle());
        description.setText(property.getDescription());
        price.setText(String.valueOf(property.getPrice()));
        address.setText(property.getAddress());
        size.setText(String.valueOf(property.getSize()));
        zipcode.setText(String.valueOf(property.getZipcode()));
        address.setText(property.getAddress());
        rooms.setText(String.valueOf(property.getRooms()));
    }

    public void loadAllCategories() {
        CategoryService serviceC = ServiceGenerator.createService(CategoryService.class);
        Call<ContainerResponse<CategoryResponse>> callC = serviceC.listCategories();
        callC.enqueue(new Callback<ContainerResponse<CategoryResponse>>() {
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
                    ArrayAdapter<CategoryResponse> adapter =
                            new ArrayAdapter<>(EditPropertyActivity.this, android.R.layout.simple_spinner_dropdown_item, listCategories);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    categories.setAdapter(adapter);
                    categories.setSelection(listCategories.size() - 1);
                } else {
                    Toast.makeText(EditPropertyActivity.this, "Error loading categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContainerResponse<CategoryResponse>> call, Throwable t) {
                Toast.makeText(EditPropertyActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editProperty() {
        fullAddress = "Calle " + address.getText().toString() + ", " + zipcode.getText().toString() + " " + " " + tvProvincia.getText().toString() + ", España";
        try {
            loc = getLoc(fullAddress);
        } catch (IOException e) {
            e.printStackTrace();
        }
        EditProperty edited = property;
        CategoryResponse chosen = (CategoryResponse) categories.getSelectedItem();
        edited.setTitle(title.getText().toString());
        edited.setRooms(Long.parseLong(rooms.getText().toString()));
        edited.setDescription(description.getText().toString());
        edited.setAddress(address.getText().toString());
        edited.setZipcode(zipcode.getText().toString());
        edited.setCity(tvMunicipio.getText().toString());
        edited.setPrice(Long.parseLong(price.getText().toString()));
        edited.setSize(Long.parseLong(size.getText().toString()));
        edited.setProvince(tvProvincia.getText().toString());
        edited.setCategoryId(chosen.getId());
        edited.setLoc(loc);
        editProperty(edited);
    }

    public String getLoc(String fullAddress) throws IOException {
        String loc = GeoCode.getLatLong(EditPropertyActivity.this, fullAddress);
        return loc;
    }

    //@Override
    public void onGeographySelected(Map<String, String> hm) {
        tvRegion.setText(hm.get(GeographySpain.REGION));
        tvProvincia.setText(hm.get(GeographySpain.PROVINCIA));
        tvMunicipio.setText(hm.get(GeographySpain.MUNICIPIO));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btProbar) {
            GeographySelectorFragment gs = new GeographySelectorFragment(EditPropertyActivity.this);
            //gs.setOnGeograpySelectedListener(EditPropertyActivity.this);
            FragmentManager fm = getSupportFragmentManager();
            gs.show(fm, "geographySelector");
        }
    }

    public void editProperty(EditProperty edited) {
        service = ServiceGenerator.createService(PropertyService.class, jwt, autentication.JWT);
        Call<EditProperty> call = service.edit(edited.getId(), edited);
        call.enqueue(new Callback<EditProperty>() {
            @Override
            public void onResponse(Call<EditProperty> call, Response<EditProperty> response) {
                if (response.isSuccessful()) {

                    Toast.makeText(EditPropertyActivity.this, "Created", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(EditPropertyActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<EditProperty> call, Throwable t) {
                Toast.makeText(EditPropertyActivity.this, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
