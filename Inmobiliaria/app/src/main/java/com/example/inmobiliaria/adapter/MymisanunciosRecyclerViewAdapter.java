package com.example.inmobiliaria.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.fragment.misanunciosFragment.OnListFragmentInteractionListener;
import com.example.inmobiliaria.models.EditProperty;
import com.example.inmobiliaria.responses.ContainerOneRowResponse;
import com.example.inmobiliaria.responses.PropertyFavsResponse;
import com.example.inmobiliaria.responses.PropertyResponse;
import com.example.inmobiliaria.retrofit.generator.ServiceGenerator;
import com.example.inmobiliaria.retrofit.generator.autentication;
import com.example.inmobiliaria.retrofit.services.PropertyService;
import com.example.inmobiliaria.ui.property.EditPropertyActivity;
import com.example.inmobiliaria.ui.property.InfoActivity;
import com.example.inmobiliaria.util.UtilToken;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MymisanunciosRecyclerViewAdapter extends RecyclerView.Adapter<MymisanunciosRecyclerViewAdapter.ViewHolder> {
    private final List<PropertyFavsResponse> mValues;
    private final OnListFragmentInteractionListener mListener;
    Context contexto;
    String jwt;
    PropertyService service;

    public MymisanunciosRecyclerViewAdapter(Context ctx, List<PropertyFavsResponse> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        contexto = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_misanuncios, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        jwt = UtilToken.getToken(contexto);
        holder.mItem = mValues.get(position);
        holder.title.setText(mValues.get(position).getTitle());
        holder.price.setText(String.valueOf(Math.round(mValues.get(position).getPrice())) + "€");
        holder.size.setText(String.valueOf(Math.round(mValues.get(position).getSize())) + "/m2");
        holder.city.setText(mValues.get(position).getCity());
        if (holder.mItem.getPhotos().size() != 0) {
            Glide.with(holder.mView).load(holder.mItem.getPhotos().get(0))
                    .centerCrop()
                    .into(holder.photo);
        } else {
            Glide.with(holder.mView).load("https://rexdalehyundai.ca/dist/img/nophoto.jpg")
                    .centerCrop()
                    .into(holder.photo);
        }
        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
        holder.edit.setOnClickListener(v -> createEdited(holder));
        holder.delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
            builder.setTitle(R.string.title_add).setMessage(R.string.message_delete);
            builder.setPositiveButton(R.string.go, (dialog, which) ->
                    deleteProperty(holder));
            builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
                Log.d("Back", "Going back");
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });

        holder.constraintLayout.setOnClickListener(v -> {
            System.out.println(holder.mItem.getId());
            service = ServiceGenerator.createService(PropertyService.class);
            Call<ContainerOneRowResponse<PropertyResponse>> callOne = service.getOne(holder.mItem.getId());
            callOne.enqueue(new Callback<ContainerOneRowResponse<PropertyResponse>>() {
                @Override
                public void onResponse(Call<ContainerOneRowResponse<PropertyResponse>> call, Response<ContainerOneRowResponse<PropertyResponse>> response) {
                    PropertyResponse resp = response.body().getRows();
                    Intent detailsActivity = new Intent(contexto, InfoActivity.class);
                    detailsActivity.putExtra("property", resp);
                    contexto.startActivity(detailsActivity);
                }
                @Override
                public void onFailure(Call<ContainerOneRowResponse<PropertyResponse>> call, Throwable t) {
                }
            });
        });
    }

    public void deleteProperty(final ViewHolder holder) {
        String id = holder.mItem.getId();
        System.out.println(id);
        service = ServiceGenerator.createService(PropertyService.class, jwt, autentication.JWT);
        Call<PropertyFavsResponse> callDelete = service.delete(id);
        callDelete.enqueue(new Callback<PropertyFavsResponse>() {
            @Override
            public void onResponse(Call<PropertyFavsResponse> call, Response<PropertyFavsResponse> response) {
                if (response.code() == 200 || response.code() == 204) {
                    Toast.makeText(contexto, "Property deleted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(contexto, "Error while deleting", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<PropertyFavsResponse> call, Throwable t) {
                Toast.makeText(contexto, "Failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void createEdited(final ViewHolder holder) {
        final EditProperty editedDto = new EditProperty();
        System.out.println(holder.mItem.getId());
        service = ServiceGenerator.createService(PropertyService.class);
        Call<ContainerOneRowResponse<PropertyResponse>> callOne = service.getOne(holder.mItem.getId());
        callOne.enqueue(new Callback<ContainerOneRowResponse<PropertyResponse>>() {
            @Override
            public void onResponse(Call<ContainerOneRowResponse<PropertyResponse>> call, Response<ContainerOneRowResponse<PropertyResponse>> response) {
                PropertyResponse resp = response.body().getRows();
                editedDto.setId(holder.mItem.getId());
                editedDto.setAddress(resp.getAddress());
                editedDto.setCategoryId(resp.getCategoryId().getId());
                editedDto.setCity(resp.getCity());
                editedDto.setDescription(resp.getDescription());
                editedDto.setLoc(resp.getLoc());
                editedDto.setOwnerId(holder.mItem.getOwnerId());
                editedDto.setPhotos(resp.getPhotos());
                editedDto.setPrice(resp.getPrice());
                editedDto.setRooms(resp.getRooms());
                editedDto.setProvince(resp.getProvince());
                editedDto.setZipcode(resp.getZipcode());
                editedDto.setSize(resp.getSize());
                editedDto.setTitle(resp.getTitle());
                Intent editPropertyActivity = new Intent(contexto, EditPropertyActivity.class);
                editPropertyActivity.putExtra("property", editedDto);
                contexto.startActivity(editPropertyActivity);
            }
            @Override
            public void onFailure(Call<ContainerOneRowResponse<PropertyResponse>> call, Throwable t) {
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView title;
        public final TextView price;
        public final TextView size;
        public final TextView city;
        public final ImageView photo;
        public final ImageButton edit;
        public final ImageButton delete;
        public final ConstraintLayout constraintLayout;
        public PropertyFavsResponse mItem;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = view.findViewById(R.id.title);
            price = view.findViewById(R.id.price_property);
            size = view.findViewById(R.id.size);
            city = view.findViewById(R.id.city);
            photo = view.findViewById(R.id.photo);
            edit = view.findViewById(R.id.editButton);
            delete = view.findViewById(R.id.deleteButton);
            constraintLayout = view.findViewById(R.id.constraint);
        }
    }
}
