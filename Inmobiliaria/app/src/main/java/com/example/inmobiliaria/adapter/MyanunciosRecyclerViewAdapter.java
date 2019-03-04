package com.example.inmobiliaria.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.fragment.anunciosFragment.OnListFragmentInteractionListener;
import com.example.inmobiliaria.responses.ContainerOneRowResponse;
import com.example.inmobiliaria.responses.PhotoResponse;
import com.example.inmobiliaria.responses.PropertyResponse;
import com.example.inmobiliaria.retrofit.generator.ServiceGenerator;
import com.example.inmobiliaria.retrofit.generator.autentication;
import com.example.inmobiliaria.retrofit.services.PropertyService;
import com.example.inmobiliaria.ui.property.InfoActivity;
import com.example.inmobiliaria.ui.loginsignup.LoginActivity;
import com.example.inmobiliaria.util.UtilToken;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyanunciosRecyclerViewAdapter extends RecyclerView.Adapter<MyanunciosRecyclerViewAdapter.ViewHolder> {
    private PhotoResponse photo;
    private final List<PropertyResponse> mValues;
    private final OnListFragmentInteractionListener mListener;
    Context contexto;
    PropertyService service;
    String jwt;

    public MyanunciosRecyclerViewAdapter(Context ctx, List<PropertyResponse> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
        contexto = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_anuncios, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

    }
    //habia algunos fallos
    /*@Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        jwt = UtilToken.getToken(contexto);
        holder.mItem = mValues.get(position);
        holder.title.setText(mValues.get(position).getTitle());
        holder.price.setText(String.valueOf(Math.round(mValues.get(position).getPrice())) + "€");
        holder.size.setText(String.valueOf(Math.round(mValues.get(position).getSize())) + "/m2");
        holder.city.setText(mValues.get(position).getCity());
        if (holder.mItem.getPhotos().size() != 0) {
            Glide.with(holder.mView).load(holder.mItem.getPhotos().get(0))
                    //.centerCrop()
                    .into(holder.photo);
        } else {
            Glide.with(holder.mView).load("https://rexdalehyundai.ca/dist/img/nophoto.jpg")
                    //.centerCrop()
                    .into(holder.photo);
        }
        holder.mView.setOnClickListener(v -> {
            if (null != mListener) {
                mListener.onListFragmentInteraction(holder.mItem);
            }
        });
        holder.fav.setOnClickListener(v -> {
            int c = 0;
            if (c == 0) {
                if (jwt == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
                    builder.setTitle(R.string.title_add).setMessage(R.string.message_add);
                    builder.setPositiveButton(R.string.go, (dialog, which) ->
                            contexto.startActivity(new Intent(contexto, LoginActivity.class)));
                    builder.setNegativeButton(R.string.cancel, (dialog, id) -> {
                        Log.d("Back", "Going back");
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    service = ServiceGenerator.createService(PropertyService.class, jwt, autentication.JWT);
                    Call<PropertyResponse> call = service.addFav(holder.mItem.getId());
                    call.enqueue(new Callback<PropertyResponse>() {
                        @Override
                        public void onResponse(Call<PropertyResponse> call, Response<PropertyResponse> response) {
                            if (response.code() != 201) {
                                Toast.makeText(contexto, "Error in request", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(contexto, "Added to favourites", Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<PropertyResponse> call, Throwable t) {
                            Toast.makeText(contexto, "Failure", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                c = 1;
                holder.fav.setImageResource(R.drawable.ic_fav);
            } else {
                service = ServiceGenerator.createService(PropertyService.class, jwt, autentication.JWT);
                Call<PropertyResponse> call = service.deleteFav(holder.mItem.getId());
                call.enqueue(new Callback<PropertyResponse>() {
                    @Override
                    public void onResponse(Call<PropertyResponse> call, Response<PropertyResponse> response) {
                        if (response.code() != 200) {
                            Toast.makeText(contexto, "Error in request", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(contexto, "Deleted from favourites", Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<PropertyResponse> call, Throwable t) {
                        Toast.makeText(contexto, "Failure", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            c = 0;
            holder.fav.setImageResource(R.drawable.ic_fav);
        });
        holder.constraintLayout.setOnClickListener(v -> {
            System.out.println(holder.mItem.getId());
            service = ServiceGenerator.createService(PropertyService.class);
            Call<ContainerOneRowResponse<PropertyResponse>> callOne = service.getOne(holder.mItem.getId());
            callOne.enqueue(new Callback<ContainerOneRowResponse<PropertyResponse>>() {
                @Override
                public void onResponse(Call<ContainerOneRowResponse<PropertyResponse>> call, Response<ContainerOneRowResponse<PropertyResponse>> response) {
                    PropertyResponse resp = response.body().getRows();
                    Intent infoActivity = new Intent(contexto , InfoActivity.class);
                    infoActivity.putExtra("property", resp);
                    contexto.startActivity(infoActivity);
                }
                @Override
                public void onFailure(Call<ContainerOneRowResponse<PropertyResponse>> call, Throwable t) {
                }
            });
        });
    }*/

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
        public final ImageView fav;
        public PropertyResponse mItem;
        public ConstraintLayout constraintLayout;
        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = view.findViewById(R.id.title);
            price = view.findViewById(R.id.price_property);
            size = view.findViewById(R.id.size);
            city = view.findViewById(R.id.city);
            photo = view.findViewById(R.id.photo);
            fav = view.findViewById(R.id.favPrincipal);
            constraintLayout = view.findViewById(R.id.constraint);
        }
    }
}
