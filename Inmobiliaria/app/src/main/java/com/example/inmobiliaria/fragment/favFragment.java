package com.example.inmobiliaria.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.inmobiliaria.adapter.MyanunciosRecyclerViewAdapter;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.responses.ContainerResponse;
import com.example.inmobiliaria.responses.PropertyResponse;
import com.example.inmobiliaria.retrofit.generator.ServiceGenerator;
import com.example.inmobiliaria.retrofit.generator.autentication;
import com.example.inmobiliaria.retrofit.services.PropertyService;
import com.example.inmobiliaria.ui.loginsignup.LoginActivity;
import com.example.inmobiliaria.util.UtilToken;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class favFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    Context ctx = getContext();
    List<PropertyResponse> properties = new ArrayList<>();
    String jwt;
    PropertyService service;
    MyanunciosRecyclerViewAdapter adapter;

    public favFragment() {
    }

    @SuppressWarnings("unused")
    public static favFragment newInstance(int columnCount) {
        favFragment fragment = new favFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jwt = UtilToken.getToken(getContext());
        if (jwt == null) {
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
        }
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fav_list, container, false);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            service = ServiceGenerator.createService(PropertyService.class, jwt, autentication.JWT);
            Call<ContainerResponse<PropertyResponse>> call = service.getFavs();
            call.enqueue(new Callback<ContainerResponse<PropertyResponse>>() {
                @Override
                public void onResponse(Call<ContainerResponse<PropertyResponse>> call, Response<ContainerResponse<PropertyResponse>> response) {
                    if (response.code() != 200) {
                        Toast.makeText(getActivity(), "Error in request", Toast.LENGTH_SHORT).show();
                    } else {
                        properties = response.body().getRows();
                        //adapter = new MyanunciosRecyclerViewAdapter(context, properties, mListener);
                        //recyclerView.setAdapter(adapter);
                    }
                }
                @Override
                public void onFailure(Call<ContainerResponse<PropertyResponse>> call, Throwable t) {
                    Log.e("NetworkFailure", t.getMessage());
                    Toast.makeText(getActivity(), "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            });
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnListFragmentInteractionListener extends anunciosFragment.OnListFragmentInteractionListener {
        void onListFragmentInteraction(PropertyResponse item);
    }
}
