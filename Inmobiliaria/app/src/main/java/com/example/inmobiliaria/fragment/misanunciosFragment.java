package com.example.inmobiliaria.fragment;

import android.content.Context;
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

import com.example.inmobiliaria.adapter.MymisanunciosRecyclerViewAdapter;
import com.example.inmobiliaria.R;
import com.example.inmobiliaria.responses.ContainerResponse;
import com.example.inmobiliaria.responses.PropertyFavsResponse;
import com.example.inmobiliaria.retrofit.generator.ServiceGenerator;
import com.example.inmobiliaria.retrofit.generator.autentication;
import com.example.inmobiliaria.retrofit.services.PropertyService;
import com.example.inmobiliaria.util.UtilToken;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class misanunciosFragment extends Fragment {
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    Context ctx = getContext();
    List<PropertyFavsResponse> properties = new ArrayList<>();
    String jwt;
    PropertyService service;
    MymisanunciosRecyclerViewAdapter adapter;

    public misanunciosFragment() {
    }

    @SuppressWarnings("unused")
    public static misanunciosFragment newInstance(int columnCount) {
        misanunciosFragment fragment = new misanunciosFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        jwt = UtilToken.getToken(getContext());
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_misanuncios_list, container, false);
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            service = ServiceGenerator.createService(PropertyService.class, jwt, autentication.JWT);
            Call<ContainerResponse<PropertyFavsResponse>> call = service.getMine();
            call.enqueue(new Callback<ContainerResponse<PropertyFavsResponse>>() {
                @Override
                public void onResponse(Call<ContainerResponse<PropertyFavsResponse>> call, Response<ContainerResponse<PropertyFavsResponse>> response) {
                    if (response.code() != 200) {
                        Toast.makeText(getActivity(), "Error in request", Toast.LENGTH_SHORT).show();
                    } else {
                        properties = response.body().getRows();
                        //adapter = new MymisanunciosRecyclerViewAdapter(context, properties, mListener);
                        //recyclerView.setAdapter(adapter);
                    }
                }
                @Override
                public void onFailure(Call<ContainerResponse<PropertyFavsResponse>> call, Throwable t) {
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

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(PropertyFavsResponse item);
    }
}
