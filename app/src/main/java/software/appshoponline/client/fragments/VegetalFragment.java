package software.appshoponline.client.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import software.appshoponline.R;
import software.appshoponline.client.adapters.Product;
import software.appshoponline.client.adapters.ProductAdapter;

public class VegetalFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;

    private ArrayList<Product> ListaProductos;
    private RecyclerView recycler;
    private View root;

    public VegetalFragment() {
        // Required empty public constructor
    }

    public static VegetalFragment newInstance(String param1, String param2) {
        VegetalFragment fragment = new VegetalFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_vegetal, container, false);
        recycler = root.findViewById(R.id.recyclerCardVegetales);
        recycler.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.VERTICAL, false));
        this.ListaProductos = new ArrayList<Product>();

        for(int i=0; i<10; i++){
            ListaProductos.add(new Product());
        }
        ProductAdapter productAdapter = new ProductAdapter(ListaProductos);
        this.recycler.setAdapter(productAdapter);
        return root;
    }
}